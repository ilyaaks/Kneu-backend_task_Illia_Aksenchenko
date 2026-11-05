package com.kneu.medications.error;

import java.time.LocalTime;

/** Thrown when a prescription for the same medication at the same time already exists. Mapped to 409. */
public class DuplicatePrescriptionException extends RuntimeException {

    public DuplicatePrescriptionException(Long medicationId, LocalTime timeOfDay) {
        super("A prescription for medication " + medicationId + " at " + timeOfDay + " already exists");
    }
}
