package com.kneu.medications.prescription;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kneu.medications.Medication;
import com.kneu.medications.MedicationRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // each test is rolled back, so tests don't see each other's data
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicationRepository medicationRepository;

    private Medication aspirin;

    @BeforeEach
    void setUp() {
        // H2 starts empty (init.sql only runs against Postgres), so create a medication to reference.
        aspirin = medicationRepository.save(new Medication("Aspirin", "100mg"));
    }

    private ResultActions postPrescription(String json) throws Exception {
        return mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    @Test
    void createReturns201AndCanBeFetchedById() throws Exception {
        String location = postPrescription("""
                {"medicationId": %d, "timeOfDay": "08:00", "label": "  with breakfast  "}
                """.formatted(aspirin.getId()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.medication.name").value("Aspirin"))
                .andExpect(jsonPath("$.timeOfDay").value("08:00"))
                .andExpect(jsonPath("$.label").value("with breakfast")) // trimmed
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medication.name").value("Aspirin"));
    }

    @Test
    void listReturnsPrescriptionsSortedByTime() throws Exception {
        postPrescription("""
                {"medicationId": %d, "timeOfDay": "20:00"}
                """.formatted(aspirin.getId()));
        postPrescription("""
                {"medicationId": %d, "timeOfDay": "07:30"}
                """.formatted(aspirin.getId()));

        mockMvc.perform(get("/prescriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].timeOfDay").value("07:30"))
                .andExpect(jsonPath("$[1].timeOfDay").value("20:00"));
    }

    @Test
    void createWithMissingFieldsReturns400() throws Exception {
        postPrescription("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.medicationId").exists())
                .andExpect(jsonPath("$.errors.timeOfDay").exists());
    }

    @Test
    void createWithInvalidTimeReturns400() throws Exception {
        postPrescription("""
                {"medicationId": %d, "timeOfDay": "25:00"}
                """.formatted(aspirin.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithUnknownMedicationReturns422() throws Exception {
        postPrescription("""
                {"medicationId": 999999, "timeOfDay": "08:00"}
                """)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createDuplicateReturns409() throws Exception {
        String json = """
                {"medicationId": %d, "timeOfDay": "08:00"}
                """.formatted(aspirin.getId());

        postPrescription(json).andExpect(status().isCreated());
        postPrescription(json).andExpect(status().isConflict());
    }

    @Test
    void getUnknownIdReturns404() throws Exception {
        mockMvc.perform(get("/prescriptions/999999"))
                .andExpect(status().isNotFound());
    }
}
