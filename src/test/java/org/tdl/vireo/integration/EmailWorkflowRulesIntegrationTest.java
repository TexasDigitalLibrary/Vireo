package org.tdl.vireo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;

import com.fasterxml.jackson.databind.JsonNode;

public class EmailWorkflowRulesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @BeforeEach
    public void setup() throws Exception {
        // takes a long time, try not to add more tests in this class or switch to static @BeforeAll
        systemDataLoader.loadSystemData();
        entityControlledVocabularyService.scanForEntityControlledVocabularies();

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser(roles = { "MANAGER" })
    public void testEmailWorkflowRulePersistenceUponReload() throws Exception {
        Organization org = organizationRepo.findAll().get(0);

        assertEquals(2, org.getEmailWorkflowRules().size());

        SubmissionStatus submissionStatus = submissionStatusRepo.findAll().get(0);

        EmailTemplate emailTemplate = emailTemplateRepo.findAll().get(0);

        Long existingOrgWithNewWorkflowRuleId = org.getId();
        Long submissionStatusId = submissionStatus.getId();
        Long emailTemplateId = emailTemplate.getId();

        JsonNode recipientNode = objectMapper.createObjectNode()
            .put("type", "SUBMITTER");

        Map<String, Object> data = new HashMap<>();
        data.put("recipient", recipientNode);
        data.put("submissionStatusId", submissionStatusId);
        data.put("templateId", emailTemplateId);

        MvcResult results = mockMvc.perform(post("/organization/{requestingOrgId}/add-email-workflow-rule", org.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.convertValue(data, JsonNode.class).toString().getBytes("utf-8"))
            )
            .andExpect(status().isOk()).andExpect(jsonPath("$.meta.status").value("SUCCESS"))
            .andExpect(jsonPath("$.payload.id").isNumber())
            .andReturn();

        Long newlyCreatedEmailWorkflowRuleId = objectMapper.readTree(results.getResponse().getContentAsString())
            .get("payload")
            .get("id")
            .asLong();

        assertEquals(3, org.getEmailWorkflowRules().size());

        // reload system data
        systemDataLoader.loadSystemData();

        EmailWorkflowRuleByStatus newWorkflowRule = emailWorkflowRuleRepo.getById(newlyCreatedEmailWorkflowRuleId);
        assertNotNull(newWorkflowRule);
        Organization newOrgRef = organizationRepo.getById(existingOrgWithNewWorkflowRuleId);
        assertNotNull(newOrgRef);

        List<EmailWorkflowRuleByStatus> emailWorkflowRulesForOrg = newOrgRef.getEmailWorkflowRules();

        assertEquals(3, emailWorkflowRulesForOrg.size());

        EmailWorkflowRuleByStatus newEmailWorkflowRuleRef = emailWorkflowRulesForOrg.get(emailWorkflowRulesForOrg.size() - 1);

        assertEquals(newlyCreatedEmailWorkflowRuleId, newEmailWorkflowRuleRef.getId());
    }

}
