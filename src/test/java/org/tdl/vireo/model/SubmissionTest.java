package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

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

        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
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

        attachmentType = attachmentTypeRepo.create(TEST_ATTACHMENT_TYPE_NAME);
        assertEquals("The attachmentType does not exist!", 1, attachmentTypeRepo.count());
        
        attachment = attachmentRepo.create(TEST_ATTACHMENT_NAME, TEST_UUID, attachmentType);
        assertEquals("The attachment does not exist!", 1, attachmentRepo.count());

        embargoType = embargoRepo.create(TEST_EMBARGO_TYPE_NAME, TEST_EMBARGO_TYPE_DESCRIPTION, TEST_EMBARGO_TYPE_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals("The embargo type does not exist!", 1, embargoRepo.count());
    }

    @Override
    public void testCreate() {
        Submission submission = submissionRepo.create(submitter, submissionState);
        submission.addOrganization(organization);
        submission.addSubmissionWorkflowStep(workflowStep);
        submission.addFieldValue(fieldValue);
        submission.addAttachment(attachment);
        submission.addEmbargoType(embargoType);
        submission = submissionRepo.save(submission);

        assertEquals("The repository did not save the submission!", 1, submissionRepo.count());
        assertEquals("Saved submission did not contain the correct state!", submissionState, submission.getState());
        assertEquals("Saved submission did not contain the correct submitter!", submitter, submission.getSubmitter());
        assertEquals("Saved submission did not contain the correct organization!", true, submission.getOrganizations().contains(organization));
        assertEquals("Saved submission did not contain the correct submission workflow step!", true, submission.getSubmissionWorkflowSteps().contains(workflowStep));
        assertEquals("Saved submission did not contain the correct field value!", true, submission.getFieldValues().contains(fieldValue));
        assertEquals("Saved submission did not contain the correct attachment!", true, submission.getAttachments().contains(attachment));
        assertEquals("Saved submission did not contain the correct embargo type!", true, submission.getEmbargoTypes().contains(embargoType));
    }

    @Override
    public void testDuplication() {
        submissionRepo.create(submitter, submissionState);
        assertEquals("The repository didn't persist submission!", 1, submissionRepo.count());
        try {
            submissionRepo.create(submitter, submissionState);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated the submission!", 1, submissionRepo.count());
    }

    @Override
    public void testDelete() {
        Submission submission = submissionRepo.create(submitter, submissionState);
        submissionRepo.delete(submission);
        assertEquals("Submission did not delete!", 0, submissionRepo.count());
    }

    @Override
    @Transactional
    public void testCascade() {
        parentCategory = organizationCategoryRepo.findOne(organization.getCategory().getId());
        
        Organization severableOrganization = organizationRepo.create(TEST_SEVERABLE_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        WorkflowStep severableWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
        

        FieldPredicate severableFieldPredicate = fieldPredicateRepo.create(TEST_SEVERABLE_FIELD_PREDICATE_VALUE);
        FieldValue severableFieldValue = fieldValueRepo.create(severableFieldPredicate);

        Submission submission = submissionRepo.create(submitter, submissionState);

        ActionLog severableActionLog = actionLogRepo.create(submission, submissionState, submitter, TEST_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE, attachment, TEST_SUBMISSION_STATE_ACTION_LOG_ENTRY, TEST_SUBMISSION_STATE_ACTION_LOG_FLAG);
        submission = submissionRepo.findOne(submission.getId());
        
        
        submission.addOrganization(organization);
        submission.addSubmissionWorkflowStep(workflowStep);
        submission.addFieldValue(fieldValue);
        submission.addOrganization(severableOrganization);
        submission.addSubmissionWorkflowStep(severableWorkflowStep);
        submission.addFieldValue(severableFieldValue);
        submission.addAttachment(attachment);
        submission.addEmbargoType(embargoType);
        submission.addActionLog(severableActionLog);
        submission = submissionRepo.save(submission);

        // test remove pointer to organization and make sure the organization is
        // no longer associated but still exists
        submission.removeOrganization(severableOrganization);
        submission = submissionRepo.save(submission);
        assertEquals("The organization was not removed!", 1, submission.getOrganizations().size());
        assertEquals("The organization was deleted!", 2, organizationRepo.count());
        
        
        severableWorkflowStep = workflowStepRepo.findOne(severableWorkflowStep.getId());

        // test remove pointer workflow step and make sure the workflow step is
        // no longer associated but still exists
        submission.removeSubmissionWorkflowStep(severableWorkflowStep);
        submission = submissionRepo.save(submission);
        assertEquals("The workflow step was not removed!", 1, submission.getSubmissionWorkflowSteps().size());
        assertEquals("The workflow step was deleted!", 2, workflowStepRepo.count());
        
        
        // test remove field value
        submission.removeFieldValue(severableFieldValue);
        submission = submissionRepo.save(submission);
        assertEquals("The field value was not removed!", 1, submission.getFieldValues().size());
        assertEquals("The field value was orphaned!", 1, fieldValueRepo.count());

        // From here on we test the actual cascade:

        // test delete submission and make sure:
        // the submission is deleted
        submissionRepo.delete(submission);
        assertEquals("Submission was not deleted!", 0, submissionRepo.count());

        // the submission state is not deleted
        // the organization is not deleted
        assertEquals("The submission state was deleted!", 1, submissionStateRepo.count());
        assertEquals("The organization was deleted!", 2, organizationRepo.count());

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

    @After
    public void cleanUp() {        
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();        
        workflowStepRepo.findAll().forEach(workflowStep -> {
        	workflowStepRepo.delete(workflowStep);
        });
        actionLogRepo.deleteAll();
        fieldValueRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        organizationCategoryRepo.deleteAll();
        embargoRepo.deleteAll();
        userRepo.deleteAll();        
        attachmentRepo.deleteAll();
        attachmentTypeRepo.deleteAll();
    }
    
}
