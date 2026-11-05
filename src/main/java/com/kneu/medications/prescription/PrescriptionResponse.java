package com.kneu.medications.prescription;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kneu.medications.Medication;

public record PrescriptionResponse(
        Long id,
        MedicationSummary medication,                           // (1)
        @JsonFormat(pattern = "HH:mm") LocalTime timeOfDay,     // (2)
        String label
) {
    public record MedicationSummary(Long id, String name, String dosage) {  // (3)
    }

    public static PrescriptionResponse from(Prescription p) {   // (4)
        Medication m = p.getMedication();
        return new PrescriptionResponse(
                p.getId(),
                new MedicationSummary(m.getId(), m.getName(), m.getDosage()),  // (5)
                p.getTimeOfDay(),
                p.getLabel());
    }
}
