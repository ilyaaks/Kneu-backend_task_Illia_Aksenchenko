package com.kneu.medications.prescription;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
        boolean existsByMedicationIdAndTimeOfDay(Long medicationId, LocalTime timeOfDay);

        @EntityGraph(attributePaths = "medication")
        List<Prescription> findAllByOrderByTimeOfDayAsc();
}