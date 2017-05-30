package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

    @Ignore
    @Override
    public void testCreate() {
        // Covered in OrganizationBasicTest.java
    }
    
    @Ignore
    @Override
    public void testDuplication() {
        // Covered in OrganizationBasicTest.java
    }    

    @Ignore
    @Override
    public void testDelete() {
        // Covered in OrganizationBasicTest.java
    }

    @Override
    public void testCascade() {
        createEmailWorkflowRule();

        Organization severableParentOrganization = organizationRepo.create(TEST_SEVERABLE_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        Organization childOrganizationToDisinherit = organizationRepo.create(TEST_SEVERABLE_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        childCategory = organizationCategoryRepo.findOne(childCategory.getId());
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep severableParentWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        WorkflowStep severableChildWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

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
        // assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        // assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
        // assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(grandChildWorkflowStep));

        // check workflow inheritance
        // assertEquals("The child organization did not inherit parent workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        // assertEquals("The grand child organization did not inherit parent workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep));
        // assertEquals("The grand child organization did not inherit child workflow step!", true, grandChildOrganization.getAggregateWorkflowSteps().contains(childWorkflowStep));

        // verify parent organization
//        parentOrganization = (Organization) childOrganization.getParentOrganization();
//
//        assertEquals("The parent organization did not have the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
//        assertEquals("The parent organization category dit not have the correct name!", TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName());
//        assertEquals("The parent organization has the wrong email workflow rule", true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule));

        // check number of child organizations of parent organization
        // assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // verify child organization
        // assertTrue("The parent did not have the child organization!", parentOrganization.getChildrenOrganizations().contains(childOrganization));
        // assertTrue("The parent did not have the severable child organization!", parentOrganization.getChildrenOrganizations().contains(childOrganizationToDisinherit));
        // assertEquals("The parent's child organization category did not have the correct Name!", TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName());

        // check number of child(grand child) organizations of child organization
        // assertEquals("The child organization had incorrect number of grand children!", 1, childOrganization.getChildrenOrganizations().size());

        // verify grand child organization
        // assertTrue("The parent did not have the severable child organization!", childOrganization.getChildrenOrganizations().contains(grandChildOrganization));
        // assertEquals("The grand child organization category dit not have the correct Name!", TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName());

        // test remove severable workflow steps
         parentOrganization.removeOriginalWorkflowStep(severableParentWorkflowStep);
         parentOrganization = organizationRepo.save(parentOrganization);
         childOrganization = organizationRepo.findOne(childOrganization.getId());

        // assertEquals("The severable workflow step was not removed!", 1, parentOrganization.getOriginalWorkflowSteps().size());
        // assertNotEquals("The sererable workflow step was not deleted!", null, workflowStepRepo.findByName(severableParentWorkflowStep.getName()));

        childOrganization.removeOriginalWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        // assertEquals("The severable workflow step was not removed!", false, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
        // assertNotEquals("The sererable workflow step was not deleted!", null, workflowStepRepo.findByName(severableChildWorkflowStep.getName()));

        // check workflowstep removal inheritance
        // assertEquals("The parent organization did not remove the workflow step!", false, parentOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        // assertEquals("The child organization did not remove the workflow step!", false, childOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        // assertEquals("The child organization did not remove the workflow step!", false, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
        // assertEquals("The grand child organization did not remove the workflow step!", false, grandChildOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        // assertEquals("The grand child organization did not remove the workflow step!", false, grandChildOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));

        severableParentWorkflowStep = workflowStepRepo.findOne(severableParentWorkflowStep.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        // reattach severable workflow steps
        parentOrganization.addOriginalWorkflowStep(severableParentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganization = organizationRepo.findOne(childOrganization.getId());

        // Reattach workflow step then make sure it stayed.
        assertEquals("The parent organization had incorrect number of workflow steps!", 2, parentOrganization.getAggregateWorkflowSteps().size());

        childOrganization.addOriginalWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        // 
        assertEquals("The child organization had incorrect number of workflow steps!", 4, childOrganization.getAggregateWorkflowSteps().size());

        // test remove severable child organization
        parentOrganization.removeChildOrganization(childOrganizationToDisinherit);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganizationToDisinherit = organizationRepo.findOne(childOrganizationToDisinherit.getId());
        // assertNotEquals("The severable child organization was deleted!", null, childOrganizationToDisinherit);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        // assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());

        parentOrganization.removeEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganization = organizationRepo.findOne(childOrganization.getId());
//        assertEquals("The email workflow rule was not removed from the parent organization", 0, parentOrganization.getEmailWorkflowRules().size());

        // reattach severable child organization
        parentOrganization.addChildOrganization(childOrganizationToDisinherit);
        parentOrganization = organizationRepo.save(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());

//        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // test delete severable child organization
//        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(childOrganizationToDisinherit.getId()));

        childOrganizationToDisinherit = organizationRepo.findOne(childOrganizationToDisinherit.getId());

        organizationRepo.delete(childOrganizationToDisinherit);

        // assertEquals("The organization was not deleted!", null, organizationRepo.findOne(childOrganizationToDisinherit.getId()));
        // assertNotEquals("The parent organization was deleted!", null, parentOrganization);

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
    public void testDeleteInterior() {
        organizationRepo.delete(childOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertEquals("Middle organization did not delete!", 2, organizationRepo.count());
        assertEquals("Hierarchy was not preserved when middle was deleted.  Leaf node " + grandChildOrganization.getName() + " (" + grandChildOrganization.getId() + ") didn't get it's grandparent " + parentOrganization.getName() + " (" + parentOrganization.getId() + ") as new parent.", parentOrganization, grandChildOrganization.getParentOrganization());
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
