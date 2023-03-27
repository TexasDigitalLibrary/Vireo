package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

public class CustomActionValueTest extends AbstractEntityTest {

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @BeforeEach
    public void setUp() throws Exception {
        assertEquals(0, customActionValueRepo.count(), "The CustomActionValue repository is not empty!");
        userRepo.deleteAll();

        testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        assertEquals(1, userRepo.count(), "The user repository is not empty!");

        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);
        assertEquals(1, submissionStatusRepo.count(), "The submissionStatus repository is not empty!");

        Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, organizationCategoryRepo.create(TEST_ORGANIZATION_CATEGORY_NAME));

        testSubmission = submissionRepo.create(testUser, organization, submissionStatus, getCredentials(), customActionDefinitionRepo.findAll());

        assertEquals(1, submissionRepo.count(), "The submission repository is not empty!");

        testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        assertEquals(1, customActionDefinitionRepo.count(), "The customActionDefinition repository is not empty!");
    }

    @Transactional
    @Override
    @Test
    public void testCreate() {
        CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        assertEquals(1, customActionValueRepo.count(), "The custom action value was not created in the repository");
        testSubmission = submissionRepo.read(testSubmission.getId());
        assertTrue(testSubmission.getCustomActionValues().contains(testCustomActionValue), "The submission didn't get the created custom action value!");
        // assertEquals(TEST_USER_FIRSTNAME, testCustomActionValue.getSubmission().getSubmitter().getFirstName(), "Saved custom Action Value does not contain the correct submitter first name");
        // TODO - similar tests for custom action definition
    }

    @Transactional
    @Override
    @Test
    public void testDuplication() {
        CustomActionValue cav1 = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        testSubmission = submissionRepo.read(testSubmission.getId());
        CustomActionValue cav2 = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        testSubmission = submissionRepo.read(testSubmission.getId());
        assertEquals(2, customActionValueRepo.count(), "The action log entry was not duplicated");
        assertTrue(testSubmission.getCustomActionValues().contains(cav1), "The submission didn't get the created custom action value!");
        assertTrue(testSubmission.getCustomActionValues().contains(cav2), "The submission didn't get the created custom action value!");

    }

    @Transactional
    @Override
    @Test
    public void testDelete() {
        CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        customActionValueRepo.delete(testCustomActionValue);
        assertEquals(0, customActionValueRepo.count(), "CustomActionValue was not deleted!");
    }

    @Transactional
    @Override
    @Test
    public void testCascade() {
        CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
        customActionValueRepo.delete(testCustomActionValue);
        assertEquals(1, submissionRepo.count(), "Submission was deleted");
        assertEquals(1, customActionDefinitionRepo.count(), "CustomActionDefinition was deleted");
    }

    @AfterEach
    public void cleanUp() {
        submissionRepo.deleteAll();
        customActionValueRepo.deleteAll();
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        namedSearchFilterGroupRepo.findAll().forEach(nsf -> {
            namedSearchFilterGroupRepo.delete(nsf);
        });
        userRepo.deleteAll();
        submissionStatusRepo.deleteAll();
        customActionDefinitionRepo.deleteAll();
    }

}
