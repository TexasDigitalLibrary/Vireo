package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class OrganizationTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The organization repository was not empty!", 0, organizationRepo.count());

        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME);

        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);

        assertEquals("The submissionStatus does not exist!", 1, submissionStatusRepo.count());

        emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);

        assertEquals("The emailTemplate does not exist!", 1, emailTemplateRepo.count());

        emailWorkflowRule = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);

        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
    }

    @Override
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

        assertEquals("The parent organization was not atached to the child!", parentOrganization, childOrganization.getParentOrganization());
        assertEquals("The child organization was not atached to the parent!", 1, parentOrganization.getChildrenOrganizations().size());
        assertEquals("The child organization's id was incorrect!", childOrganization.getId(), ((Organization) parentOrganization.getChildrenOrganizations().toArray()[0]).getId());
        assertEquals("The parent's organization's id was incorrect!", parentOrganization.getId(), childOrganization.getParentOrganization().getId());
    }

    @Override
    public void testDuplication() {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, parentCategory);
        try {
            organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, parentCategory);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }

        for (Organization o : organizationRepo.findAll()) {
            System.out.println("Have org " + o.getName() + " with category " + o.getCategory() + " with parent " + o.getParentOrganization());
        }

        assertEquals("The repository duplicated entity!", 2, organizationRepo.count());
    }

    @Override
    public void testDelete() {
        Organization organization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        organizationRepo.delete(organization);
        assertEquals("Entity did not delete!", 0, organizationRepo.count());
    }

    @Override
    public void testCascade() {

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

        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
        assertEquals("The emailWorkflowRule does not exist on parent organization!", emailWorkflowRule.getId(), ((EmailWorkflowRule) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId());

        // check workflow step
        assertEquals("The parent organization did not add the workflow step!", true, parentOrganization.getOriginalWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The parent organization did not add the workflow step!", true, childOrganization.getOriginalWorkflowSteps().contains(severableChildWorkflowStep));

        // check workflow
        assertEquals("The child organization did not add the workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep));
        assertEquals("The child organization did not add the workflow step!", true, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep));
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
    @Transactional
    public void syncWithParent() {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        
        childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);

        WorkflowStep parentWSOne = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        FieldProfile fieldProfileOne = fieldProfileRepo.create(parentWSOne, fieldPredicate, inputType, false, false, false, false, false, false, null);
        parentWSOne.addAggregateFieldProfile(fieldProfileOne);
        parentWSOne = workflowStepRepo.save(parentWSOne);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep parentWSTwo = workflowStepRepo.create("Parent Step 2", parentOrganization);
        FieldProfile fieldProfileTwo = fieldProfileRepo.create(parentWSTwo, fieldPredicate, inputType, false, false, false, false, false, false, null);
        parentWSOne.addAggregateFieldProfile(fieldProfileTwo);
        parentWSOne = workflowStepRepo.save(parentWSOne);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, childOrganization, childCategory);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        
        WorkflowStep childWSOne = workflowStepRepo.create("Child Step 1", childOrganization);
        FieldProfile fieldProfileThree = fieldProfileRepo.create(childWSOne, fieldPredicate, inputType, false, false, false, false, false, false, null);
        childWSOne.addAggregateFieldProfile(fieldProfileThree);
        childWSOne = workflowStepRepo.save(childWSOne);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        
        WorkflowStep childWSTwo = workflowStepRepo.create("Child Step 2", childOrganization);
        FieldProfile fieldProfileFour = fieldProfileRepo.create(childWSTwo, fieldPredicate, inputType, false, false, false, false, false, false, null);
        childWSTwo.addAggregateFieldProfile(fieldProfileFour);
        childWSTwo = workflowStepRepo.save(childWSTwo);
        
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        WorkflowStep grandChildWSOne = workflowStepRepo.create("Grand Child Step 1", grandChildOrganization);
        FieldProfile fieldProfileFive = fieldProfileRepo.create(grandChildWSOne, fieldPredicate, inputType, false, false, false, false, false, false, null);
        grandChildWSOne.addAggregateFieldProfile(fieldProfileFive);
        grandChildWSOne.removeAggregateFieldProfile(fieldProfileOne);
        grandChildWSOne = workflowStepRepo.save(grandChildWSOne);
        
        childOrganization = organizationRepo.restoreDefaults(childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        assertEquals("childOrganization's original workflow steps are not empty!", 0, childOrganization.getOriginalWorkflowSteps().size());
        assertEquals("childOrganization's aggregated workflow steps do not equal parents!", parentOrganization.getAggregateWorkflowSteps(), childOrganization.getAggregateWorkflowSteps());
        
        assertEquals("grandChildOrganization's original workflow steps are not empty!", 0, grandChildOrganization.getOriginalWorkflowSteps().size());
        assertEquals("grandChildOrganization's aggregated workflow steps do not equal parents!", parentOrganization.getAggregateWorkflowSteps(), grandChildOrganization.getAggregateWorkflowSteps());
 
    }

    @Test
    public void testSanity() {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);

        workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        workflowStepRepo.create("Step 2", parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        workflowStepRepo.create("Step 3", parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        workflowStepRepo.create("Step 4", parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        assertEquals("The number of original workflowsteps was off!", 4, parentOrganization.getOriginalWorkflowSteps().size());
        assertEquals("The number of aggregate workflowsteps was off!", 4, parentOrganization.getAggregateWorkflowSteps().size());
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
        submissionStatusRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }

}
