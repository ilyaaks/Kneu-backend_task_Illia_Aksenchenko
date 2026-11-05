package com.kneu.medications.prescription;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kneu.medications.Medication;
import com.kneu.medications.MedicationRepository;
import com.kneu.medications.error.DuplicatePrescriptionException;
import com.kneu.medications.error.MedicationNotFoundException;
import com.kneu.medications.error.PrescriptionNotFoundException;

@Service                                                            
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;    
    private final MedicationRepository medicationRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               MedicationRepository medicationRepository) {   
        this.prescriptionRepository = prescriptionRepository;
        this.medicationRepository = medicationRepository;
    }

    @Transactional                                                  
    public PrescriptionResponse create(CreatePrescriptionRequest request) {
        Medication medication = medicationRepository.findById(request.medicationId())   
                .orElseThrow(() -> new MedicationNotFoundException(request.medicationId()));

        if (prescriptionRepository.existsByMedicationIdAndTimeOfDay(
                medication.getId(), request.timeOfDay())) {         
            throw new DuplicatePrescriptionException(medication.getId(), request.timeOfDay());
        }

        Prescription saved = prescriptionRepository.save(           
                new Prescription(medication, request.timeOfDay(), request.label()));

        return PrescriptionResponse.from(saved);                    
    }

    @Transactional(readOnly = true)                                 
    public List<PrescriptionResponse> findAll() {
        return prescriptionRepository.findAllByOrderByTimeOfDayAsc()
                .stream()                                          
                .map(PrescriptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse findById(Long id) {
        return prescriptionRepository.findById(id)                 
                .map(PrescriptionResponse::from)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));
    }
}
