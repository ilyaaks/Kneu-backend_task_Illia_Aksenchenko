package com.kneu.medications.error;

/** Thrown when a request references a medication id that does not exist. Mapped to 422. */
public class MedicationNotFoundException extends RuntimeException {

    public MedicationNotFoundException(Long medicationId) {
        super("Medication with id " + medicationId + " does not exist");
    }
}
