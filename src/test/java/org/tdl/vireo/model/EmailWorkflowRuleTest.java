package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;

public class EmailWorkflowRuleTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertTrue("The repository was not empty!", emailWorkflowRuleRepo.count() == 0);
        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);
        emailTemplate = emailTemplateRepo.create("Important Notification", "A Marvelous Submission", "Be it known to ye that this submission is marvelous.");
        emailRecipient = emailRecipientRepo.createSubmitterRecipient();
    }

    @Override
    public void testCreate() {
        EmailWorkflowRule notifyEverybodyOfImportantDoings = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        assertTrue("We didn't have enough email workflow rules in the repo!", emailWorkflowRuleRepo.count() == 1);
        assertTrue("We didn't have the right submissionStatus on our rule!", notifyEverybodyOfImportantDoings.getSubmissionStatus().equals(submissionStatus));
        assertTrue("We didn't have the right recipient type on our rule!", notifyEverybodyOfImportantDoings.getEmailRecipient().equals(emailRecipient));
        assertTrue("We didn't have the right template on our rule!", notifyEverybodyOfImportantDoings.getEmailTemplate().equals(emailTemplate));
    }

    @Override
    public void testDuplication() {
        emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);

        assertTrue("Duplicated!", emailWorkflowRuleRepo.count() == 2);
    }

    @Override
    public void testDelete() {
        EmailWorkflowRule ruleToDelete = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        assertEquals("Didn't create the rule!", 1, emailWorkflowRuleRepo.count());
        emailWorkflowRuleRepo.delete(ruleToDelete);
        assertEquals("Didn't delete the rule!", 0, emailWorkflowRuleRepo.count());
    }

    @Override
    public void testCascade() {
        EmailWorkflowRule ruleToCascade = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        emailWorkflowRuleRepo.delete(ruleToCascade);
        assertEquals("Submission State is deleted", 1, submissionStatusRepo.count());
        assertEquals("Email Template is deleted", 1, emailTemplateRepo.count());
    }

    @After
    public void cleanup() {
        emailWorkflowRuleRepo.deleteAll();
        submissionStatusRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }

}
