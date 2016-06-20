package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;


import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;

import edu.tamu.framework.model.Credentials;

public class SubmissionWorkflowStepTest extends AbstractEntityTest {
    
    User anotherUser;
    Credentials credentials;
    Credentials anotherCredentials;
    Submission submission;
    
    @Autowired
    SubmissionFieldProfileRepo submissionFieldProfileRepo;
    
    FieldProfile fieldProfile;

    

    @Before
    public void setUp() throws Exception {
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        submitter = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        anotherUser = userRepo.create("another@tdl.org", "Other", "An", TEST_USER_ROLE);
        credentials = new Credentials();
        anotherCredentials = new Credentials();
        
        credentials.setEmail(submitter.getEmail());
        submission = submissionRepo.create(credentials, organization.getId()); 
        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
    }

   
    @Override
    public void testCreate() {
        
        SubmissionWorkflowStep sws = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        
        assertEquals("The repository didn't save the entity!", 1, submissionWorkflowStepRepo.count());
        assertFalse("The submissionWorkflowStep didn't clone it's originating step's field profile!", fieldProfile.equals( sws.getAggregateFieldProfiles().get(0) ) );
        assertEquals("The submissionWorkflowStep didn't clone it's originating step's field profile!", fieldProfile.getPredicate(), sws.getAggregateFieldProfiles().get(0).getPredicate());
        assertEquals("The submissionWorkflowStep didn't clone it's originating step's field profile!", fieldProfile.getInputType(), sws.getAggregateFieldProfiles().get(0).getInputType());
    }


    @Override
    public void testDuplication() {
        SubmissionWorkflowStep sws = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        SubmissionWorkflowStep sws2 = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        assertEquals("The submission workflow step was duplicated!", sws.getId(), sws2.getId());
    }

    
    @Override
    public void testDelete() {
    
        SubmissionWorkflowStep sws = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        
        long id = sws.getId();
        long count = submissionWorkflowStepRepo.count();
        
        submissionWorkflowStepRepo.delete(sws);
        count--;
        
        assertNull("The submissionWorkflowStep wasn't deleted!", submissionWorkflowStepRepo.findOne(id));
        assertEquals("The submissionWorkflowStep wasn't deleted!", count, submissionWorkflowStepRepo.count());
        
    }

    @Override
    public void testCascade() {
        // TODO Auto-generated method stub
        
    }
    
    @After
    public void tearDown() throws Exception {
        
        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });
        
        submissionWorkflowStepRepo.deleteAll();
        
        for(FieldProfile fp : fieldProfileRepo.findAll()){
            fieldProfileRepo.delete(fp);
        }
        assertEquals("Couldn't delete all field profiles!", 0, fieldProfileRepo.count());
        
        submissionFieldProfileRepo.deleteAll();
                
        submissionRepo.deleteAll();
        
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        userRepo.deleteAll();
        
        organizationCategoryRepo.deleteAll();
        
        noteRepo.deleteAll();
        assertEquals("Couldn't delete all notes!", 0, noteRepo.count());
        
        fieldPredicateRepo.deleteAll();
    }

}
