package com.kneu.medications;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicationRepository medicationRepository;

    @Test
    void getAllMedications() throws Exception {
        medicationRepository.save(new Medication("Aspirin", "100mg"));
        medicationRepository.save(new Medication("Metformin", "500mg"));

        mockMvc.perform(get("/medications"))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                [
                    {"id": 1, "name": "Aspirin", "dosage": "100mg"},
                    {"id": 2, "name": "Metformin", "dosage": "500mg"}
                ]
                """));
    }
}
