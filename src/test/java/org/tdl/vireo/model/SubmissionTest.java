package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        assertEquals("The organization does not exist!", 1, organizationRepo.count());

        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The workflow step does not exist!", 1, workflowStepRepo.count());

        submissionWorkflowStep = submissionWorkflowStepRepo.cloneWorkflowStep(workflowStep);
        
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, false);
        
        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
        
        fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED);
        assertEquals("The field profile does not exist!", 1, fieldProfileRepo.count());
        
        SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.create(fieldProfile);
        assertEquals("The submission field profile does not exist!", 1, submissionFieldProfileRepo.count());
        
        fieldValue = fieldValueRepo.create(submissionFieldProfile.getFieldPredicate());
        fieldValue.setValue(TEST_FIELD_VALUE);
        fieldValue = fieldValueRepo.save(fieldValue);
        assertEquals("The field value does not exist!", 1, fieldValueRepo.count());
        assertEquals("The field value did not have the correct value!", TEST_FIELD_VALUE, fieldValue.getValue());
        
        attachmentType = deprecatedAttachmentTypeRepo.create(TEST_ATTACHMENT_TYPE_NAME);
        assertEquals("The attachmentType does not exist!", 1, deprecatedAttachmentTypeRepo.count());

        attachment = deprecatedAttachmentRepo.create(TEST_ATTACHMENT_NAME, TEST_UUID, attachmentType);
        assertEquals("The attachment does not exist!", 1, deprecatedAttachmentRepo.count());

        embargoType = embargoRepo.create(TEST_EMBARGO_TYPE_NAME, TEST_EMBARGO_TYPE_DESCRIPTION, TEST_EMBARGO_TYPE_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals("The embargo type does not exist!", 1, embargoRepo.count());

        testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
		assertEquals("The customActionDefinition repository is not 1!", 1, customActionDefinitionRepo.count());

    }

    @Override
    public void testCreate() {

        Submission submission = submissionRepo.create(submitter, organization, submissionState, getCredentials());

        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        submission.addFieldValue(fieldValue);
        submission.addAttachment(attachment);
        submission.addEmbargoType(embargoType);
        
        CustomActionDefinition cad = customActionDefinitionRepo.create("My Custom Action", true);
        CustomActionValue cav = customActionValueRepo.create(submission, cad, false);
        
        organization = organizationRepo.findOne(organization.getId());
        submission = submissionRepo.save(submission);

        assertEquals("The repository did not save the submission!", 1, submissionRepo.count());
        assertEquals("Saved submission did not contain the correct state!", submissionState, submission.getSubmissionState());
        assertEquals("Saved submission did not contain the correct submitter!", submitter, submission.getSubmitter());
        assertEquals("Saved submission did not contain the correct organization!", submission.getOrganization(), organization);
        assertEquals("Saved submission did not contain the correct submission workflow step!", true, submission.getSubmissionWorkflowSteps().contains(submissionWorkflowStep));
        assertEquals("Saved submission did not contain the correct field value!", true, submission.getFieldValues().contains(fieldValue));
        assertEquals("Saved submission did not contain the correct attachment!", true, submission.getAttachments().contains(attachment));
        assertEquals("Saved submission did not contain the correct embargo type!", true, submission.getEmbargoTypes().contains(embargoType));
        assertEquals("Saved submission did not contain the correct custom action value!", true, submission.getCustomActionValues().contains(cav));
            
    }

    @Override
    public void testDuplication() {

        submissionRepo.create(submitter, organization, submissionState, getCredentials());
        assertEquals("The repository didn't persist submission!", 1, submissionRepo.count());
        try {
            submissionRepo.create(submitter, organization, submissionState, getCredentials());
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        
        assertEquals("The repository duplicated the submission!", 1, submissionRepo.count());
    }

    @Override
    public void testDelete() {

        Submission submission = submissionRepo.create(submitter, organization, submissionState, getCredentials());

        submissionRepo.delete(submission);
        assertEquals("Submission did not delete!", 0, submissionRepo.count());
    }

    @Override
    @Transactional
    public void testCascade() {
        organization = organizationRepo.findOne(organization.getId());
        parentCategory = organizationCategoryRepo.findOne(organization.getCategory().getId());

        WorkflowStep severableWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, organization);
        SubmissionWorkflowStep severableSubmissionWorkflowStep = submissionWorkflowStepRepo.cloneWorkflowStep(severableWorkflowStep);

        SubmissionFieldProfile severableSubmissionfieldProfile = submissionFieldProfileRepo.create(fieldProfile);
        FieldValue severableFieldValue = fieldValueRepo.create(severableSubmissionfieldProfile.getFieldPredicate());
        
        severableFieldValue.setValue("Remove me from the submission!");
        Long severableFieldValueId = severableFieldValue.getId();


        Submission submission = submissionRepo.create(submitter, organization, submissionState, getCredentials());

        ActionLog severableActionLog = actionLogRepo.create(submission, submissionState, submitter, TEST_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE, attachment, TEST_SUBMISSION_STATE_ACTION_LOG_ENTRY, TEST_SUBMISSION_STATE_ACTION_LOG_FLAG);
        submission = submissionRepo.findOne(submission.getId());

        int numSteps = submission.getSubmissionWorkflowSteps().size();
        // TODO: assert that the brand new submission has only the ones it gets from its org
        assertEquals("The submission didn't get its org's workflow!", organization.getAggregateWorkflowSteps().size(), submission.getSubmissionWorkflowSteps().size());

        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        numSteps++;
        submission.addFieldValue(fieldValue);

        submission.addSubmissionWorkflowStep(severableSubmissionWorkflowStep);
        numSteps++;
        submission.addFieldValue(severableFieldValue);

        submissionRepo.saveAndFlush(submission);

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
        
        // 2 submission workflow steps added during the create method!
        assertEquals("The workflow step was deleted!", 4, submissionWorkflowStepRepo.count());

        long fieldValueCount = fieldValueRepo.count();
        submission.removeFieldValue(severableFieldValue);
        submission = submissionRepo.saveAndFlush(submission);
        // should delete the orphan field value, so decrement our expected count.
        fieldValueCount--;
        FieldValue orphan = fieldValueRepo.findOne(severableFieldValueId);
        assertEquals("The field value was orphaned! ", null, orphan);
        assertEquals("The field value was not removed!", 1, submission.getFieldValues().size());
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
        assertEquals("The attachment were orphaned", 0, deprecatedAttachmentRepo.count());
        assertEquals("The embargo type was deleted!", 1, embargoRepo.count());

        // and, going another level deep on the cascade from field values to their predicates,
        // see that the field predicate was not deleted.
        assertEquals("The field predicate was deleted!", 1, fieldPredicateRepo.count());
    }

    @Test
    public void testUniqueConstraint() {

        Submission submission = submissionRepo.create(submitter, organization, submissionState, getCredentials());

        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        submission.addFieldValue(fieldValue);
        submission.addAttachment(attachment);
        submission.addEmbargoType(embargoType);

        submissionWorkflowStep = submissionWorkflowStepRepo.findOne(submissionWorkflowStep.getId());
        organization = organizationRepo.findOne(organization.getId());
        submission = submissionRepo.save(submission);

        assertEquals("The submission was not retrievable by its unique constraint!", submission, submissionRepo.findBySubmitterAndOrganization(submitter, organization));

        try {

            submissionRepo.create(submitter, organization, submissionState, getCredentials());
            assertTrue(false);
        } catch (Exception e) { /* SUCCESS */ }
    }

    @After
    public void cleanUp() {
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();
        customActionValueRepo.deleteAll();
        customActionDefinitionRepo.deleteAll();
        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });
        submissionWorkflowStepRepo.deleteAll();
        actionLogRepo.deleteAll();
        fieldValueRepo.deleteAll();        
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        organizationCategoryRepo.deleteAll();
        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });
        submissionFieldProfileRepo.findAll().forEach(fieldProfile -> {
            submissionFieldProfileRepo.delete(fieldProfile);
        });
        fieldPredicateRepo.deleteAll();
        inputTypeRepo.deleteAll();
        embargoRepo.deleteAll();
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        userRepo.deleteAll();
        deprecatedAttachmentRepo.deleteAll();
        deprecatedAttachmentTypeRepo.deleteAll();
    }

}
