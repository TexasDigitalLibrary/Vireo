package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class SubmissionStatusTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        SubmissionStatus parentSubmissionState = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, TEST_SUBMISSION_STATUS_DEFAULT, null);
        SubmissionStatus transitionSubmissionState = submissionStatusRepo.create(TEST_TRANSITION1_SUBMISSION_STATUS_NAME, TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, TEST_TRANSITION_SUBMISSION_STATUS_DEFAULT, null);
        parentSubmissionState.addTransitionSubmissionStatus(transitionSubmissionState);
        submissionStatusRepo.save(parentSubmissionState);

        assertEquals("The submission state does not exist!", 2, submissionStatusRepo.count());

        assertEquals("The submission state did not contain the correct name!", TEST_PARENT_SUBMISSION_STATUS_NAME, parentSubmissionState.getName());
        assertEquals("The submission state did not contain the correct archived!", TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, parentSubmissionState.isArchived());
        assertEquals("The submission state did not contain the correct publishable!", TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, parentSubmissionState.isPublishable());
        assertEquals("The submission state did not contain the correct deletable!", TEST_PARENT_SUBMISSION_STATUS_DELETABLE, parentSubmissionState.isDeletable());
        assertEquals("The submission state did not contain the correct editable by reviewer!", TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, parentSubmissionState.isEditableByReviewer());
        assertEquals("The submission state did not contain the correct editable by student!", TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, parentSubmissionState.isEditableByStudent());
        assertEquals("The submission state did not contain the correct active!", TEST_PARENT_SUBMISSION_STATUS_ACTIVE, parentSubmissionState.isActive());
        assertEquals("The submission state did not contain the correct default!", TEST_PARENT_SUBMISSION_STATUS_DEFAULT, parentSubmissionState.isDefault());

        assertEquals("The submission state did not contain the correct transition submission step!", transitionSubmissionState, (parentSubmissionState.getTransitionSubmissionStatuses().toArray()[0]));

        assertEquals("The submission state did not contain the correct name!", TEST_TRANSITION1_SUBMISSION_STATUS_NAME, transitionSubmissionState.getName());
        assertEquals("The submission state did not contain the correct archived!", TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, transitionSubmissionState.isArchived());
        assertEquals("The submission state did not contain the correct publishable!", TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, transitionSubmissionState.isPublishable());
        assertEquals("The submission state did not contain the correct deletable!", TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, transitionSubmissionState.isDeletable());
        assertEquals("The submission state did not contain the correct editable by reviewer!", TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, transitionSubmissionState.isEditableByReviewer());
        assertEquals("The submission state did not contain the correct editable by student!", TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, transitionSubmissionState.isEditableByStudent());
        assertEquals("The submission state did not contain the correct active!", TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, transitionSubmissionState.isActive());
        assertEquals("The submission state did not contain the correct default!", TEST_TRANSITION_SUBMISSION_STATUS_DEFAULT, transitionSubmissionState.isDefault());
    }

    @Override
    public void testDuplication() {
        submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, TEST_PARENT_SUBMISSION_STATUS_DEFAULT, null);
        try {
            submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, TEST_PARENT_SUBMISSION_STATUS_DEFAULT, null);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated submission state!", 1, submissionStatusRepo.count());
    }

    @Override
    public void testDelete() {
        SubmissionStatus parentSubmissionStatus = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, TEST_PARENT_SUBMISSION_STATUS_DEFAULT, null);
        submissionStatusRepo.delete(parentSubmissionStatus);
        assertEquals("Submission status did not delete!", 0, submissionStatusRepo.count());

        // verify that submission status cannot be deleted when isDefault is TRUE.
        parentSubmissionStatus = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, true, null);
        submissionStatusRepo.delete(parentSubmissionStatus);
        assertEquals("Submission status should not be deleted!", 1, submissionStatusRepo.count());
    }

    @Override
    public void testCascade() {
        // create states
        SubmissionStatus parentSubmissionStatus = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, TEST_PARENT_SUBMISSION_STATUS_DEFAULT, null);
        SubmissionStatus childTransitionSubmissionStatus = submissionStatusRepo.create(TEST_TRANSITION1_SUBMISSION_STATUS_NAME, TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, TEST_TRANSITION_SUBMISSION_STATUS_DEFAULT, null);
        SubmissionStatus grandchildTransitionSubmissionStatus = submissionStatusRepo.create(TEST_TRANSITION2_SUBMISSION_STATUS_NAME, TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, TEST_PARENT_SUBMISSION_STATUS_DEFAULT, null);

        // add child transitional submission state to parent submission state
        parentSubmissionStatus.addTransitionSubmissionStatus(childTransitionSubmissionStatus);
        parentSubmissionStatus = submissionStatusRepo.save(parentSubmissionStatus);

        // add grandchild transitional submission state to child transitional submission state
        childTransitionSubmissionStatus.addTransitionSubmissionStatus(grandchildTransitionSubmissionStatus);
        childTransitionSubmissionStatus = submissionStatusRepo.save(childTransitionSubmissionStatus);

        // verify submission state transitions
        assertEquals("Parent submission status does not contain correct count of transition submission statuses!", 1, parentSubmissionStatus.getTransitionSubmissionStatuses().size());
        assertEquals("Parent submission status does not contain correct child transition submission status!", childTransitionSubmissionStatus, (parentSubmissionStatus.getTransitionSubmissionStatuses().toArray()[0]));
        assertEquals("Child transition submission status does not contain correct count of transition submission statuses!", 1, childTransitionSubmissionStatus.getTransitionSubmissionStatuses().size());
        assertEquals("Child transition submission status does not contain correct grandchild transition submission status!", grandchildTransitionSubmissionStatus, (childTransitionSubmissionStatus.getTransitionSubmissionStatuses().toArray()[0]));
        assertEquals("Grandchild transition submission status does not contain correct count of transition submission statuses!", 0, grandchildTransitionSubmissionStatus.getTransitionSubmissionStatuses().size());

        // test remove severable child transition submission state
        parentSubmissionStatus.removeTransitionSubmissionStatus(childTransitionSubmissionStatus);
        childTransitionSubmissionStatus = submissionStatusRepo.findOne(childTransitionSubmissionStatus.getId());
        assertNotEquals("The severable child transition submission status was deleted!", null, childTransitionSubmissionStatus);
        parentSubmissionStatus = submissionStatusRepo.save(parentSubmissionStatus);
        assertEquals("The parent submission status had incorrect number of transition submission status (after detatch)!", 0, parentSubmissionStatus.getTransitionSubmissionStatuses().size());

        // test re-attach severable child transition submission state
        parentSubmissionStatus.addTransitionSubmissionStatus(childTransitionSubmissionStatus);
        parentSubmissionStatus = submissionStatusRepo.save(parentSubmissionStatus);
        assertEquals("The parent submission status had incorrect number of transition submission status (after re-attach)!", 1, parentSubmissionStatus.getTransitionSubmissionStatuses().size());

        // test delete parent submission state transition
        submissionStatusRepo.delete(parentSubmissionStatus);
        childTransitionSubmissionStatus = submissionStatusRepo.findOne(childTransitionSubmissionStatus.getId());
        assertNotEquals("The child transition submission status was deleted!", null, childTransitionSubmissionStatus);
        assertEquals("The child transition submission status was deleted!", 2, submissionStatusRepo.count());
        assertEquals("The child transition submission status was deleted!", 1, childTransitionSubmissionStatus.getTransitionSubmissionStatuses().size());

        // test delete child submission state transition
        submissionStatusRepo.delete(childTransitionSubmissionStatus);
        grandchildTransitionSubmissionStatus = submissionStatusRepo.findOne(grandchildTransitionSubmissionStatus.getId());
        assertNotEquals("The grandchild transition submission state was deleted!", null, grandchildTransitionSubmissionStatus);
        assertEquals("The child transition submission state was deleted!", 1, submissionStatusRepo.count());


        // test delete grandchild submission state transition
        submissionStatusRepo.delete(grandchildTransitionSubmissionStatus);
        assertEquals("The child transition submission status was deleted!", 0, submissionStatusRepo.count());
    }

    @After
    public void cleanUp() {
        submissionStatusRepo.deleteAll();
    }

}
