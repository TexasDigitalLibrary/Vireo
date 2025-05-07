package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ActionLogRepoTest extends AbstractRepoTest {

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @BeforeEach
    public void setUp() throws OrganizationDoesNotAcceptSubmissionsException {

        assertEquals(0, actionLogRepo.count(), "The actionLog repository is not empty!");
        testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        assertEquals(1, userRepo.count(), "The user repository is not empty!");
        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);
        assertEquals(1, submissionStatusRepo.count(), "The submissionStatus repository is not empty!");

        parentCategory = organizationCategoryRepo.create(TEST_ORGANIZATION_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);

        testSubmission = submissionRepo.create(testUser, organization, submissionStatus, getCredentials(), customActionDefinitionRepo.findAll());

        assertEquals(1, submissionRepo.count(), "The submission repository is not empty!");
    }

    @Transactional
    @Override
    @Test
    public void testCreate() {
        ActionLog testActionLog = actionLogRepo.create(Action.UNDETERMINED, testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        assertEquals(1, actionLogRepo.count(), "The actionLog repository is not empty!");
        assertEquals(1, actionLogRepo.count(), "The test Action log was not saved");
        assertEquals(TEST_USER_EMAIL, testActionLog.getUser().getEmail(), "Saved action log does not have the correct submitter email");
        assertEquals(TEST_USER_FIRSTNAME, testActionLog.getUser().getFirstName(), "Saved action log does not have the correct submitter first name");
        assertEquals(TEST_USER_LASTNAME, testActionLog.getUser().getLastName(), "Saved action log does not have the correct submitter last name");
        assertEquals(TEST_USER_ROLE, testActionLog.getUser().getRole(), "Saved action log does not have the correct submitter role");
        assertEquals(TEST_SUBMISSION_STATUS_NAME, testActionLog.getSubmissionStatus().getName(), "Saved action log does not have the correct submission state name");
        assertEquals(TEST_SUBMISSION_STATUS_ARCHIVED, testActionLog.getSubmissionStatus().isArchived(), "Saved action log does not have the correct submission archived state ");
        assertEquals(TEST_SUBMISSION_STATUS_PUBLISHABLE, testActionLog.getSubmissionStatus().isPublishable(), "Saved action log does not have the correct submission publishable state ");
        assertEquals(TEST_SUBMISSION_STATUS_DELETABLE, testActionLog.getSubmissionStatus().isDeletable(), "Saved action log does not have the correct submission deletable state ");
        assertEquals(TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, testActionLog.getSubmissionStatus().isEditableByReviewer(), "Saved action log does not have the correct submission editable by reviewer");
        assertEquals(TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, testActionLog.getSubmissionStatus().isEditableByStudent(), "Saved action log does not have the correct submission editable by  student state ");
        assertEquals(TEST_SUBMISSION_STATUS_ACTIVE, testActionLog.getSubmissionStatus().isActive(), "Saved action log does not have the correct submission  active state ");
        assertEquals(TEST_ACTION_LOG_ENTRY, testActionLog.getEntry(), "Saved action log does not have the correct log entry ");
        assertEquals(TEST_ACTION_LOG_FLAG, testActionLog.isPrivateFlag(), "Saved action log does not have the correct flag ");
    }

    @Transactional
    @Override
    @Test
    public void testDuplication() {
        actionLogRepo.create(Action.UNDETERMINED, testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        actionLogRepo.create(Action.UNDETERMINED, testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        assertEquals(2, actionLogRepo.count(), "The action log entry was not duplicated");
    }

    @Transactional
    @Override
    @Test
    public void testDelete() {
        ActionLog testActionLog = actionLogRepo.create(Action.UNDETERMINED, testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        actionLogRepo.delete(testActionLog);
        assertEquals(0, actionLogRepo.count(), "The test action log was not deleted!");
    }

    @Transactional
    @Override
    @Test
    public void testCascade() {
        ActionLog testActionLog = actionLogRepo.create(Action.UNDETERMINED, testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        actionLogRepo.delete(testActionLog);
        assertEquals(0, actionLogRepo.count(), "The test action log was not deleted!");
        assertEquals(1, submissionRepo.count(), "Submission is not deleted");
        assertEquals(1, submissionStatusRepo.count(), "Submission State is not deleted");
        assertEquals(1, userRepo.count(), "User is not deleted");
    }

}
