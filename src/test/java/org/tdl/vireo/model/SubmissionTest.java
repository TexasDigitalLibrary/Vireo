package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Calendar;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.AttachmentRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubmissionTest {

    private static final String TEST_SUBMISSION_SUBMITTER_EMAIL = "admin@tdl.org";
    private static final String TEST_SUBMISSION_SUBMITTER_FIRSTNAME = "TDL";
    private static final String TEST_SUBMISSION_SUBMITTER_LASTNAME = "Admin";
    private static final Role TEST_SUBMISSION_SUBMITTER_ROLE = Role.ADMINISTRATOR;

    private static final String TEST_SUBMISSION_STATE_NAME = "Test Parent Submission State";

    private static final boolean TEST_SUBMISSION_STATE_ARCHIVED = true;
    private static final boolean TEST_SUBMISSION_STATE_PUBLISHABLE = true;
    private static final boolean TEST_SUBMISSION_STATE_DELETABLE = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    private static final boolean TEST_SUBMISSION_STATE_ACTIVE = true;

    private static final String TEST_FIELD_PREDICATE_VALUE = "dc.whatever";
    private static final String TEST_DETACHABLE_FIELD_PREDICATE_VALUE = "dc.detachable";

    private static final String TEST_FIELD_VALUE = "Test Field Value";

    private static final String TEST_CATEGORY_NAME = "Test Parent Category";

    private static final int TEST_CATEGORY_LEVEL = 0;

    private static final String TEST_ORGANIZATION_NAME = "Test Parent Organization";
    private static final String TEST_DETACHABLE_ORGANIZATION_NAME = "Test Detachable Organization";

    private static final String TEST_WORKFLOW_STEP_NAME = "Test Parent Workflow Step";
    private static final String TEST_DETACHABLE_WORKFLOW_STEP_NAME = "Test Detachable Workflow Step";
    
    private static final String TEST_SUBMISSION_STATE_ACTION_LOG_ENTRY = "Test ActionLog Entry";
    private static final boolean TEST_SUBMISSION_STATE_ACTION_LOG_FLAG = true;
    private Calendar TEST_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE;
    
    private static final String TEST_ATTACHMENT_NAME = "Test Attachment Name";
    private UUID TEST_UUID = UUID.randomUUID();

    private static User submitter;
    private static SubmissionState submissionState;
    private static FieldPredicate fieldPredicate;
    private static FieldValue fieldValue;
    private static Organization organization;
    private static WorkflowStep workflowStep;
    private static Attachment attachment;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private AttachmentRepo attachmentRepo;
    
    @Autowired
    private ActionLogRepo actionLogRepo;
    
    

    @Before
    public void setUp() {
        assertEquals("The submission repository was not empty!", 0, submissionRepo.count());

        submitter = userRepo.create(TEST_SUBMISSION_SUBMITTER_EMAIL, TEST_SUBMISSION_SUBMITTER_FIRSTNAME, TEST_SUBMISSION_SUBMITTER_LASTNAME, TEST_SUBMISSION_SUBMITTER_ROLE);
        assertEquals("The user does not exist!", 1, userRepo.count());

        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submission state does not exist!", 1, submissionStateRepo.count());

        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        assertEquals("The field predicate does not exist!", 1, fieldPredicateRepo.count());

        fieldValue = fieldValueRepo.create(fieldPredicate);
        fieldValue.setValue(TEST_FIELD_VALUE);
        fieldValue = fieldValueRepo.save(fieldValue);
        assertEquals("The field value does not exist!", 1, fieldValueRepo.count());
        assertEquals("The field value did not have the correct value!", TEST_FIELD_VALUE, fieldValue.getValue());

        OrganizationCategory parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        assertEquals("The organization does not exist!", 1, organizationRepo.count());

        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME);
        assertEquals("The workflow step does not exist!", 1, workflowStepRepo.count());
        
        attachment = attachmentRepo.create(TEST_ATTACHMENT_NAME, TEST_UUID);
        assertEquals("The attachment does not exist!", 1, attachmentRepo.count());
    }

    @Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
        Submission submission = submissionRepo.create(submitter, submissionState);
        submission.addOrganization(organization);
        submission.addSubmissionWorkflowStep(workflowStep);
        submission.addFieldValue(fieldValue);
        submission.addAttachment(attachment);
        submission = submissionRepo.save(submission);

        assertEquals("The repository did not save the submission!", 1, submissionRepo.count());
        assertEquals("Saved submission did not contain the correct state!", submissionState, submission.getState());
        assertEquals("Saved submission did not contain the correct submitter!", submitter, submission.getSubmitter());
        assertEquals("Saved submission did not contain the correct organization!", true, submission.getOrganizations().contains(organization));
        assertEquals("Saved submission did not contain the correct submission workflow step!", true, submission.getSubmissionWorkflowSteps().contains(workflowStep));
        assertEquals("Saved submission did not contain the correct field value!", true, submission.getFieldValues().contains(fieldValue));
        assertEquals("Saved submission did not contain the correct attachment!", true, submission.getAttachments().contains(attachment));
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        submissionRepo.create(submitter, submissionState);
        assertEquals("The repository didn't persist submission!", 1, submissionRepo.count());
        try {
            submissionRepo.create(submitter, submissionState);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated the submission!", 1, submissionRepo.count());
    }

    @Test
    @Order(value = 3)
    public void testFind() {
        submissionRepo.create(submitter, submissionState);
        Submission submission = submissionRepo.findBySubmitterAndState(submitter, submissionState);
        assertNotEquals("Did not find submission!", null, submission);
        assertEquals("Found submission did not contain the correct state!", submissionState, submission.getState());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        Submission submission = submissionRepo.create(submitter, submissionState);
        submissionRepo.delete(submission);
        assertEquals("Submission did not delete!", 0, submissionRepo.count());
    }
    
    @Test
    @Order(value=5)
    public void testRemovePointers() {
    	//TODO:  move the tests of remove methods from the cascade method below into here.
    	
    
    }

    @Test
    @Order(value = 6)
    public void testCascade() {
        Organization detachableOrganization = organizationRepo.create(TEST_DETACHABLE_ORGANIZATION_NAME, organization.getCategory());

        WorkflowStep detachableWorkflowStep = workflowStepRepo.create(TEST_DETACHABLE_WORKFLOW_STEP_NAME);

        FieldPredicate detachableFieldPredicate = fieldPredicateRepo.create(TEST_DETACHABLE_FIELD_PREDICATE_VALUE);
        FieldValue detachableFieldValue = fieldValueRepo.create(detachableFieldPredicate);

        Submission submission = submissionRepo.create(submitter, submissionState);
        
        ActionLog detachableActionLog = actionLogRepo.create(submission, submissionState, submitter, TEST_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE,attachment, TEST_SUBMISSION_STATE_ACTION_LOG_ENTRY, TEST_SUBMISSION_STATE_ACTION_LOG_FLAG) ;
        		
        submission.addOrganization(organization);
        submission.addSubmissionWorkflowStep(workflowStep);
        submission.addFieldValue(fieldValue);
        submission.addOrganization(detachableOrganization);
        submission.addSubmissionWorkflowStep(detachableWorkflowStep);
        submission.addFieldValue(detachableFieldValue);
        submission.addAttachment(attachment);
        submission.addActionLog(detachableActionLog);
        submission = submissionRepo.save(submission);

        // test remove pointer to organization and make sure the organization is no longer associated but still exists
        submission.removeOrganization(detachableOrganization);
        submission = submissionRepo.save(submission);
        assertEquals("The organization was not detached!", 1, submission.getOrganizations().size());
        assertEquals("The organization was deleted!", 2, organizationRepo.count());

        // test remove pointer workflow step and make sure the workflow step is no longer associated but still exists
        submission.removeSubmissionWorkflowStep(detachableWorkflowStep);
        submission = submissionRepo.save(submission);
        assertEquals("The workflow step was not detached!", 1, submission.getSubmissionWorkflowSteps().size());
        assertEquals("The workflow step was deleted!", 1, workflowStepRepo.count());

        // test detach field value
        submission.removeFieldValue(detachableFieldValue);
        submission = submissionRepo.save(submission);
        assertEquals("The field value was not detached!", 1, submission.getFieldValues().size());
        assertEquals("The field value was orphaned!", 1, fieldValueRepo.count());       
        
        
        //From here on we test the actual cascade:
        
        // test delete submission and make sure:
        //the submission is deleted
        submissionRepo.delete(submission);
        assertEquals("Submission was not deleted!", 0, submissionRepo.count());
        
        //the submission state is not deleted
        //the organization is not deleted
        assertEquals("The submission state was deleted!", 1, submissionStateRepo.count());
        assertEquals("The organization was deleted!", 2, organizationRepo.count());
        
        
        //the field values are deleted
        //the workflow steps are deleted
        //the actionlog is deleted
        //the attachment is deleted
        assertEquals("The field values were orphaned!", 0, fieldValueRepo.count());
        assertEquals("The workflow steps were deleted!", 0, workflowStepRepo.count());
        assertEquals("The action log was  orphaned!", 0, actionLogRepo.count());
        assertEquals("The attachment were orphaned",0,attachmentRepo.count());
        
        //and, going another level deep on the cascade from field values to their predicates,
        //see that the field predicates were not deleted.
        assertEquals("The field predicates were orphaned!", 2, fieldPredicateRepo.count());
        
    }

    @After
    public void cleanUp() {
    	attachmentRepo.deleteAll();
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();
        workflowStepRepo.deleteAll();
        actionLogRepo.deleteAll();
        fieldValueRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        userRepo.deleteAll();
    }
}
