package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.WorkflowStep;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SubmissionRepoTest extends AbstractRepoTest {

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @BeforeEach
    public void setUp() {
        assertEquals(0, submissionRepo.count(), "The submission repository was not empty!");

        submitter = userRepo.create(TEST_SUBMISSION_SUBMITTER_EMAIL, TEST_SUBMISSION_SUBMITTER_FIRSTNAME, TEST_SUBMISSION_SUBMITTER_LASTNAME, TEST_SUBMISSION_SUBMITTER_ROLE);
        assertEquals(1, userRepo.count(), "The user does not exist!");

        graduateOfficeEmployee1 = userRepo.create(TEST_SUBMISSION_REVIEWER1_EMAIL, TEST_SUBMISSION_REVIEWER1_FIRSTNAME, TEST_SUBMISSION_REVIEWER1_LASTNAME, TEST_SUBMISSION_REVIEWER1_ROLE);
        assertEquals(2, userRepo.count(), "The first reviewer does not exist!");

        graduateOfficeEmployee1 = userRepo.create(TEST_SUBMISSION_REVIEWER2_EMAIL, TEST_SUBMISSION_REVIEWER1_FIRSTNAME, TEST_SUBMISSION_REVIEWER1_LASTNAME, TEST_SUBMISSION_REVIEWER1_ROLE);
        assertEquals(3, userRepo.count(), "The second reviewer does not exist!");

        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);
        assertEquals(1, submissionStatusRepo.count(), "The submission state does not exist!");

        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals(1, organizationCategoryRepo.count(), "The category does not exist!");

        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();
        assertEquals(1, organizationRepo.count(), "The organization does not exist!");

        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(1, workflowStepRepo.count(), "The workflow step does not exist!");

        submissionWorkflowStep = submissionWorkflowStepRepo.cloneWorkflowStep(workflowStep);

        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, false);

        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);

        fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        assertEquals(1, fieldProfileRepo.count(), "The field profile does not exist!");

        SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.create(fieldProfile);
        assertEquals(1, submissionFieldProfileRepo.count(), "The submission field profile does not exist!");

        fieldValue = fieldValueRepo.create(submissionFieldProfile.getFieldPredicate());
        fieldValue.setValue(TEST_FIELD_VALUE);
        fieldValue = fieldValueRepo.save(fieldValue);
        assertEquals(1, fieldValueRepo.count(), "The field value does not exist!");
        assertEquals(TEST_FIELD_VALUE, fieldValue.getValue(), "The field value did not have the correct value!");

        embargoType = embargoRepo.create(TEST_EMBARGO_TYPE_NAME, TEST_EMBARGO_TYPE_DESCRIPTION, TEST_EMBARGO_TYPE_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals(1, embargoRepo.count(), "The embargo type does not exist!");

        testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        assertEquals(1, customActionDefinitionRepo.count(), "The customActionDefinition repository is not 1!");

    }

    @Override
    @Test
    @Transactional
    public void testCreate() throws OrganizationDoesNotAcceptSubmissionsException {

        Submission submission = submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), customActionDefinitionRepo.findAll());

        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        submission.addFieldValue(fieldValue);

        submission = submissionRepo.save(submission);

        CustomActionDefinition cad = customActionDefinitionRepo.create("My Custom Action", true);
        CustomActionValue cav = customActionValueRepo.create(submission, cad, false);

        organization = organizationRepo.findById(organization.getId()).get();

        submission = submissionRepo.read(submission.getId());

        assertEquals(1, submissionRepo.count(), "The repository did not save the submission!");
        assertEquals(submissionStatus, submission.getSubmissionStatus(), "Saved submission did not contain the correct state!");
        assertEquals(submitter, submission.getSubmitter(), "Saved submission did not contain the correct submitter!");
        assertEquals(submission.getOrganization(), organization, "Saved submission did not contain the correct organization!");
        assertEquals(true, submission.getSubmissionWorkflowSteps().contains(submissionWorkflowStep), "Saved submission did not contain the correct submission workflow step!");
        assertEquals(true, submission.getFieldValues().contains(fieldValue), "Saved submission did not contain the correct field value!");
        assertEquals(true, submission.getCustomActionValues().contains(cav), "Saved submission did not contain the correct custom action value!");

    }

    @Test
    public void testAcceptsSubmissions() throws OrganizationDoesNotAcceptSubmissionsException {
        organization.setAcceptsSubmissions(false);

        // expect an exception when creating Submission on the Organization that doesn't accept them
        Assertions.assertThrows(OrganizationDoesNotAcceptSubmissionsException.class, () -> {
            submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), customActionDefinitionRepo.findAll());
        });
    }

    @Override
    @Test
    @Transactional
    public void testDuplication() throws OrganizationDoesNotAcceptSubmissionsException {
        List<CustomActionDefinition> actions = customActionDefinitionRepo.findAll();
        submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), actions);
        assertEquals(1, submissionRepo.count(), "The repository didn't persist submission!");
        submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), actions);
        assertEquals(2, submissionRepo.count(), "The repository didn't create the additional submission!");
    }

    @Override
    @Test
    public void testDelete() throws OrganizationDoesNotAcceptSubmissionsException {

        Submission submission = submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), customActionDefinitionRepo.findAll());

        submissionRepo.delete(submission);
        assertEquals(0, submissionRepo.count(), "Submission did not delete!");
    }

    @Override
    @Test
    @Transactional
    public void testCascade() throws OrganizationDoesNotAcceptSubmissionsException {
        organization = organizationRepo.findById(organization.getId()).get();
        parentCategory = organizationCategoryRepo.findById(organization.getCategory().getId()).get();

        WorkflowStep severableWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, organization);
        SubmissionWorkflowStep severableSubmissionWorkflowStep = submissionWorkflowStepRepo.cloneWorkflowStep(severableWorkflowStep);

        SubmissionFieldProfile severableSubmissionfieldProfile = submissionFieldProfileRepo.create(fieldProfile);
        FieldValue severableFieldValue = fieldValueRepo.create(severableSubmissionfieldProfile.getFieldPredicate());

        severableFieldValue.setValue("Remove me from the submission!");
        Long severableFieldValueId = severableFieldValue.getId();

        Submission submission = submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), customActionDefinitionRepo.findAll());

        ActionLog severableActionLog = actionLogRepo.create(Action.UNDETERMINED, submission, submitter, TEST_SUBMISSION_STATUS_ACTION_LOG_ACTION_DATE, TEST_SUBMISSION_STATUS_ACTION_LOG_ENTRY, TEST_SUBMISSION_STATUS_ACTION_LOG_FLAG);
        submission = submissionRepo.findById(submission.getId()).get();

        int numSteps = submission.getSubmissionWorkflowSteps().size();
        // TODO: assert that the brand new submission has only the ones it gets from its org
        assertEquals(organization.getAggregateWorkflowSteps().size(), submission.getSubmissionWorkflowSteps().size(), "The submission didn't get its org's workflow!");

        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        numSteps++;
        submission.addFieldValue(fieldValue);

        submission.addSubmissionWorkflowStep(severableSubmissionWorkflowStep);
        numSteps++;
        submission.addFieldValue(severableFieldValue);

        submissionRepo.saveAndFlush(submission);

        submission.addActionLog(severableActionLog);
        submission = submissionRepo.save(submission);

        severableSubmissionWorkflowStep = submissionWorkflowStepRepo.findById(severableSubmissionWorkflowStep.getId()).get();

        // test remove pointer workflow step and make sure the workflow step is
        // no longer associated but still exists
        submission.removeSubmissionWorkflowStep(severableSubmissionWorkflowStep);
        numSteps--;
        submission = submissionRepo.save(submission);
        assertEquals(numSteps, submission.getSubmissionWorkflowSteps().size(), "The workflow step was not removed!");

        // 2 submission workflow steps added during the create method!
        assertEquals(4, submissionWorkflowStepRepo.count(), "The workflow step was deleted!");

        long fieldValueCount = fieldValueRepo.count();
        long submissionFieldValueCount = submission.getFieldValues().size();
        submission.removeFieldValue(severableFieldValue);
        submission = submissionRepo.saveAndFlush(submission);

        // should delete the orphan field value, so decrement our expected count.
        fieldValueCount--;
        submissionFieldValueCount--;

        assertEquals(false, fieldValueRepo.findById(severableFieldValueId).isPresent(), "The field value was orphaned! ");
        assertEquals(submissionFieldValueCount, submission.getFieldValues().size(), "The field value was not removed!");
        assertEquals(fieldValueCount, fieldValueRepo.count(), "The field value was orphaned!");

        // From here on we test the actual cascade:

        // test delete submission and make sure:
        // the submission is deleted
        submissionRepo.delete(submission);
        assertEquals(0, submissionRepo.count(), "Submission was not deleted!");

        // the submission state is not deleted
        // the organization is not deleted
        assertEquals(1, submissionStatusRepo.count(), "The submission state was deleted!");
        assertEquals(1, organizationRepo.count(), "The organization was deleted!");

        // the field values are deleted
        // the workflow steps are not deleted
        // the actionlog is deleted
        assertEquals(0, fieldValueRepo.count(), "The field values were orphaned!");
        assertEquals(2, workflowStepRepo.count(), "The workflow steps were deleted!");
        assertEquals(0, actionLogRepo.count(), "The action log was  orphaned!");
        assertEquals(1, embargoRepo.count(), "The embargo type was deleted!");

        // and, going another level deep on the cascade from field values to their predicates,
        // see that the field predicate was not deleted.
        assertEquals(1, fieldPredicateRepo.count(), "The field predicate was deleted!");
    }

    @Test
    @Transactional
    public void testMultiple() throws OrganizationDoesNotAcceptSubmissionsException {

        List<CustomActionDefinition> actions= customActionDefinitionRepo.findAll();

        Submission submission = submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), actions);

        submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        submission.addFieldValue(fieldValue);

        submissionWorkflowStep = submissionWorkflowStepRepo.findById(submissionWorkflowStep.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        submission = submissionRepo.save(submission);

        List<Submission> found = submissionRepo.findAllBySubmitterAndOrganization(submitter, organization);

        assertEquals(1, found.size(), "Did not retrieve exactly one submission by submitter and organization!");
        assertEquals(submission, found.get(0), "The submission was not retrievable by submitter and organization!");

        Submission secondSubmission = submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), actions);

        secondSubmission.addSubmissionWorkflowStep(submissionWorkflowStep);
        submission = submissionRepo.save(submission);

        found = submissionRepo.findAllBySubmitterAndOrganization(submitter, organization);

        assertEquals(2, found.size(), "Did not retrieve exactly two submissions by submitter and organization!");
        assertEquals(secondSubmission, found.get(1), "The submission was not retrievable by submitter and organization!");
        assertNotEquals(found.get(0).getId(), found.get(1).getId(), "The submissions retrieved by submitter and organization are the same!");
    }

}
