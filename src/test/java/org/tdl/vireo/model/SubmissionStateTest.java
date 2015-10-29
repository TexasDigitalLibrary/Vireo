package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public class SubmissionStateTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        SubmissionState parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        SubmissionState transitionSubmissionState = submissionStateRepo.create(TEST_TRANSITION1_SUBMISSION_STATE_NAME, TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATE_DELETABLE, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATE_ACTIVE);
        parentSubmissionState.addTransitionSubmissionState(transitionSubmissionState);
        submissionStateRepo.save(parentSubmissionState);

        assertEquals("The submission state does not exist!", 2, submissionStateRepo.count());

        assertEquals("The submission state did not contain the correct name!", TEST_PARENT_SUBMISSION_STATE_NAME, parentSubmissionState.getName());
        assertEquals("The submission state did not contain the correct archived!", TEST_PARENT_SUBMISSION_STATE_ARCHIVED, parentSubmissionState.isArchived());
        assertEquals("The submission state did not contain the correct publishable!", TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, parentSubmissionState.isPublishable());
        assertEquals("The submission state did not contain the correct deletable!", TEST_PARENT_SUBMISSION_STATE_DELETABLE, parentSubmissionState.isDeletable());
        assertEquals("The submission state did not contain the correct editable by reviewer!", TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, parentSubmissionState.isEditableByReviewer());
        assertEquals("The submission state did not contain the correct editable by student!", TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, parentSubmissionState.isEditableByStudent());
        assertEquals("The submission state did not contain the correct active!", TEST_PARENT_SUBMISSION_STATE_ACTIVE, parentSubmissionState.isActive());

        assertEquals("The submission state did not contain the correct transition submission step!", transitionSubmissionState, (SubmissionState) (parentSubmissionState.getTransitionSubmissionStates().toArray()[0]));

        assertEquals("The submission state did not contain the correct name!", TEST_TRANSITION1_SUBMISSION_STATE_NAME, transitionSubmissionState.getName());
        assertEquals("The submission state did not contain the correct archived!", TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED, transitionSubmissionState.isArchived());
        assertEquals("The submission state did not contain the correct publishable!", TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE, transitionSubmissionState.isPublishable());
        assertEquals("The submission state did not contain the correct deletable!", TEST_TRANSITION_SUBMISSION_STATE_DELETABLE, transitionSubmissionState.isDeletable());
        assertEquals("The submission state did not contain the correct editable by reviewer!", TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, transitionSubmissionState.isEditableByReviewer());
        assertEquals("The submission state did not contain the correct editable by student!", TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT, transitionSubmissionState.isEditableByStudent());
        assertEquals("The submission state did not contain the correct active!", TEST_TRANSITION_SUBMISSION_STATE_ACTIVE, transitionSubmissionState.isActive());
    }

    @Override
    public void testDuplication() {
        submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        try {
            submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated submission state!", 1, submissionStateRepo.count());
    }

    @Override
    public void testDelete() {
        SubmissionState parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        submissionStateRepo.delete(parentSubmissionState);
        assertEquals("Submission state did not delete!", 0, submissionStateRepo.count());
    }

    @Override
    @Transactional
    public void testCascade() {
        // create states
        SubmissionState parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        SubmissionState transition1SubmissionState = submissionStateRepo.create(TEST_TRANSITION1_SUBMISSION_STATE_NAME, TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATE_DELETABLE, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATE_ACTIVE);
        SubmissionState transition2SubmissionState = submissionStateRepo.create(TEST_TRANSITION2_SUBMISSION_STATE_NAME, TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATE_DELETABLE, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATE_ACTIVE);

        // add transitional2 submission state to transitional1 submission state
        transition1SubmissionState.addTransitionSubmissionState(transition2SubmissionState);
        transition1SubmissionState = submissionStateRepo.findOne(transition1SubmissionState.getId());

        // add transitional1 submission state to parent submission state
        parentSubmissionState.addTransitionSubmissionState(transition1SubmissionState);
        parentSubmissionState = submissionStateRepo.findOne(parentSubmissionState.getId());

        // verify submission state transitions
        assertEquals("Parent submission state does not contain correct count of transition submission states!", 1, parentSubmissionState.getTransitionSubmissionStates().size());
        assertEquals("Parent submission state does not contain correct transition1 submission state!", transition1SubmissionState, (SubmissionState) (parentSubmissionState.getTransitionSubmissionStates().toArray()[0]));
        assertEquals("Transition 1 submission state does not contain correct count of transition submission states!", 1, transition1SubmissionState.getTransitionSubmissionStates().size());
        assertEquals("Transition 1 submission state does not contain correct transition2 submission state!", transition2SubmissionState, (SubmissionState) (transition1SubmissionState.getTransitionSubmissionStates().toArray()[0]));
        assertEquals("Transition 2 submission state does not contain correct count of transition submission states!", 0, transition2SubmissionState.getTransitionSubmissionStates().size());

        // test detach detachable child transition submission state
        parentSubmissionState.removeTransitionSubmissionState(transition1SubmissionState);
        transition1SubmissionState = submissionStateRepo.findOne(transition1SubmissionState.getId());
        assertNotEquals("The detachable transition1 submission state was deleted!", null, transition1SubmissionState);
        parentSubmissionState = submissionStateRepo.findOne(parentSubmissionState.getId());
        assertEquals("The parent submission state had incorrect number of transition submission states (after detatch)!", 0, parentSubmissionState.getTransitionSubmissionStates().size());

        // test re-attach detachable child transition submission state
        parentSubmissionState.addTransitionSubmissionState(transition1SubmissionState);
        parentSubmissionState = submissionStateRepo.findOne(parentSubmissionState.getId());
        assertEquals("The parent submission state had incorrect number of transition submission states (after re-attach)!", 1, parentSubmissionState.getTransitionSubmissionStates().size());

        // test delete parent submission state transition
        submissionStateRepo.delete(parentSubmissionState);
        transition1SubmissionState = submissionStateRepo.findOne(transition1SubmissionState.getId());
        assertNotEquals("The child transition submission state was deleted!", null, transition1SubmissionState);
        assertEquals("The child transition submission state was deleted!", 2, submissionStateRepo.count());
        assertEquals("The child transition submission state was deleted!", 1, transition1SubmissionState.getTransitionSubmissionStates().size());

        // test delete child submission state transition
        submissionStateRepo.delete(transition1SubmissionState);
        transition2SubmissionState = submissionStateRepo.findOne(transition2SubmissionState.getId());
        assertNotEquals("The grandchild transition submission state was deleted!", null, transition2SubmissionState);
        assertEquals("The child transition submission state was deleted!", 1, submissionStateRepo.count());

        // test delete grandchild submission state transition
        submissionStateRepo.delete(transition2SubmissionState);
        assertEquals("The child transition submission state was deleted!", 0, submissionStateRepo.count());
    }

    @After
    public void cleanUp() {
        submissionStateRepo.deleteAll();
    }

}
