package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsExcception;

public class ActionLogTest extends AbstractEntityTest {

    @Before
    public void setUp() throws OrganizationDoesNotAcceptSubmissionsExcception {

        assertEquals("The actionLog repository is not empty!", 0, actionLogRepo.count());
        testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        assertEquals("The user repository is not empty!", 1, userRepo.count());
        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);
        assertEquals("The submissionStatus repository is not empty!", 1, submissionStatusRepo.count());

        parentCategory = organizationCategoryRepo.create(TEST_ORGANIZATION_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);

        testSubmission = submissionRepo.create(testUser, organization, submissionStatus, getCredentials());

        assertEquals("The submission repository is not empty!", 1, submissionRepo.count());
    }

    @Override
    public void testCreate() {
        ActionLog testActionLog = actionLogRepo.create(testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        assertEquals("The actionLog repository is not empty!", 1, actionLogRepo.count());
        assertEquals("The test Action log was not saved", 1, actionLogRepo.count());
        assertEquals("Saved action log does not have the correct submitter email", TEST_USER_EMAIL, testActionLog.getUser().getEmail());
        assertEquals("Saved action log does not have the correct submitter first name", TEST_USER_FIRSTNAME, testActionLog.getUser().getFirstName());
        assertEquals("Saved action log does not have the correct submitter last name", TEST_USER_LASTNAME, testActionLog.getUser().getLastName());
        assertEquals("Saved action log does not have the correct submitter role", TEST_USER_ROLE, testActionLog.getUser().getRole());
        assertEquals("Saved action log does not have the correct submission state name", TEST_SUBMISSION_STATUS_NAME, testActionLog.getSubmissionStatus().getName());
        assertEquals("Saved action log does not have the correct submission archived state ", TEST_SUBMISSION_STATUS_ARCHIVED, testActionLog.getSubmissionStatus().isArchived());
        assertEquals("Saved action log does not have the correct submission publishable state ", TEST_SUBMISSION_STATUS_PUBLISHABLE, testActionLog.getSubmissionStatus().isPublishable());
        assertEquals("Saved action log does not have the correct submission deletable state ", TEST_SUBMISSION_STATUS_DELETABLE, testActionLog.getSubmissionStatus().isDeletable());
        assertEquals("Saved action log does not have the correct submission editable by reviewer", TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, testActionLog.getSubmissionStatus().isEditableByReviewer());
        assertEquals("Saved action log does not have the correct submission editable by  student state ", TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, testActionLog.getSubmissionStatus().isEditableByStudent());
        assertEquals("Saved action log does not have the correct submission  active state ", TEST_SUBMISSION_STATUS_ACTIVE, testActionLog.getSubmissionStatus().isActive());
        assertEquals("Saved action log does not have the correct log entry ", TEST_ACTION_LOG_ENTRY, testActionLog.getEntry());
        assertEquals("Saved action log does not have the correct flag ", TEST_ACTION_LOG_FLAG, testActionLog.isPrivateFlag());
    }

    @Override
    public void testDuplication() {
        actionLogRepo.create(testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        actionLogRepo.create(testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        assertEquals("The action log entry was not duplicated", 2, actionLogRepo.count());
    }

    @Override
    public void testDelete() {
        ActionLog testActionLog = actionLogRepo.create(testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        actionLogRepo.delete(testActionLog);
        assertEquals("The test action log was not deleted!", 0, actionLogRepo.count());
    }

    @Override
    public void testCascade() {
        ActionLog testActionLog = actionLogRepo.create(testSubmission, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
        actionLogRepo.delete(testActionLog);
        assertEquals("The test action log was not deleted!", 0, actionLogRepo.count());
        assertEquals("Submission is not deleted", 1, submissionRepo.count());
        assertEquals("Submission State is not deleted", 1, submissionStatusRepo.count());
        assertEquals("User is not deleted", 1, userRepo.count());
    }

    @After
    public void cleanUp() {
        actionLogRepo.findAll().forEach(actionLog -> {
            actionLogRepo.delete(actionLog);
        });
        submissionRepo.deleteAll();
        submissionStatusRepo.deleteAll();
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        namedSearchFilterGroupRepo.findAll().forEach(nsf -> {
            namedSearchFilterGroupRepo.delete(nsf);
        });
        userRepo.deleteAll();
    }

}
