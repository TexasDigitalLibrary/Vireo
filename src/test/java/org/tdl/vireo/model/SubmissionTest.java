package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import edu.tamu.framework.model.Credentials;

public class SubmissionTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The submission repository was not empty!", 0, submissionRepo.count());

        submitter = userRepo.create(TEST_SUBMISSION_SUBMITTER_EMAIL, TEST_SUBMISSION_SUBMITTER_FIRSTNAME, TEST_SUBMISSION_SUBMITTER_LASTNAME, TEST_SUBMISSION_SUBMITTER_ROLE);
        assertEquals("The user does not exist!", 1, userRepo.count());

        graduateOfficeEmployee1 = userRepo.create(TEST_SUBMISSION_REVIEWER1_EMAIL, TEST_SUBMISSION_REVIEWER1_FIRSTNAME, TEST_SUBMISSION_REVIEWER1_LASTNAME, TEST_SUBMISSION_REVIEWER1_ROLE);
        assertEquals("The first reviewer does not exist!", 2, userRepo.count());

        graduateOfficeEmployee1 = userRepo.create(TEST_SUBMISSION_REVIEWER2_EMAIL, TEST_SUBMISSION_REVIEWER1_FIRSTNAME, TEST_SUBMISSION_REVIEWER1_LASTNAME, TEST_SUBMISSION_REVIEWER1_ROLE);
        assertEquals("The second reviewer does not exist!", 3, userRepo.count());

        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submission state does not exist!", 1, submissionStateRepo.count());

        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        assertEquals("The field predicate does not exist!", 1, fieldPredicateRepo.count());

        fieldValue = fieldValueRepo.create(fieldPredicate);
        fieldValue.setValue(TEST_FIELD_VALUE);
        fieldValue = fieldValueRepo.save(fieldValue);
        assertEquals("The field value does not exist!", 1, fieldValueRepo.count());
        assertEquals("The field value did not have the correct value!", TEST_FIELD_VALUE, fieldValue.getValue());

        OrganizationCategory parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        assertEquals("The organization does not exist!", 1, organizationRepo.count());

        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The workflow step does not exist!", 1, workflowStepRepo.count());

        submissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        
        attachmentType = attachmentTypeRepo.create(TEST_ATTACHMENT_TYPE_NAME);
        assertEquals("The attachmentType does not exist!", 1, attachmentTypeRepo.count());
        
        attachment = attachmentRepo.create(TEST_ATTACHMENT_NAME, TEST_UUID, attachmentType);
        assertEquals("The attachment does not exist!", 1, attachmentRepo.count());

        embargoType = embargoRepo.create(TEST_EMBARGO_TYPE_NAME, TEST_EMBARGO_TYPE_DESCRIPTION, TEST_EMBARGO_TYPE_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals("The embargo type does not exist!", 1, embargoRepo.count());
    }

    @Override
    public void testCreate() {
        Credentials credentials = new Credentials();
                
        credentials.setEmail(submitter.getEmail());
        Submission submission = submissionRepo.create(credentials, organization.getId());
        submission.setState(submissionState);
        
        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        submission.addFieldValue(fieldValue);
        submission.addAttachment(attachment);
        submission.addEmbargoType(embargoType);
        
        submissionWorkflowStep = submissionWorkflowStepRepo.findOne(submissionWorkflowStep.getId());
        organization = organizationRepo.findOne(organization.getId());
        submission = submissionRepo.save(submission);

        assertEquals("The repository did not save the submission!", 1, submissionRepo.count());
        assertEquals("Saved submission did not contain the correct state!", submissionState, submission.getState());
        assertEquals("Saved submission did not contain the correct submitter!", submitter, submission.getSubmitter());
        assertEquals("Saved submission did not contain the correct organization!", submission.getOrganization(), organization);
        assertEquals("Saved submission did not contain the correct submission workflow step!", true, submission.getSubmissionWorkflowSteps().contains(submissionWorkflowStep));
        assertEquals("Saved submission did not contain the correct field value!", true, submission.getFieldValues().contains(fieldValue));
        assertEquals("Saved submission did not contain the correct attachment!", true, submission.getAttachments().contains(attachment));
        assertEquals("Saved submission did not contain the correct embargo type!", true, submission.getEmbargoTypes().contains(embargoType));
    }

    @Override
    public void testDuplication() {
        Credentials credentials = new Credentials();
        credentials.setEmail(submitter.getEmail());
        submissionRepo.create(credentials, organization.getId());
        assertEquals("The repository didn't persist submission!", 1, submissionRepo.count());
        try {
            submissionRepo.create(credentials,  organization.getId());
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated the submission!", 1, submissionRepo.count());
    }

    @Override
    public void testDelete() {
        Credentials credentials = new Credentials();
        credentials.setEmail(submitter.getEmail());
        Submission submission = submissionRepo.create(credentials, organization.getId());
        submissionRepo.delete(submission);
        assertEquals("Submission did not delete!", 0, submissionRepo.count());
    }

    @Override
    @Transactional
    public void testCascade() {
        parentCategory = organizationCategoryRepo.findOne(organization.getCategory().getId());
        
        WorkflowStep severableWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, organization);
        SubmissionWorkflowStep severableSubmissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, severableWorkflowStep);
        
        organization = organizationRepo.findOne(organization.getId());
        

        FieldPredicate severableFieldPredicate = fieldPredicateRepo.create(TEST_SEVERABLE_FIELD_PREDICATE_VALUE, new Boolean(false));
        FieldValue severableFieldValue = fieldValueRepo.create(severableFieldPredicate);
        severableFieldValue.setValue("Remove me from the submission!");
        Long severableFieldValueId = severableFieldValue.getId();

        Credentials credentials = new Credentials();
                
        credentials.setEmail(submitter.getEmail());
        Submission submission = submissionRepo.create(credentials, organization.getId());
        
        ActionLog severableActionLog = actionLogRepo.create(submission, submissionState, submitter, TEST_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE, attachment, TEST_SUBMISSION_STATE_ACTION_LOG_ENTRY, TEST_SUBMISSION_STATE_ACTION_LOG_FLAG);
        submission = submissionRepo.findOne(submission.getId());
        
        int numSteps = submission.getSubmissionWorkflowSteps().size();
        //TODO:  assert that the brand new submission has only the ones it gets from its org
        assertEquals("The submission didn't get its org's workflow!" , organization.getAggregateWorkflowSteps().size(), submission.getSubmissionWorkflowSteps().size());
        
        submissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, workflowStep);
        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        numSteps++;
        submission.addFieldValue(fieldValue);
        
        severableSubmissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, severableWorkflowStep);
        submission.addSubmissionWorkflowStep(severableSubmissionWorkflowStep);
        numSteps++;
        submission.addFieldValue(severableFieldValue);
        submission.addAttachment(attachment);
        submission.addEmbargoType(embargoType);
        submission.addActionLog(severableActionLog);
        submission = submissionRepo.save(submission);
        
        severableSubmissionWorkflowStep = submissionWorkflowStepRepo.findOne(severableSubmissionWorkflowStep.getId());

        // test remove pointer workflow step and make sure the workflow step is
        // no longer associated but still exists
        submission.removeSubmissionWorkflowStep(severableSubmissionWorkflowStep);
        numSteps--;
        submission = submissionRepo.save(submission);
        assertEquals("The workflow step was not removed!", numSteps, submission.getSubmissionWorkflowSteps().size());
        assertEquals("The workflow step was deleted!", 2, submissionWorkflowStepRepo.count());
        
        long fieldValueCount = fieldValueRepo.count();
        submission.removeFieldValue(severableFieldValue);
        submission = submissionRepo.save(submission);
        //should delete the orphan field value, so decrement our expected count.
        fieldValueCount--;
        //need this to refresh the repo in the transaction.  Otherwise, we can still get the orphan.
        fieldValueRepo.flush();
        FieldValue orphan = fieldValueRepo.findOne(severableFieldValueId);
        assertEquals("The field value was orphaned! ", null , orphan);
        assertEquals("The field value was not removed!", 1, submission.getFieldValues().size());
        assertEquals("The field value was orphaned! ", null , orphan);
        assertEquals("The field value was orphaned!", fieldValueCount, fieldValueRepo.count());


        // From here on we test the actual cascade:

        // test delete submission and make sure:
        // the submission is deleted
        submissionRepo.delete(submission);
        assertEquals("Submission was not deleted!", 0, submissionRepo.count());

        // the submission state is not deleted
        // the organization is not deleted
        assertEquals("The submission state was deleted!", 1, submissionStateRepo.count());
        assertEquals("The organization was deleted!", 1, organizationRepo.count());

        // the field values are deleted
        // the workflow steps are not deleted
        // the actionlog is deleted
        // the attachment is deleted
        assertEquals("The field values were orphaned!", 0, fieldValueRepo.count());
        assertEquals("The workflow steps were deleted!", 2, workflowStepRepo.count());
        assertEquals("The action log was  orphaned!", 0, actionLogRepo.count());
        assertEquals("The attachment were orphaned", 0, attachmentRepo.count());
        assertEquals("The embargo type was deleted!", 1, embargoRepo.count());

        // and, going another level deep on the cascade from field values to
        // their predicates,
        // see that the field predicates were not deleted.
        assertEquals("The field predicates were orphaned!", 2, fieldPredicateRepo.count());
    }
    
    @Test
    public void testUniqueConstraint()
    {
        Credentials credentials = new Credentials();
        
        credentials.setEmail(submitter.getEmail());
        Submission submission = submissionRepo.create(credentials, organization.getId());
        submission.setState(submissionState);
        
        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        submission.addFieldValue(fieldValue);
        submission.addAttachment(attachment);
        submission.addEmbargoType(embargoType);
        
        submissionWorkflowStep = submissionWorkflowStepRepo.findOne(submissionWorkflowStep.getId());
        organization = organizationRepo.findOne(organization.getId());
        submission = submissionRepo.save(submission);
        
        assertEquals("The submission was not retrievable by its unique constraint!", submission, submissionRepo.findBySubmitterAndOrganization(submitter, organization));
    
        try
        {
            submissionRepo.create(credentials, organization.getId());
            assertTrue(false);
        }
        catch(Exception e)
        {
            //good
        }
    }

    @After
    public void cleanUp() {        
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();        
        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });
        submissionWorkflowStepRepo.deleteAll();
        actionLogRepo.deleteAll();
        fieldValueRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        //organizationRepo.deleteAll();
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        assertEquals("The organization repo wouldn't clear!", 0, organizationRepo.count());
        organizationCategoryRepo.deleteAll();
        embargoRepo.deleteAll();
        userRepo.deleteAll();        
        attachmentRepo.deleteAll();
        attachmentTypeRepo.deleteAll();
    }
    
}
