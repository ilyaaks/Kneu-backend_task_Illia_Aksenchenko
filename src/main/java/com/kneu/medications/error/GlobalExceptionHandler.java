package com.kneu.medications.error;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

/**
 * Turns exceptions thrown anywhere in a request into consistent RFC 9457 "problem detail" JSON responses.
 *
 * Extending ResponseEntityExceptionHandler means Spring's own exceptions (malformed JSON, bad path variables,
 * unsupported media type, ...) are already mapped to the right 4xx status in the same format; we only add
 * our domain exceptions and enrich the validation responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- Domain exceptions (thrown by PrescriptionService) ----------

    /** The body is well-formed, but refers to a medication that doesn't exist: 422, not 404 (the URL is fine). */
    @ExceptionHandler(MedicationNotFoundException.class)
    public ProblemDetail handleMedicationNotFound(MedicationNotFoundException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Medication not found", ex.getMessage());
    }

    @ExceptionHandler(PrescriptionNotFoundException.class)
    public ProblemDetail handlePrescriptionNotFound(PrescriptionNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Prescription not found", ex.getMessage());
    }

    @ExceptionHandler(DuplicatePrescriptionException.class)
    public ProblemDetail handleDuplicatePrescription(DuplicatePrescriptionException ex) {
        return problem(HttpStatus.CONFLICT, "Duplicate prescription", ex.getMessage());
    }

    /**
     * Safety net: the service checks for duplicates first, but two identical requests at the same moment can
     * both pass that check. The database's unique constraint then rejects the second insert.
     * The raw database message is logged but not returned, because it would leak table and constraint names.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "Conflict", "The request conflicts with existing data");
    }

    /** Anything unexpected: log the full stack trace for us, return a generic 500 without internals. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred");
    }

    // ---------- Spring's built-in exceptions, enriched ----------

    /** @Valid failed (@NotNull, @Size, ...): list every invalid field so the client can fix them all at once. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail body = problem(status, "Validation failed", "One or more fields are invalid");
        body.setProperty("errors", errors);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    /** The body couldn't be parsed: malformed JSON, or a value of the wrong type/format (e.g. "timeOfDay": "25:00"). */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail body = problem(status, "Malformed request", "The request body could not be read as valid JSON");

        // If Jackson knows which field it couldn't convert, say so.
        if (ex.getCause() instanceof MismatchedInputException cause && !cause.getPath().isEmpty()) {
            List<JsonMappingException.Reference> path = cause.getPath();
            String field = path.get(path.size() - 1).getFieldName();
            if (field != null) {
                body.setDetail("Invalid value for field '" + field + "'");
                body.setProperty("errors", Map.of(field, "invalid value or format"));
            }
        }
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    private static ProblemDetail problem(HttpStatusCode status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
