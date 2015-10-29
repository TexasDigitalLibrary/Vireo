package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.tdl.vireo.enums.RecipientType;

public class EmailWorkflowRuleTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertTrue("The repository was not empty!", emailWorkflowRuleRepo.count() == 0);
        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        emailTemplate = emailTemplateRepo.create("Important Notification", "A Marvelous Submission", "Be it known to ye that this submission is marvelous.");
    }

    @Override
    public void testCreate() {
        EmailWorkflowRule notifyEverybodyOfImportantDoings = emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);
        assertTrue("We didn't have enough email workflow rules in the repo!", emailWorkflowRuleRepo.count() == 1);
        assertTrue("We didn't have the right submissionState on our rule!", notifyEverybodyOfImportantDoings.getSubmissionState().equals(submissionState));
        assertTrue("We didn't have the right recipient type on our rule!", notifyEverybodyOfImportantDoings.getRecipientType().equals(RecipientType.DEPARTMENT));
        assertTrue("We didn't have the right template on our rule!", notifyEverybodyOfImportantDoings.getEmailTemplate().equals(emailTemplate));
    }

    @Override
    public void testDuplication() {
        emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);
        emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);

        assertTrue("Duplicated!", emailWorkflowRuleRepo.count() == 2);
    }

    @Override
    public void testDelete() {
        EmailWorkflowRule ruleToDelete = emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);
        assertEquals("Didn't create the rule!", 1, emailWorkflowRuleRepo.count());
        emailWorkflowRuleRepo.delete(ruleToDelete);
        assertEquals("Didn't delete the rule!", 0, emailWorkflowRuleRepo.count());
    }

    @Override
    public void testCascade() {
        EmailWorkflowRule ruleToCascade = emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);
        emailWorkflowRuleRepo.delete(ruleToCascade);
        assertEquals("Submission State is deleted", 1, submissionStateRepo.count());
        assertEquals("Email Template is deleted", 1, emailTemplateRepo.count());
    }

    @After
    public void cleanup() {
        emailWorkflowRuleRepo.deleteAll();
        submissionStateRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }

}
