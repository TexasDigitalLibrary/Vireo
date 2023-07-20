package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionWorkflowStepRepoTest extends AbstractRepoTest {

    // private User anotherUser;
    // private Credentials credentials;
    // private Credentials anotherCredentials;
    // private Submission submission;
    // private FieldProfile fieldProfile;

    @Autowired
    private SubmissionFieldProfileRepo submissionFieldProfileRepo;

    @Autowired
    private SubmissionNoteRepo submissionNoteRepo;

    @BeforeEach
    public void setup() throws Exception {
        // parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        // organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        // parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        // submitter = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        // anotherUser = userRepo.create("another@tdl.org", "Other", "An", TEST_USER_ROLE);
        // credentials = new Credentials();
        // anotherCredentials = new Credentials();
        //
        // credentials.setEmail(submitter.getEmail());
        // submission = submissionRepo.create(credentials, organization.getId());
        // workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        // fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        // fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
    }

    @Override
    @Test
    public void testCreate() {

        // SubmissionWorkflowStep sws = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        //
        // assertEquals(1, submissionWorkflowStepRepo.count(), "The repository didn't save the entity!");
        // assertFalse(fieldProfile.equals( sws.getAggregateFieldProfiles().get(0) ) , "The submissionWorkflowStep didn't clone it's originating step's field profile!");
        // assertEquals(fieldProfile.getPredicate(), sws.getAggregateFieldProfiles().get(0).getPredicate(), "The submissionWorkflowStep didn't clone it's originating step's field profile!");
        // assertEquals(fieldProfile.getInputType(), sws.getAggregateFieldProfiles().get(0).getInputType(), "The submissionWorkflowStep didn't clone it's originating step's field profile!");
    }

    @Override
    @Test
    public void testDuplication() {
        // SubmissionWorkflowStep sws = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        // SubmissionWorkflowStep sws2 = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        // assertEquals(sws.getId(), sws2.getId(), "The submission workflow step was duplicated!");
    }

    @Override
    @Test
    public void testDelete() {
        // TODO:
        // SubmissionWorkflowStep sws = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        //
        // long id = sws.getId();
        // long count = submissionWorkflowStepRepo.count();
        //
        // submissionWorkflowStepRepo.delete(sws);
        // count--;
        //
        // assertNull(submissionWorkflowStepRepo.findOne(id), "The submissionWorkflowStep wasn't deleted!");
        // assertEquals(count, submissionWorkflowStepRepo.count(), "The submissionWorkflowStep wasn't deleted!");

    }

    @Override
    @Test
    public void testCascade() {
        // TODO Auto-generated method stub

    }

    @AfterEach
    public void cleanUp() throws Exception {

        noteRepo.findAll().forEach(note -> {
            noteRepo.delete(note);
        });
        assertEquals(0, noteRepo.count(), "Couldn't delete all notes!");

        submissionNoteRepo.deleteAll();

        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });

        assertEquals(0, fieldProfileRepo.count(), "Couldn't delete all field profiles!");

        submissionListColumnRepo.deleteAll();

        inputTypeRepo.deleteAll();
        assertEquals(0, inputTypeRepo.count(), "Couldn't delete all input types!");

        submissionFieldProfileRepo.deleteAll();

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });

        submissionWorkflowStepRepo.deleteAll();

        submissionRepo.deleteAll();

        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });

        organizationCategoryRepo.deleteAll();

        fieldPredicateRepo.deleteAll();

        userRepo.deleteAll();
    }

}
