package com.kneu.medications.prescription;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePrescriptionRequest(                        // (1)
        @NotNull Long medicationId,                             // (2)
        @NotNull @JsonFormat(pattern = "HH:mm") LocalTime timeOfDay,  // (3)
        @Size(max = 100) String label                           // (4)
) {
    public CreatePrescriptionRequest {                          // (5)
        if (label != null) {
            label = label.strip();                              // (6)
            if (label.isEmpty()) {
                label = null;                                   // (7)
            }
        }
    }
}