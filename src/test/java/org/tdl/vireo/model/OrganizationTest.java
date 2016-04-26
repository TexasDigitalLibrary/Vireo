package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.RecipientType;

public class OrganizationTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The organization repository was not empty!", 0, organizationRepo.count());
        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submissionState does not exist!", 1, submissionStateRepo.count());
        emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals("The emailTemplate does not exist!", 1, emailTemplateRepo.count());
        emailWorkflowRule = emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
    }

    @Override
    @Transactional
    public void testCreate() {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The repository did not save the entity!", 1, organizationRepo.count());
        assertEquals("Saved entity did not contain the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("Saved entity did not contain the correct category name!", parentCategory.getName(), parentOrganization.getCategory().getName());
        assertEquals("Saved entity did not have the emailWorkflow rule!", true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule));
        
        childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        assertEquals("The parent organization was not atached to the child!", 1, childOrganization.getParentOrganizations().size());
        assertEquals("The child organization was not atached to the parent!", 1, parentOrganization.getChildrenOrganizations().size());
        assertEquals("The child organization's id was incorrect!", childOrganization.getId(), ((Organization)parentOrganization.getChildrenOrganizations().toArray()[0]).getId());
        assertEquals("The parent's organization's id was incorrect!", parentOrganization.getId(), ((Organization)childOrganization.getParentOrganizations().toArray()[0]).getId());
    }

    @Override
    public void testDuplication() {
        organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        try {
            organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, organizationRepo.count());
    }

    @Override
    @Transactional
    public void testDelete() {
        Organization organization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        organizationRepo.delete(organization);
        assertEquals("Entity did not delete!", 0, organizationRepo.count());
    }
    
    @Test
    @Order(value=5)
    @Transactional
    public void testDeleteInterior() {
        Organization topOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization middleOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, topOrganization, parentCategory);
        Organization leafOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, middleOrganization, parentCategory);
        
        organizationRepo.delete(middleOrganization);
        assertEquals("Middle organization did not delete!", 2, organizationRepo.count());
        
        assertEquals("Hierarchy was not preserved when middle was deleted.  Leaf node didn't get it's grandparent as new parent.", topOrganization.getId(), ((Organization)leafOrganization.getParentOrganizations().toArray()[0]).getId() );
    }

    @Override
    @Transactional
    public void testCascade() {
        // create categories
        OrganizationCategory childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        OrganizationCategory grandChildCategory = organizationCategoryRepo.create(TEST_GRAND_CHILD_CATEGORY_NAME);

        // create organizations
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, childOrganization, grandChildCategory);
        Organization severableParentOrganization = organizationRepo.create(TEST_SEVERABLE_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization severableChildOrganization = organizationRepo.create(TEST_SEVERABLE_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);

        // create organization workflow steps
        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        WorkflowStep childWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, childOrganization);
        WorkflowStep grandChildWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, grandChildOrganization);
        WorkflowStep severableParentWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, parentOrganization);
        WorkflowStep severableChildWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, childOrganization);
        
        // add emails to organizations
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        childOrganization.addEmail(TEST_CHILD_EMAIL);
        grandChildOrganization.addEmail(TEST_GRAND_CHILD_EMAIL);
        severableParentOrganization.addEmail(TEST_SEVERABLE_PARENT_EMAIL);
        severableChildOrganization.addEmail(TEST_SEVERABLE_CHILD_EMAIL);

        // add emailworkflow rule to organizations
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
        assertEquals("The emailWorkflowRule does not exist on parent organization!", emailWorkflowRule.getId(), ((EmailWorkflowRule) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId());
        
        // check number of parent organizations of child organization
        assertEquals("The organization had incorrect number of parents!", 1, childOrganization.getParentOrganizations().size());
        
        // check workflowsteps 
        assertEquals("The parent organization did not add the workflow step!", true, parentOrganization.getWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The child organization did not add the workflow step!", true, childOrganization.getWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The child organization did not add the workflow step!", true, childOrganization.getWorkflowSteps().contains(severableChildWorkflowStep));
        assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getWorkflowSteps().contains(severableChildWorkflowStep));        
        assertEquals("The grand child organization did not add the workflow step!", true, grandChildOrganization.getWorkflowSteps().contains(grandChildWorkflowStep));
        
        // check workflowstep inheritance
        assertEquals("The child organization did not inherit parent workflow step!", true, childOrganization.getWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The grand child organization did not inherit parent workflow step!", true, grandChildOrganization.getWorkflowSteps().contains(parentWorkflowStep));
        assertEquals("The grand child organization did not inherit child workflow step!", true, grandChildOrganization.getWorkflowSteps().contains(childWorkflowStep));
        
        // verify parent organization
        // TODO: this sometimes fails because array position is not always the same
        // can add convinence method to extract from list by id to avoid
        parentOrganization = (Organization) childOrganization.getParentOrganizations().toArray()[0]; 
        
        assertEquals("The parent organization did not have the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("The parent organization category dit not have the correct name!", TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName());
        assertEquals("The parent organization has the wrong email workflow rule", true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule));

        // check number of child organizations of parent organization
        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // verify child organization
        childOrganization = (Organization) parentOrganization.getChildrenOrganizations().toArray()[0];
        assertEquals("The parent's child organization did not have the correct name!", TEST_CHILD_ORGANIZATION_NAME, childOrganization.getName());
        assertEquals("The parent's child organization category did not have the correct Name!", TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName());

        // check number of child(grand child) organizations of child organization
        assertEquals("The child organization had incorrect number of grand children!", 1, childOrganization.getChildrenOrganizations().size());

        // verify grand child organization
        grandChildOrganization = (Organization) childOrganization.getChildrenOrganizations().toArray()[0];
        assertEquals("The grand child organization did not have the correct name!", TEST_GRAND_CHILD_ORGANIZATION_NAME, grandChildOrganization.getName());
        assertEquals("The grand child organization category dit not have the correct Name!", TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName());

        // check the number of parent organizations of the grand child organization
        assertEquals("The grand child organization had incorrect number of parents!", 1, grandChildOrganization.getParentOrganizations().size());

        
        // test remove severable workflow steps
        parentOrganization.removeWorkflowStep(severableParentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The severable workflow step was not removed!", 1, parentOrganization.getWorkflowSteps().size());
        assertNotEquals("The sererable workflow step was not deleted!", null, workflowStepRepo.findByName(severableParentWorkflowStep.getName()));
        childOrganization.removeWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);
        assertEquals("The severable workflow step was not removed!", 2, childOrganization.getWorkflowSteps().size());
        assertNotEquals("The sererable workflow step was not deleted!", null, workflowStepRepo.findByName(severableChildWorkflowStep.getName()));
        
        
        // check workflowstep removal inheritance
        assertEquals("The parent organization did not remove the workflow step!", false, parentOrganization.getWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The child organization did not remove the workflow step!", false, childOrganization.getWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The child organization did not remove the workflow step!", false, childOrganization.getWorkflowSteps().contains(severableChildWorkflowStep));
        assertEquals("The grand child organization did not remove the workflow step!", false, grandChildOrganization.getWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The grand child organization did not remove the workflow step!", false, grandChildOrganization.getWorkflowSteps().contains(severableChildWorkflowStep));
        
        
        // reattach severable workflow steps
        parentOrganization.addWorkflowStep(severableParentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The parent organization had incorrect number of workflow steps!", 2, parentOrganization.getWorkflowSteps().size());
        childOrganization.addWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);
        assertEquals("The child organization had incorrect number of workflow steps!", 4, childOrganization.getWorkflowSteps().size());
        
        
        // test remove severable child organization
        parentOrganization.removeChildOrganization(severableChildOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);
        severableChildOrganization = organizationRepo.findOne(severableChildOrganization.getId());
        assertNotEquals("The severable child organization was deleted!", null, severableChildOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());
        parentOrganization.removeEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The email workflow rule was not removed from the parent organization", 0, parentOrganization.getEmailWorkflowRules().size());

        // reattach severable child organization
        parentOrganization.addChildOrganization(severableChildOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // test remove severable parent organization
        childOrganization.removeParentOrganization(severableParentOrganization);
        childOrganization = organizationRepo.save(childOrganization);
        severableParentOrganization = organizationRepo.findOne(severableParentOrganization.getId());
        assertNotEquals("The severable parent organization was deleted!", null, severableParentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization had incorrect number of parents!", 1, childOrganization.getParentOrganizations().size());

        // reattach severable parent organization
        severableParentOrganization.addChildOrganization(childOrganization);
        severableParentOrganization = organizationRepo.save(severableParentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization had incorrect number of parents!", 2, childOrganization.getParentOrganizations().size());

        // test delete severable child organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(severableChildOrganization.getId()));
        organizationRepo.delete(severableChildOrganization);
        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(severableChildOrganization.getId()));
        assertNotEquals("The parent organization was deleted!", null, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());
        

        // test delete severable parent organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(severableParentOrganization.getId()));
        organizationRepo.delete(severableParentOrganization);
        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(severableParentOrganization.getId()));
        assertNotEquals("The child organization was deleted!", null, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization had incorrect number of parents!", 1, childOrganization.getParentOrganizations().size());
        
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
        assertEquals("The child organization had incorrect number of parents!", 0, childOrganization.getParentOrganizations().size());
        
        assertEquals("The parent workflowstep was not deleted!", null, workflowStepRepo.findOne(parentWorkflowStep.getId()));
        assertEquals("The child contained workflowsteps with a detached originatingOrganization!", false, childOrganization.getWorkflowSteps().contains(parentWorkflowStep));
        
        
        // test delete child organization
        assertNotEquals("The organization does not exist!", null, organizationRepo.findOne(childOrganization.getId()));
        organizationRepo.delete(childOrganization);
        assertEquals("The organization was not deleted!", null, organizationRepo.findOne(childOrganization.getId()));
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertNotEquals("The grand child organization was deleted!", null, grandChildOrganization);
        assertEquals("The grand child organization had incorrect number of parents!", 0, grandChildOrganization.getParentOrganizations().size());

        assertEquals("The grand child contained workflowsteps with a detached originatingOrganization!", false, grandChildOrganization.getWorkflowSteps().contains(parentWorkflowStep));
        
        
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

    @After
    public void cleanUp() {
        organizationRepo.deleteAll();
        workflowStepRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        emailWorkflowRuleRepo.deleteAll();
        submissionStateRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }

}
