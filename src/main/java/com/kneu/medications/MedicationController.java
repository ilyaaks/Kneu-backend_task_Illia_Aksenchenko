package com.kneu.medications;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicationController {

    private final MedicationRepository repository;

    public MedicationController(MedicationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/medications")
    public List<Medication> getAllMedications() {
        return repository.findAll();
    }
}
