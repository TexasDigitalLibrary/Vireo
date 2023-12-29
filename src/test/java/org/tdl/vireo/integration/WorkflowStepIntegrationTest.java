package org.tdl.vireo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;

import com.fasterxml.jackson.databind.JsonNode;

public class WorkflowStepIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private InputTypeRepo inputTypeRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @BeforeEach
    public void setup() throws Exception {
        // takes a long time, try not to add more tests in this class or switch to static @BeforeAll
        systemDataLoader.loadSystemData();

        entityControlledVocabularyService.scanForEntityControlledVocabularies();

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser(roles = { "MANAGER" })
    public void testSubmissionListColumnRemovedWhenRemovingFieldProfileFromWorkflowStep() throws Exception {
        Organization org = organizationRepo.findAll().get(0);

        assertEquals(4, org.getAggregateWorkflowSteps().size());

        WorkflowStep workflowStep = org.getAggregateWorkflowSteps().get(0);

        assertEquals(18, workflowStep.getAggregateFieldProfiles().size());

        InputType inputType = inputTypeRepo.findByName("INPUT_TEXT");

        assertNotNull(inputType.getName());

        long initialFieldPredicateCount = fieldPredicateRepo.count();
        long initialFieldProfileCount = fieldProfileRepo.count();
        long initialSubmissionListColumnCount = submissionListColumnRepo.count();

        Long orgId = org.getId();
        Long wsId = workflowStep.getId();


        // create field predicate

        FieldPredicate fieldPredicate = new FieldPredicate("test", false);

        MvcResult results = mockMvc.perform(post("/settings/field-predicates/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.convertValue(fieldPredicate, JsonNode.class).toString().getBytes("utf-8")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.meta.status").value("SUCCESS"))
                .andExpect(jsonPath("$.payload.FieldPredicate").exists())
                .andExpect(jsonPath("$.payload.FieldPredicate.id").isNumber())
                .andExpect(jsonPath("$.payload.FieldPredicate.value").value("test"))
                .andReturn();

        Long fieldPredicateId = objectMapper.readTree(results.getResponse().getContentAsString())
            .get("payload")
            .get("FieldPredicate")
            .get("id")
            .asLong();

        fieldPredicate.setId(fieldPredicateId);

        assertEquals(initialFieldPredicateCount + 1, fieldPredicateRepo.count());
        assertTrue(fieldPredicateRepo.existsById(fieldPredicateId));

        // add field profile to workflow step

        FieldProfile fieldProfile = new FieldProfile(workflowStep, fieldPredicate, inputType, "Test", false, true, true, false, false, false, null);

        results = mockMvc.perform(post("/workflow-step/{requestingOrgId}/{workflowStepId}/add-field-profile", orgId, wsId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.convertValue(fieldProfile, JsonNode.class).toString().getBytes("utf-8")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.meta.status").value("SUCCESS"))
                .andExpect(jsonPath("$.payload.FieldProfile").exists())
                .andExpect(jsonPath("$.payload.FieldProfile.id").isNumber())
                .andExpect(jsonPath("$.payload.FieldProfile.gloss").value("Test"))
                .andReturn();

        Long fpId = objectMapper.readTree(results.getResponse().getContentAsString())
            .get("payload")
            .get("FieldProfile")
            .get("id")
            .asLong();

        assertEquals(initialFieldProfileCount + 1, fieldProfileRepo.count());
        assertTrue(fieldProfileRepo.existsById(fpId));

        assertEquals(initialSubmissionListColumnCount + 1, submissionListColumnRepo.count());
        assertNotNull(submissionListColumnRepo.findByTitle("Test"));

        // remove field profile and assert submission list column not orphaned

        mockMvc.perform(post("/workflow-step/{requestingOrgId}/{workflowStepId}/remove-field-profile/{fieldProfileId}", orgId, wsId, fpId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.convertValue(fieldProfile, JsonNode.class).toString().getBytes("utf-8")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.meta.status").value("SUCCESS"));

        assertEquals(initialFieldProfileCount, fieldProfileRepo.count());
        assertFalse(fieldProfileRepo.existsById(fpId));

        assertEquals(initialSubmissionListColumnCount, submissionListColumnRepo.count());
        assertNull(submissionListColumnRepo.findByTitle("Test"));
    }

}
