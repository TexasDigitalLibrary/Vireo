package org.tdl.vireo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;

public class EmailWorkflowRulesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private AbstractEmailRecipientRepo abstractEmailRecipientRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @BeforeEach
    public void setup() throws Exception {
        // takes a long time, try not to add more tests in this class
        systemDataLoader.loadSystemData();
        entityControlledVocabularyService.scanForEntityControlledVocabularies();
    }

    @Test
    public void testEmailWorkflowRulePersistenceUponReload() throws Exception {
        Organization org = organizationRepo.findAll().get(0);

        assertEquals(2, org.getEmailWorkflowRules().size());

        SubmissionStatus submissionStatus = submissionStatusRepo.findAll().get(0);

        EmailTemplate emailTemplate = emailTemplateRepo.findAll().get(0);

        EmailRecipient emailRecipient = abstractEmailRecipientRepo.findAll().get(0);

        EmailWorkflowRule newEmailWorkflowRule = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        org.addEmailWorkflowRule(newEmailWorkflowRule);
        organizationRepo.update(org);

        Long existingOrgWithNewWorkflowRuleId = org.getId();
        Long newlyCreatedEmailWorkflowRuleId = newEmailWorkflowRule.getId();

        int numberOfWorkflowRulesForOrg = org.getEmailWorkflowRules().size();

        assertEquals(3, numberOfWorkflowRulesForOrg);

        this.setup();

        EmailWorkflowRule newWorkflowRule = emailWorkflowRuleRepo.getById(newlyCreatedEmailWorkflowRuleId);
        assertNotNull(newWorkflowRule);
        Organization newOrgRef = organizationRepo.getById(existingOrgWithNewWorkflowRuleId);
        assertNotNull(newOrgRef);

        List<EmailWorkflowRule> emailWorkflowRulesForOrg = newOrgRef.getEmailWorkflowRules();

        assertEquals(numberOfWorkflowRulesForOrg, emailWorkflowRulesForOrg.size());

        EmailWorkflowRule newEmailWorkflowRuleRef = emailWorkflowRulesForOrg.get(emailWorkflowRulesForOrg.size() - 1);

        assertEquals(newlyCreatedEmailWorkflowRuleId, newEmailWorkflowRuleRef.getId());
    }

    @AfterEach
    public void cleanup() {
        defaultFiltersService.getDefaultFilter().clear();
        defaultSubmissionListColumnService.getDefaultSubmissionListColumns().clear();
    }

}
