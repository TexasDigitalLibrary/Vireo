package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;

public class EmailWorkflowRuleRepoTest extends AbstractRepoTest {

    @BeforeEach
    public void setUp() {
        assertTrue(emailWorkflowRuleRepo.count() == 0, "The repository was not empty!");
        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);
        emailTemplate = emailTemplateRepo.create("Important Notification", "A Marvelous Submission", "Be it known to ye that this submission is marvelous.");
        emailRecipient = emailRecipientRepo.createSubmitterRecipient();
    }

    @Override
    @Test
    public void testCreate() {
        EmailWorkflowRuleByStatus notifyEverybodyOfImportantDoings = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        assertTrue(emailWorkflowRuleRepo.count() == 1, "We didn't have enough email workflow rules in the repo!");
        assertTrue(notifyEverybodyOfImportantDoings.getSubmissionStatus().equals(submissionStatus), "We didn't have the right submissionStatus on our rule!");
        assertTrue(notifyEverybodyOfImportantDoings.getEmailRecipient().equals(emailRecipient), "We didn't have the right recipient type on our rule!");
        assertTrue(notifyEverybodyOfImportantDoings.getEmailTemplate().equals(emailTemplate), "We didn't have the right template on our rule!");
    }

    @Override
    @Test
    public void testDuplication() {
        emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);

        assertTrue(emailWorkflowRuleRepo.count() == 2, "Duplicated!");
    }

    @Override
    @Test
    public void testDelete() {
        EmailWorkflowRuleByStatus ruleToDelete = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        assertEquals(1, emailWorkflowRuleRepo.count(), "Didn't create the rule!");
        emailWorkflowRuleRepo.delete(ruleToDelete);
        assertEquals(0, emailWorkflowRuleRepo.count(), "Didn't delete the rule!");
    }

    @Override
    @Test
    public void testCascade() {
        EmailWorkflowRuleByStatus ruleToCascade = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
        emailWorkflowRuleRepo.delete(ruleToCascade);
        assertEquals(1, submissionStatusRepo.count(), "Submission State is deleted");
        assertEquals(1, emailTemplateRepo.count(), "Email Template is deleted");
    }

    @AfterEach
    public void cleanup() {
        emailWorkflowRuleRepo.deleteAll();
        submissionStatusRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }

}
