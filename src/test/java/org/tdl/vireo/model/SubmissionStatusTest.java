package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class SubmissionStatusTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        SubmissionStatus parentSubmissionState = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        SubmissionStatus transitionSubmissionState = submissionStatusRepo.create(TEST_TRANSITION1_SUBMISSION_STATUS_NAME, TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, null);
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

        assertEquals("The submission state did not contain the correct transition submission step!", transitionSubmissionState, (SubmissionStatus) (parentSubmissionState.getTransitionSubmissionStatuses().toArray()[0]));

        assertEquals("The submission state did not contain the correct name!", TEST_TRANSITION1_SUBMISSION_STATUS_NAME, transitionSubmissionState.getName());
        assertEquals("The submission state did not contain the correct archived!", TEST_TRANSITION_SUBMISSION_STATUS_ARCHIVED, transitionSubmissionState.isArchived());
        assertEquals("The submission state did not contain the correct publishable!", TEST_TRANSITION_SUBMISSION_STATUS_PUBLISHABLE, transitionSubmissionState.isPublishable());
        assertEquals("The submission state did not contain the correct deletable!", TEST_TRANSITION_SUBMISSION_STATUS_DELETABLE, transitionSubmissionState.isDeletable());
        assertEquals("The submission state did not contain the correct editable by reviewer!", TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, transitionSubmissionState.isEditableByReviewer());
        assertEquals("The submission state did not contain the correct editable by student!", TEST_TRANSITION_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, transitionSubmissionState.isEditableByStudent());
        assertEquals("The submission state did not contain the correct active!", TEST_TRANSITION_SUBMISSION_STATUS_ACTIVE, transitionSubmissionState.isActive());
    }

    @Override
    public void testDuplication() {
        submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        try {
            submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated submission state!", 1, submissionStatusRepo.count());
    }

    @Override
    public void testDelete() {
        SubmissionStatus parentSubmissionState = submissionStatusRepo.create(TEST_PARENT_SUBMISSION_STATUS_NAME, TEST_PARENT_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATUS_DELETABLE, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATUS_ACTIVE, null);
        submissionStatusRepo.delete(parentSubmissionState);
        assertEquals("Submission state did not delete!", 0, submissionStatusRepo.count());
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
        assertEquals("Parent submission state does not contain correct count of transition submission states!", 1, parentSubmissionState.getTransitionSubmissionStatuses().size());
        assertEquals("Parent submission state does not contain correct transition1 submission state!", transition1SubmissionState, (SubmissionStatus) (parentSubmissionState.getTransitionSubmissionStatuses().toArray()[0]));
        assertEquals("Transition 1 submission state does not contain correct count of transition submission states!", 1, transition1SubmissionState.getTransitionSubmissionStatuses().size());
        assertEquals("Transition 1 submission state does not contain correct transition2 submission state!", transition2SubmissionState, (SubmissionStatus) (transition1SubmissionState.getTransitionSubmissionStatuses().toArray()[0]));
        assertEquals("Transition 2 submission state does not contain correct count of transition submission states!", 0, transition2SubmissionState.getTransitionSubmissionStatuses().size());

        // test remove severable child transition submission state
        parentSubmissionState.removeTransitionSubmissionStatus(transition1SubmissionState);
        transition1SubmissionState = submissionStatusRepo.findOne(transition1SubmissionState.getId());
        assertNotEquals("The severable transition1 submission state was deleted!", null, transition1SubmissionState);
        parentSubmissionState = submissionStatusRepo.save(parentSubmissionState);
        assertEquals("The parent submission state had incorrect number of transition submission states (after detatch)!", 0, parentSubmissionState.getTransitionSubmissionStatuses().size());

        // test re-attach severable child transition submission state
        parentSubmissionState.addTransitionSubmissionStatus(transition1SubmissionState);
        parentSubmissionState = submissionStatusRepo.save(parentSubmissionState);
        assertEquals("The parent submission state had incorrect number of transition submission states (after re-attach)!", 1, parentSubmissionState.getTransitionSubmissionStatuses().size());

        // test delete parent submission state transition
        submissionStatusRepo.delete(parentSubmissionState);
        transition1SubmissionState = submissionStatusRepo.findOne(transition1SubmissionState.getId());
        assertNotEquals("The child transition submission state was deleted!", null, transition1SubmissionState);
        assertEquals("The child transition submission state was deleted!", 2, submissionStatusRepo.count());
        assertEquals("The child transition submission state was deleted!", 1, transition1SubmissionState.getTransitionSubmissionStatuses().size());

        // test delete child submission state transition
        submissionStatusRepo.delete(transition1SubmissionState);
        transition2SubmissionState = submissionStatusRepo.findOne(transition2SubmissionState.getId());
        assertNotEquals("The grandchild transition submission state was deleted!", null, transition2SubmissionState);
        assertEquals("The child transition submission state was deleted!", 1, submissionStatusRepo.count());

        // test delete grandchild submission state transition
        submissionStatusRepo.delete(transition2SubmissionState);
        assertEquals("The child transition submission state was deleted!", 0, submissionStatusRepo.count());
    }

    @After
    public void cleanUp() {
        submissionStatusRepo.deleteAll();
    }

}
