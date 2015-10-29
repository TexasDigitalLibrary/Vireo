package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CustomActionValueTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The CustomActionValue repository is not empty!", 0, customActionValueRepo.count());

        testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        assertEquals("The user repository is not empty!", 1, userRepo.count());

        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submissionState repository is not empty!", 1, submissionStateRepo.count());

        testSubmission = submissionRepo.create(testUser, submissionState);
        assertEquals("The submission repository is not empty!", 1, submissionRepo.count());

        testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        assertEquals("The customActionDefinition repository is not empty!", 1, customActionDefinitionRepo.count());
    }

    @Override
    public void testCreate() {
        CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        assertEquals("The custom action value was not created in the repository", 1, customActionValueRepo.count());
        assertEquals("Saved custom Action Value does not contain the correct submitter first name", TEST_USER_FIRSTNAME, testCustomActionValue.getSubmission().getSubmitter().getFirstName());
        // TODO - similar tests for custom action definition
    }

    @Override
    public void testDuplication() {
        customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        assertEquals("The action log entry was not duplicated", 2, customActionValueRepo.count());
    }

    @Override
    public void testDelete() {
        CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        customActionValueRepo.delete(testCustomActionValue);
        assertEquals("CustomActionValue was not deleted!", 0, customActionValueRepo.count());
    }

    @Override
    public void testCascade() {
        CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        customActionValueRepo.delete(testCustomActionValue);
        assertEquals("Submission was deleted", 1, submissionRepo.count());
        assertEquals("CustomActionDefinition was deleted", 1, customActionDefinitionRepo.count());
    }

    @After
    public void cleanUp() {
        customActionValueRepo.deleteAll();
        submissionRepo.deleteAll();
        userRepo.deleteAll();
        submissionStateRepo.deleteAll();
        customActionDefinitionRepo.deleteAll();
    }

}
