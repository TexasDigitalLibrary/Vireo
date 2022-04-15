package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AfterEach;
import org.springframework.dao.DataIntegrityViolationException;

public class SubmissionStatusTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        SubmissionStatus parentSubmissionState = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        SubmissionStatus transitionSubmissionState = submissionStatusRepo.create(TEST_TRANSITION1_SUBMISSION_STATUS_NAME, TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, null);
        parentSubmissionState.addTransitionSubmissionStatus(transitionSubmissionState);
        submissionStatusRepo.save(parentSubmissionState);

        assertEquals(2, submissionStatusRepo.count(), "The submission state does not exist!");

        assertEquals(TEST_PARENT_SUBMISSION_STATUS_NAME, parentSubmissionState.getName(), "The submission state did not contain the correct name!");
        assertEquals(TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, parentSubmissionState.isArchived(), "The submission state did not contain the correct archived!");
        assertEquals(TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, parentSubmissionState.isPublishable(), "The submission state did not contain the correct publishable!");
        assertEquals(TEST_PARENT_SUBMISSION_STATUS_DELETABLE, parentSubmissionState.isDeletable(), "The submission state did not contain the correct deletable!");
        assertEquals(TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, parentSubmissionState.isEditableByReviewer(), "The submission state did not contain the correct editable by reviewer!");
        assertEquals(TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, parentSubmissionState.isEditableByStudent(), "The submission state did not contain the correct editable by student!");
        assertEquals(TEST_PARENT_SUBMISSION_STATUS_ACTIVE, parentSubmissionState.isActive(), "The submission state did not contain the correct active!");

        assertEquals(transitionSubmissionState, (SubmissionStatus) (parentSubmissionState.getTransitionSubmissionStatuses().toArray()[0]), "The submission state did not contain the correct transition submission step!");

        assertEquals(TEST_TRANSITION1_SUBMISSION_STATUS_NAME, transitionSubmissionState.getName(), "The submission state did not contain the correct name!");
        assertEquals(TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, transitionSubmissionState.isArchived(), "The submission state did not contain the correct archived!");
        assertEquals(TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, transitionSubmissionState.isPublishable(), "The submission state did not contain the correct publishable!");
        assertEquals(TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, transitionSubmissionState.isDeletable(), "The submission state did not contain the correct deletable!");
        assertEquals(TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, transitionSubmissionState.isEditableByReviewer(), "The submission state did not contain the correct editable by reviewer!");
        assertEquals(TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, transitionSubmissionState.isEditableByStudent(), "The submission state did not contain the correct editable by student!");
        assertEquals(TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, transitionSubmissionState.isActive(), "The submission state did not contain the correct active!");
    }

    @Override
    public void testDuplication() {
        submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        try {
            submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals(1, submissionStatusRepo.count(), "The repository duplicated submission state!");
    }

    @Override
    public void testDelete() {
        SubmissionStatus parentSubmissionState = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        submissionStatusRepo.delete(parentSubmissionState);
        assertEquals(0, submissionStatusRepo.count(), "Submission state did not delete!");
    }

    @Override
    public void testCascade() {
        // create states
        SubmissionStatus parentSubmissionState = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        SubmissionStatus transition1SubmissionState = submissionStatusRepo.create(TEST_TRANSITION1_SUBMISSION_STATUS_NAME, TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, null);
        SubmissionStatus transition2SubmissionState = submissionStatusRepo.create(TEST_TRANSITION2_SUBMISSION_STATUS_NAME, TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, null);

        // add transitional2 submission state to transitional1 submission state
        transition1SubmissionState.addTransitionSubmissionStatus(transition2SubmissionState);
        transition1SubmissionState = submissionStatusRepo.save(transition1SubmissionState);

        // add transitional1 submission state to parent submission state
        parentSubmissionState.addTransitionSubmissionStatus(transition1SubmissionState);
        parentSubmissionState = submissionStatusRepo.save(parentSubmissionState);

        // verify submission state transitions
        assertEquals(1, parentSubmissionState.getTransitionSubmissionStatuses().size(), "Parent submission state does not contain correct count of transition submission states!");
        assertEquals(transition1SubmissionState, (SubmissionStatus) (parentSubmissionState.getTransitionSubmissionStatuses().toArray()[0]), "Parent submission state does not contain correct transition1 submission state!");
        assertEquals(1, transition1SubmissionState.getTransitionSubmissionStatuses().size(), "Transition 1 submission state does not contain correct count of transition submission states!");
        assertEquals(transition2SubmissionState, (SubmissionStatus) (transition1SubmissionState.getTransitionSubmissionStatuses().toArray()[0]), "Transition 1 submission state does not contain correct transition2 submission state!");
        assertEquals(0, transition2SubmissionState.getTransitionSubmissionStatuses().size(), "Transition 2 submission state does not contain correct count of transition submission states!");

        // test remove severable child transition submission state
        parentSubmissionState.removeTransitionSubmissionStatus(transition1SubmissionState);
        transition1SubmissionState = submissionStatusRepo.getById(transition1SubmissionState.getId());
        assertNotEquals(null, transition1SubmissionState, "The severable transition1 submission state was deleted!");
        parentSubmissionState = submissionStatusRepo.save(parentSubmissionState);
        assertEquals(0, parentSubmissionState.getTransitionSubmissionStatuses().size(), "The parent submission state had incorrect number of transition submission states (after detatch)!");

        // test re-attach severable child transition submission state
        parentSubmissionState.addTransitionSubmissionStatus(transition1SubmissionState);
        parentSubmissionState = submissionStatusRepo.save(parentSubmissionState);
        assertEquals(1, parentSubmissionState.getTransitionSubmissionStatuses().size(), "The parent submission state had incorrect number of transition submission states (after re-attach)!");

        // test delete parent submission state transition
        submissionStatusRepo.delete(parentSubmissionState);
        transition1SubmissionState = submissionStatusRepo.getById(transition1SubmissionState.getId());
        assertNotEquals(null, transition1SubmissionState, "The child transition submission state was deleted!");
        assertEquals(2, submissionStatusRepo.count(), "The child transition submission state was deleted!");
        assertEquals(1, transition1SubmissionState.getTransitionSubmissionStatuses().size(), "The child transition submission state was deleted!");

        // test delete child submission state transition
        submissionStatusRepo.delete(transition1SubmissionState);
        transition2SubmissionState = submissionStatusRepo.getById(transition2SubmissionState.getId());
        assertNotEquals(null, transition2SubmissionState, "The grandchild transition submission state was deleted!");
        assertEquals(1, submissionStatusRepo.count(), "The child transition submission state was deleted!");

        // test delete grandchild submission state transition
        submissionStatusRepo.delete(transition2SubmissionState);
        assertEquals(0, submissionStatusRepo.count(), "The child transition submission state was deleted!");
    }

    @AfterEach
    public void cleanUp() {
        submissionStatusRepo.deleteAll();
    }

}
