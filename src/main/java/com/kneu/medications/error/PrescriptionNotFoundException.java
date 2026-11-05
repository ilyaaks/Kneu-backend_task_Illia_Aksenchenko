package com.kneu.medications.error;

/** Thrown when a prescription id in the URL does not exist. Mapped to 404. */
public class PrescriptionNotFoundException extends RuntimeException {

    public PrescriptionNotFoundException(Long id) {
        super("Prescription with id " + id + " does not exist");
    }
}
