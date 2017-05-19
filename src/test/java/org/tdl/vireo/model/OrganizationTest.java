package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class OrganizationTest extends AbstractEntityTest {
    private Organization parentOrganization;
    private Organization childOrganization;
    private Organization grandChildOrganization;

    @Before
    public void setUp() {
        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME);
    }
    
    @Test
    public void testEmailWorkflowRuleCreation() {
        assertEquals("The organization repository was not empty!", 0, organizationRepo.count());
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

    @Override
    public void testCreate() {
        organizationCategory = organizationCategoryRepo.create(TEST_ORGANIZATION_CATEGORY_NAME);
        Organization testOrganization = organizationRepo.create(TEST_ORGANIZATION_NAME, organizationCategory);

        assertEquals("The repository did not save the entity!", 1, organizationRepo.count());
        assertEquals("Saved entity did not contain the correct name!", TEST_ORGANIZATION_NAME, testOrganization.getName());
        assertEquals("Saved entity did not contain the correct category name!", organizationCategory.getName(), testOrganization.getCategory().getName());
    }
    
    @Test
    public void testChildCreation() {
        createParentOrganization();
        childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        
        assertEquals("The parent organization was not atached to the child!", parentOrganization, childOrganization.getParentOrganization());
        assertEquals("The child organization was not atached to the parent!", 1, parentOrganization.getChildrenOrganizations().size());
        assertEquals("The child organization's id was incorrect!", childOrganization.getId(), ((Organization) parentOrganization.getChildrenOrganizations().toArray()[0]).getId());
        assertEquals("The parent's organization's id was incorrect!", parentOrganization.getId(), childOrganization.getParentOrganization().getId());
    }
    
    @Test
    public void testGrandChildCreation() {
        assertEquals("not implemented", true, false);
    }

    private void createParentOrganization() {
        parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);
    }

    @Override
    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplication() {
        createParentOrganization();
        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, parentCategory);
        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, parentCategory);
    }

    @Override
    public void testDelete() {
        organizationCategory = organizationCategoryRepo.create(TEST_ORGANIZATION_CATEGORY_NAME);
        Organization testOrganization = organizationRepo.create(TEST_ORGANIZATION_NAME, organizationCategory);
        long testOrganizationId = testOrganization.getId();
        
        assertEquals("Organization not found", true, organizationRepo.exists(testOrganizationId));
        organizationRepo.delete(testOrganization);
        assertEquals("Organization not deleted", false, organizationRepo.exists(testOrganizationId));
    }

    @Override
    public void testCascade() {
        createEmailWorkflowRule();
        // create categories
        OrganizationCategory childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        OrganizationCategory grandChildCategory = organizationCategoryRepo.create(TEST_GRAND_CHILD_CATEGORY_NAME);

        // create organizations
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        childCategory = organizationCategoryRepo.findOne(childCategory.getId());
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, childOrganization, grandChildCategory);
        grandChildCategory = organizationCategoryRepo.findOne(grandChildCategory.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        Organization severableParentOrganization = organizationRepo.create(TEST_SEVERABLE_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        Organization childOrganizationToDisinherit = organizationRepo.create(TEST_SEVERABLE_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        childCategory = organizationCategoryRepo.findOne(childCategory.getId());
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        // create organization workflow steps
        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        WorkflowStep childWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        WorkflowStep grandChildWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, grandChildOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        WorkflowStep severableParentWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        WorkflowStep severableChildWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        // add emails to organizations
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        childOrganization.addEmail(TEST_CHILD_EMAIL);
        grandChildOrganization.addEmail(TEST_GRAND_CHILD_EMAIL);
        severableParentOrganization.addEmail(TEST_SEVERABLE_PARENT_EMAIL);
        childOrganizationToDisinherit.addEmail(TEST_SEVERABLE_CHILD_EMAIL);

        // add emailworkflow rule to organizations
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);

        parentOrganization = organizationRepo.save(parentOrganization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        severableParentOrganization = organizationRepo.findOne(severableParentOrganization.getId());
        childOrganizationToDisinherit = organizationRepo.findOne(childOrganizationToDisinherit.getId());

//        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
//        assertEquals("The emailWorkflowRule does not exist on parent organization!", emailWorkflowRule.getId(), ((EmailWorkflowRule) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId());

        // check workflow step
//        assertEquals("The parent organization did not add the workflow step!", true, parentOrganization.getOriginalWorkflowSteps().contains(severableParentWorkflowStep));
//        assertEquals("The parent organization did not add the workflow step!", true, childOrganization.getOriginalWorkflowSteps().contains(severableChildWorkflowStep));

        // check workflow
//        assertEquals("The child organization did not add the workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
//        assertEquals("The child organization did not add the workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
        assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
        assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(grandChildWorkflowStep));

        // check workflow inheritance
        assertEquals("The child organization did not inherit parent workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The grand child organization did not inherit parent workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The grand child organization did not inherit child workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(childWorkflowStep));

        // verify parent organization
        parentOrganization = (Organization) childOrganization.getParentOrganization();

        assertEquals("The parent organization did not have the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("The parent organization category dit not have the correct name!", TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName());
        assertEquals("The parent organization has the wrong email workflow rule", true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule));

        // check number of child organizations of parent organization
        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // verify child organization
        assertTrue("The parent did not have the child organization!", parentOrganization.getChildrenOrganizations().contains(childOrganization));
        assertTrue("The parent did not have the severable child organization!", parentOrganization.getChildrenOrganizations().contains(childOrganizationToDisinherit));
        assertEquals("The parent's child organization category did not have the correct Name!", TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName());

        // check number of child(grand child) organizations of child organization
        assertEquals("The child organization had incorrect number of grand children!", 1, childOrganization.getChildrenOrganizations().size());

        // verify grand child organization
        assertTrue("The parent did not have the severable child organization!", childOrganization.getChildrenOrganizations().contains(grandChildOrganization));
        assertEquals("The grand child organization category dit not have the correct Name!", TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName());

        // test remove severable workflow steps
        parentOrganization.removeOriginalWorkflowStep(severableParentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        assertEquals("The severable workflow step was not removed!", 1, parentOrganization.getOriginalWorkflowSteps().size());
        assertNotEquals("The sererable workflow step was not deleted!", null, workflowStepRepo.findByName(severableParentWorkflowStep.getName()));

        childOrganization.removeOriginalWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        assertEquals("The severable workflow step was not removed!", false, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
        assertNotEquals("The sererable workflow step was not deleted!", null, workflowStepRepo.findByName(severableChildWorkflowStep.getName()));

        // check workflowstep removal inheritance
        assertEquals("The parent organization did not remove the workflow step!", false, parentOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The child organization did not remove the workflow step!", false, childOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The child organization did not remove the workflow step!", false, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
        assertEquals("The grand child organization did not remove the workflow step!", false, grandChildOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The grand child organization did not remove the workflow step!", false, grandChildOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));

        severableParentWorkflowStep = workflowStepRepo.findOne(severableParentWorkflowStep.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        // reattach severable workflow steps
        parentOrganization.addOriginalWorkflowStep(severableParentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganization = organizationRepo.findOne(childOrganization.getId());

        assertEquals("The parent organization had incorrect number of workflow steps!", 2, parentOrganization.getAggregateWorkflowSteps().size());

        childOrganization.addOriginalWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        assertEquals("The child organization had incorrect number of workflow steps!", 4, childOrganization.getAggregateWorkflowSteps().size());

        // test remove severable child organization
        parentOrganization.removeChildOrganization(childOrganizationToDisinherit);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganizationToDisinherit = organizationRepo.findOne(childOrganizationToDisinherit.getId());
        assertNotEquals("The severable child organization was deleted!", null, childOrganizationToDisinherit);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());

        parentOrganization.removeEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The email workflow rule was not removed from the parent organization", 0, parentOrganization.getEmailWorkflowRules().size());

        // reattach severable child organization
        parentOrganization.addChildOrganization(childOrganizationToDisinherit);
        parentOrganization = organizationRepo.save(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // test delete severable child organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(childOrganizationToDisinherit.getId()));

        childOrganizationToDisinherit = organizationRepo.findOne(childOrganizationToDisinherit.getId());

        organizationRepo.delete(childOrganizationToDisinherit);

        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(childOrganizationToDisinherit.getId()));
        assertNotEquals("The parent organization was deleted!", null, parentOrganization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());

        // test delete severable parent organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(severableParentOrganization.getId()));

        organizationRepo.delete(severableParentOrganization);

        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(severableParentOrganization.getId()));
        assertNotEquals("The child organization was deleted!", null, childOrganization);

        childOrganization = organizationRepo.findOne(childOrganization.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        assertEquals("The child organization had wrong parents!", parentOrganization, childOrganization.getParentOrganization());

        // to test the orphan removal of the email workflow rule
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);

        parentOrganization = organizationRepo.save(parentOrganization);

        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
        assertEquals("The emailWorkflowRule does not exist on parent organization!", emailWorkflowRule.getId(), ((EmailWorkflowRule) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId());

        // test delete parent organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(parentOrganization.getId()));

        organizationRepo.delete(parentOrganization);

        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(parentOrganization.getId()));
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organization was not deleted!", null, parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertNotEquals("The child organization was deleted!", null, childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertNotEquals("The grand child organization was deleted!", null, grandChildOrganization);
        assertEquals("The child organization had a parent when it was not supposed to!", null, childOrganization.getParentOrganization());

        assertEquals("The parent workflowstep was not deleted!", null, workflowStepRepo.findOne(parentWorkflowStep.getId()));
        assertEquals("The child contained workflowsteps with a detached originatingOrganization!", false, childOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep));

        // test delete child organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(childOrganization.getId()));
        organizationRepo.delete(childOrganization);

        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(childOrganization.getId()));

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        assertNotEquals("The grand child organization was deleted!", null, grandChildOrganization);
        assertEquals("The grand child organization had a parent when it was not supposed to!", null, grandChildOrganization.getParentOrganization());

        assertEquals("The grand child contained workflowsteps with a detached originatingOrganization!", false, grandChildOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep));

        // test delete grand child organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(grandChildOrganization.getId()));
        organizationRepo.delete(grandChildOrganization);

        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(grandChildOrganization.getId()));
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        assertEquals("The grand child organization was deleted!", null, grandChildOrganization);

        assertEquals("All organizations were not deleted!", 0, organizationRepo.count());
        assertEquals("The workflow steps were orphaned!", 0, workflowStepRepo.count());
        assertEquals("The email workflow rule was orphaned!", 0, emailWorkflowRuleRepo.count());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());

    }
    
    @Test
    public void testAddEmailWorkflowRule() {
        createEmailWorkflowRule();
        createParentOrganization();
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
        assertEquals("The emailWorkflowRule does not exist on parent organization!", emailWorkflowRule.getId(), ((EmailWorkflowRule) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId());
    }
    
    @Test
    public void testChildOrganizationInheritsWorkflowStep() {
        createParentOrganization();
        WorkflowStep parentWorkflowStep = addWorkflowStepToParentOrganization(TEST_WORKFLOW_STEP_NAME);
        createChildOrganization();
        WorkflowStep childWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, childOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        
        assertEquals("The child organization did not have its parent's workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The child organization did add its workflow step", true, childOrganization.getAggregateWorkflowSteps().contains(childWorkflowStep));

        
    }
    
    @Test
    public void testGrandChildOrganizationInheritsWorkflowSteps() {
        
    }
    
    @Test
    public void testGrandChildInheritsWorkflowStepFromParent() {
        assertEquals("not implemented", true, false);
    }
    
    @Test
    public void testGrandChildOrganizationInheritsOverwrittenWorkflowSetpFromChild() {
        assertEquals("not implemented", true, false);
    }
    
    private void createChildOrganization() {
        System.out.println("\n\n\n" + parentOrganization + "\n\n\n");
        if (parentOrganization == null) {
            createParentOrganization();
        }
        childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        childCategory = organizationCategoryRepo.findOne(childCategory.getId());
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    }
    
    private void createGrandChildOrganization() {
        if (childOrganization == null) {
            createChildOrganization();
        }
        grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, childOrganization, grandChildCategory);
        grandChildCategory = organizationCategoryRepo.findOne(grandChildCategory.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
    }

    @Test
    public void testDeleteInterior() {

        Organization topOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Long topOrganizationId = topOrganization.getId();
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        Organization middleOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, topOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        Organization leafOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, middleOrganization, parentCategory);
        Long leafOrgId = leafOrganization.getId();
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        topOrganization = organizationRepo.findOne(topOrganization.getId());
        middleOrganization = organizationRepo.findOne(middleOrganization.getId());

        organizationRepo.delete(middleOrganization);

        topOrganization = organizationRepo.findOne(topOrganizationId);
        leafOrganization = organizationRepo.findOne(leafOrgId);

        assertEquals("Middle organization did not delete!", 2, organizationRepo.count());

        assertEquals("Hierarchy was not preserved when middle was deleted.  Leaf node " + leafOrganization.getName() + " (" + leafOrganization.getId() + ") didn't get it's grandparent " + topOrganization.getName() + " (" + topOrganization.getId() + ") as new parent.", topOrganization, leafOrganization.getParentOrganization());
    }

    @Test
    public void testWorkflowStepAddition() {
        createParentOrganization();
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
