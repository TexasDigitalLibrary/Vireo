package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class OrganizationBasicTest extends AbstractEntityTest{
    
    @Before
    public void setUp() {
        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME);
        parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
    }
    
    @Override
    public void testCreate() {
        assertEquals("The repository did not save the entity!", 1, organizationRepo.count());
        assertEquals("Saved entity did not contain the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("Saved entity did not contain the correct category name!", parentCategory.getName(), parentOrganization.getCategory().getName());
        assertEquals("The organization category did not have the correct Name!", TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName());
    }
    
    @Override
    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplication() {
        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, parentCategory);
        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, parentCategory);
    }
    
    @Test
    public void testEmailWorkflowRuleCreation() {
        createEmailWorkflowRule();
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
        assertEquals("The submissionState does not exist!", 1, submissionStateRepo.count());
        assertEquals("The emailTemplate does not exist!", 1, emailTemplateRepo.count());
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
    }
    
    private void createEmailWorkflowRule() {
        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        emailWorkflowRule = emailWorkflowRuleRepo.create(submissionState, emailRecipient, emailTemplate);
    }
    
    @Test
    public void testAddEmailWorkflowRule() {
        createEmailWorkflowRule();
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
        assertEquals("The emailWorkflowRule does not exist on parent organization!", emailWorkflowRule.getId(), ((EmailWorkflowRule) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId());
    }
    
    
    @Override
    public void testCascade() {
        
    }
    
    @Override
    public void testDelete() {
        long organizationId = parentOrganization.getId();
        assertEquals("Organization not found", true, organizationRepo.exists(organizationId));
        organizationRepo.delete(parentOrganization);
        assertEquals("Organization not deleted", false, organizationRepo.exists(organizationId));
    }
    
    @Test
    public void testWorkflowStepAddition() {
        WorkflowStep workflowStep1 = addWorkflowStepToParentOrganization(TEST_WORKFLOW_STEP_NAME);
        WorkflowStep workflowStep2 = addWorkflowStepToParentOrganization("Step 2");
        WorkflowStep workflowStep3 = addWorkflowStepToParentOrganization("Step 3");
        WorkflowStep workflowStep4 = addWorkflowStepToParentOrganization("Step 4");

        assertEquals("The number of original workflowsteps was off!", 4, parentOrganization.getOriginalWorkflowSteps().size());
        assertEquals("The number of aggregate workflowsteps was off!", 4, parentOrganization.getAggregateWorkflowSteps().size());
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(workflowStep1));
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(workflowStep2));
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(workflowStep3));
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(workflowStep4));
    }
    
    private WorkflowStep addWorkflowStepToParentOrganization(String stepName) {
        WorkflowStep workflowStep = workflowStepRepo.create(stepName, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        return workflowStep;
    }
    
    @After
    public void cleanUp() {

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });

        organizationCategoryRepo.deleteAll();

        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });

        emailWorkflowRuleRepo.deleteAll();
        submissionStateRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }
}
