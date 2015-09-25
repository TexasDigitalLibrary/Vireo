package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubmissionStateTest {

    static final String TEST_PARENT_SUBMISSION_STATE_NAME = "Test Parent Submission State";
    static final boolean TEST_PARENT_SUBMISSION_STATE_ARCHIVED = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_DELETABLE = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_ACTIVE = true;

    static final String TEST_TRANSITION1_SUBMISSION_STATE_NAME = "Test Transition1 Submission State";
    static final String TEST_TRANSITION2_SUBMISSION_STATE_NAME = "Test Transition2 Submission State";
    static final boolean TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED = true;
    static final boolean TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE = true;
    static final boolean TEST_TRANSITION_SUBMISSION_STATE_DELETABLE = true;
    static final boolean TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    static final boolean TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    static final boolean TEST_TRANSITION_SUBMISSION_STATE_ACTIVE = true;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @BeforeClass
    public static void init() {

    }

    @Before
    public void setUp() {
        assertEquals("The submission state repository was not empty!", 0, submissionStateRepo.count());
    }

    @Test
    @Order(value = 1)
    public void testCreate() {
        SubmissionState parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        SubmissionState transitionSubmissionState = submissionStateRepo.create(TEST_TRANSITION1_SUBMISSION_STATE_NAME, TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED, TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE, TEST_TRANSITION_SUBMISSION_STATE_DELETABLE, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_TRANSITION_SUBMISSION_STATE_ACTIVE);
        parentSubmissionState.addTransitionSubmissionState(transitionSubmissionState);
        submissionStateRepo.save(parentSubmissionState);

        assertEquals("The submission state does not exist!", 2, submissionStateRepo.count());

        assertEquals("The submission state did not contain the correct name!", TEST_PARENT_SUBMISSION_STATE_NAME, parentSubmissionState.getName());
        assertEquals("The submission state did not contain the correct archived!", TEST_PARENT_SUBMISSION_STATE_ARCHIVED, parentSubmissionState.getArchived());
        assertEquals("The submission state did not contain the correct publishable!", TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, parentSubmissionState.getPublishable());
        assertEquals("The submission state did not contain the correct deletable!", TEST_PARENT_SUBMISSION_STATE_DELETABLE, parentSubmissionState.getDeletable());
        assertEquals("The submission state did not contain the correct editable by reviewer!", TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, parentSubmissionState.getEditableByReviewer());
        assertEquals("The submission state did not contain the correct editable by student!", TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, parentSubmissionState.getEditableByStudent());
        assertEquals("The submission state did not contain the correct active!", TEST_PARENT_SUBMISSION_STATE_ACTIVE, parentSubmissionState.getActive());

        assertEquals("The submission state did not contain the correct transition submission step!", transitionSubmissionState, (SubmissionState) (parentSubmissionState.getTransitionSubmissionStates().toArray()[0]));

        assertEquals("The submission state did not contain the correct name!", TEST_TRANSITION1_SUBMISSION_STATE_NAME, transitionSubmissionState.getName());
        assertEquals("The submission state did not contain the correct archived!", TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED, transitionSubmissionState.getArchived());
        assertEquals("The submission state did not contain the correct publishable!", TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE, transitionSubmissionState.getPublishable());
        assertEquals("The submission state did not contain the correct deletable!", TEST_TRANSITION_SUBMISSION_STATE_DELETABLE, transitionSubmissionState.getDeletable());
        assertEquals("The submission state did not contain the correct editable by reviewer!", TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, transitionSubmissionState.getEditableByReviewer());
        assertEquals("The submission state did not contain the correct editable by student!", TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT, transitionSubmissionState.getEditableByStudent());
        assertEquals("The submission state did not contain the correct active!", TEST_TRANSITION_SUBMISSION_STATE_ACTIVE, transitionSubmissionState.getActive());
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        try {
            submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        } catch (Exception e) {
            /* SUCCESS */
        }
        assertEquals("The repository duplicated submission state!", 1, submissionStateRepo.count());
    }

    @Test
    @Order(value = 3)
    public void testFind() {
        submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        SubmissionState parentSubmissionState = submissionStateRepo.findByName(TEST_PARENT_SUBMISSION_STATE_NAME);
        assertNotEquals("Did not find submission state!", null, parentSubmissionState);
        assertEquals("Found submission state did not contain the correct name!", TEST_PARENT_SUBMISSION_STATE_NAME, parentSubmissionState.getName());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        SubmissionState parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        submissionStateRepo.delete(parentSubmissionState);
        assertEquals("Submission state did not delete!", 0, submissionStateRepo.count());
    }

    @Test
    @Order(value = 5)
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
