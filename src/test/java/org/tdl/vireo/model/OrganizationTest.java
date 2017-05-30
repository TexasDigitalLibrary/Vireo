package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

// Test for Organizations without children are in OrganizationBasicTest.java

public class OrganizationTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME);
        parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization = organizationRepo.save(parentOrganization);
        parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        childOrganization = organizationRepo.save(childOrganization);
        childWorkflowStep = workflowStepRepo.create(TEST_CHILD_WORKFLOW_NAME, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        grandChildCategory = organizationCategoryRepo.create(TEST_GRAND_CHILD_CATEGORY_NAME);
        grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, childOrganization, grandChildCategory);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);
        grandChildWorkflowStep = workflowStepRepo.create(TEST_GRAND_CHILD_WORKFLOW_NAME, grandChildOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
    }
    
    @Test
    public void testChildCreation() {
        assertEquals("The parent organization was not atached to the child!", parentOrganization, childOrganization.getParentOrganization());
        assertEquals("The parent organization has no children!", 1, parentOrganization.getChildrenOrganizations().size());
        assertEquals("The child organization's id was incorrect!", childOrganization.getId(), ((Organization) parentOrganization.getChildrenOrganizations().toArray()[0]).getId());
        assertEquals("The parent's organization's id was incorrect!", parentOrganization.getId(), childOrganization.getParentOrganization().getId());
        assertEquals("The child organization category did not have the correct Name!", TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName());
    }
    
    @Test
    public void testGrandChildCreation() {
        assertEquals("The grand child organization was not attached to the child!", childOrganization, grandChildOrganization.getParentOrganization());
        assertEquals("The child organization has no grand children", 1, childOrganization.getChildrenOrganizations().size());
        assertEquals("The grand child's id was incorrect", grandChildOrganization.getId(), ((Organization) childOrganization.getChildrenOrganizations().toArray()[0]).getId());
        assertEquals("The child organization's id was inccorect!", childOrganization.getId(), grandChildOrganization.getParentOrganization().getId());
        assertEquals("The grand child's organization category did not have the correct name", TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName());
    }

    @Override
    public void testCreate() {
        assertEquals("The repository did not save the entity!", 3, organizationRepo.count());
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

    @Override
    public void testDelete() {
        long organizationId = parentOrganization.getId();
        assertEquals("Organization not found", true, organizationRepo.exists(organizationId));
        organizationRepo.delete(parentOrganization);
        assertEquals("Organization not deleted", false, organizationRepo.exists(organizationId));
    }

    @Ignore
    @Override
    public void testCascade() {
        // This test was too generic and was refactored into several different tests.
    }
    
    @Test
    public void testEmailWorkflowRuleCreation() {
        createEmailWorkflowRule();
        assertEquals("The category does not exist!", 3, organizationCategoryRepo.count());
        assertEquals("The submissionState does not exist!", 1, submissionStateRepo.count());
        assertEquals("The emailTemplate does not exist!", 1, emailTemplateRepo.count());
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
    }
    
    @Test
    public void testAddEmailWorkflowRule() {
        createEmailWorkflowRule();
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
        assertEquals("The emailWorkflowRule does not exist on parent organization!", emailWorkflowRule.getId(), ((EmailWorkflowRule) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId());
    }
    
    @Test
    public void testWorkflowStepAddition() {
        WorkflowStep workflowStep2 = addWorkflowStepToParentOrganization("Step 2");
        WorkflowStep workflowStep3 = addWorkflowStepToParentOrganization("Step 3");
        WorkflowStep workflowStep4 = addWorkflowStepToParentOrganization("Step 4");

        assertEquals("The number of original workflowsteps was off!", 4, parentOrganization.getOriginalWorkflowSteps().size());
        assertEquals("The number of aggregate workflowsteps was off!", 4, parentOrganization.getAggregateWorkflowSteps().size());
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(workflowStep2));
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(workflowStep3));
        assertEquals("The expected step was not found", true, parentOrganization.getAggregateWorkflowSteps().contains(workflowStep4));
    }
    
    private WorkflowStep addWorkflowStepToParentOrganization(String stepName) {
        WorkflowStep workflowStep = workflowStepRepo.create(stepName, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        return workflowStep;
    }
    
    private void createEmailWorkflowRule() {
        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        emailWorkflowRule = emailWorkflowRuleRepo.create(submissionState, emailRecipient, emailTemplate);
    }
    
    @Test
    public void testChildOrganizationInheritsWorkflowStep() {
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization did not have its parent's workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The child organization did add its workflow step", true, childOrganization.getAggregateWorkflowSteps().contains(childWorkflowStep));
    }
    
    @Test
    public void testGrandChildOrganizationInheritsWorkflowSteps() {
        assertEquals("The grand child organization did not have the parent's workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The grand child organization did not have the child's workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(childWorkflowStep));
        assertEquals("The grand child organization did not have the its workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(grandChildWorkflowStep));
    }
    
    @Test
    public void testParentChildRelationship() {
        parentOrganization = (Organization) childOrganization.getParentOrganization();
        assertEquals("The parent organization did not have the correct name", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        childOrganization = (Organization) parentOrganization.getChildrenOrganizations().toArray()[0];
        assertEquals("The child organization did not have the correct name", TEST_CHILD_ORGANIZATION_NAME, childOrganization.getName());
        assertTrue("The parent did not have the child organization", parentOrganization.getChildrenOrganizations().contains(childOrganization));
    }
    
    @Test
    public void testParentHasCorrectNumberOfChildren() {
        organizationRepo.create(TEST_SEVERABLE_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());
    }
    
    @Test
    public void testWorkflowStepRemoval() {
        assertEquals("Workflow step not created!", 1, parentOrganization.getAggregateWorkflowSteps().size());
        parentOrganization.removeOriginalWorkflowStep(parentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("Workflow step was not removed", 0, parentOrganization.getAggregateWorkflowSteps().toArray().length);
    }
    
    @Test
    public void testWorkflowStepRemovalIsInherited() {
        parentOrganization.removeOriginalWorkflowStep(parentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("Child did not have parent workflow step removed", false, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
    }
    
    @Test
    public void testWorkflowStepRetainedIfNotOrphaned() {
        parentOrganization.removeOriginalWorkflowStep(parentWorkflowStep);
        assertEquals("Child organization does not have the parent workflow step", true, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("Parent did not have the workflow step removed", false, parentOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertNotEquals("Inherited workflow step was deleted!", null, workflowStepRepo.findByName(parentWorkflowStep.getName()));
    }
    
    @Test
    public void testReattachWorkflowStep() {
        parentOrganization.removeOriginalWorkflowStep(parentWorkflowStep);
        assertEquals("Parent workflow step was not removed", 0, parentOrganization.getAggregateWorkflowSteps().size());
        parentOrganization.addOriginalWorkflowStep(parentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertEquals("Parent organzation has the wrong number of workflow steps", 1, parentOrganization.getAggregateWorkflowSteps().size());
        assertEquals("Grand child organzation has the wrong number of workflow steps", 3, grandChildOrganization.getAggregateWorkflowSteps().size());
    }
    
    @Test
    public void testGrandChildInheritsWorkflowStepFromParent() {
        assertEquals("Grand child did not have parent's workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
    }
    
    @Test
    public void testGrandChildOrganizationInheritsOverwrittenWorkflowSetpFromChild() {
        parentWorkflowStep.setOverrideable(false);
        parentOrganization = organizationRepo.save(parentOrganization);
        childOrganization.removeAggregateWorkflowStep(parentWorkflowStep);
        assertEquals("Child organization did not remove the workflow steps", false, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("Grand child organization is missing parentWorkflowStep!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
    }
    
    @Test
    public void testDetachChildOrganization() {
        childOrganization.removeChildOrganization(grandChildOrganization);
        childOrganization = organizationRepo.save(childOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertNotEquals("The detached grand child organization was deleted!", null, grandChildOrganization);
        assertEquals("The parent organzation has the wrong number of children!", 1, parentOrganization.getChildrenOrganizations().size());
    }
    
    @Test
    public void testReattachChildOrganization() {
        childOrganization.removeChildOrganization(grandChildOrganization);
        childOrganization = organizationRepo.save(childOrganization);
        childOrganization.addChildOrganization(grandChildOrganization);
        childOrganization = organizationRepo.save(childOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organzation has the wrong number of children!", 1, childOrganization.getChildrenOrganizations().size());
    }
    
    @Test
    public void testDeleteChildOrganization() {
        organizationRepo.delete(grandChildOrganization);
        assertEquals("The grand child organization was not deleted!", null, organizationRepo.findOne(grandChildOrganization.getId()));
        assertNotEquals("The child organization was deleted!", null, organizationRepo.findOne(childOrganization.getId()));
        assertEquals("The child organization does not have the correct number of children", 0, organizationRepo.findOne(childOrganization.getId()).getChildrenOrganizations().size());
    }
    
    @Test
    public void testParentDeletionDoesNotDeleteChildren() {
        organizationRepo.delete(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertNotEquals("Child organization was deleted!", null, organizationRepo.findOne(childOrganization.getId()));
        assertNotEquals("Grand child organization was deleted!", null, organizationRepo.findOne(grandChildOrganization.getId()));
        assertEquals("The child organization has a parent when it should not", null, childOrganization.getParentOrganization());
    }

    @Test
    public void testDeleteInterior() {
        organizationRepo.delete(childOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertEquals("Middle organization did not delete!", 2, organizationRepo.count());
        assertEquals("Hierarchy was not preserved when middle was deleted.  Leaf node " + grandChildOrganization.getName() + " (" + grandChildOrganization.getId() + ") didn't get it's grandparent " + parentOrganization.getName() + " (" + parentOrganization.getId() + ") as new parent.", parentOrganization, grandChildOrganization.getParentOrganization());
    }
    
    @Test
    public void testDeletingParentDoesNotDeleteParentWorkflowStep() {
        organizationRepo.delete(parentOrganization);
        assertEquals("The parent workflowstep was not deleted!", null, workflowStepRepo.findOne(parentWorkflowStep.getId()));
        assertEquals("The child contained workflowsteps with a detached originatingOrganization!", false, childOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep));
    }
    
    @Test
    public void testDeleteHierarchy() {
        organizationRepo.delete(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        organizationRepo.delete(childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organizationRepo.delete(grandChildOrganization);
        assertEquals("The parent organization was not deleted!", null, organizationRepo.findOne(parentOrganization.getId()));
        assertEquals("The child organization was not deleted!", null, organizationRepo.findOne(childOrganization.getId()));
        assertEquals("The grand child organization was not deleted!", null, organizationRepo.findOne(grandChildOrganization.getId()));
        assertEquals("All organizations were not deleted", 0, organizationRepo.count());
        assertEquals("The workflow steps were orphaned!", 0, workflowStepRepo.count());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
    }
    
    @Test
    public void testEmailWorkflowRuleCascades() {
        createEmailWorkflowRule();
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);
        organizationRepo.delete(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        organizationRepo.delete(childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organizationRepo.delete(grandChildOrganization);
        assertEquals("The email workflow rule was orphaned!", 0, emailWorkflowRuleRepo.count());
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
