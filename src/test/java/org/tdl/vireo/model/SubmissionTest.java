package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubmissionTest {
    static final String TEST_PARENT_SUBMISSION_STATE_NAME = "Test Parent Submission State";
    static final boolean TEST_PARENT_SUBMISSION_STATE_ARCHIVED = false;
    static final boolean TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_DELETABLE = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_ACTIVE = true;

    SubmissionState parentSubmissionState;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @BeforeClass
    public static void init() {

    }

    @Before
    public void setUp() {
        assertEquals("The submission repository was not empty!", 0, submissionRepo.count());
        parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submission state does not exist!", 1, submissionStateRepo.count());
    }

    @Test
    @Order(value = 1)
    public void testCreate() {

    }

    @Test
    @Order(value = 2)
    public void testDuplication() {

    }

    @Test
    @Order(value = 3)
    public void testFind() {

    }

    @Test
    @Order(value = 4)
    public void testDelete() {

    }

    @Test
    @Order(value = 5)
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();
    }

}
