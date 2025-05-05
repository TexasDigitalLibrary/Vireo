package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.WorkflowStep;

public class OrganizationRepoTest extends AbstractRepoTest {

    @BeforeEach
    public void setUp() {

        assertEquals(0, organizationRepo.count(), "The organization repository was not empty!");

        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME);

        assertEquals(1, organizationCategoryRepo.count(), "The category does not exist!");

        submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);

        assertEquals(1, submissionStatusRepo.count(), "The submissionStatus does not exist!");

        emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);

        assertEquals(1, emailTemplateRepo.count(), "The emailTemplate does not exist!");

        emailWorkflowRule = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);

        assertEquals(1, emailWorkflowRuleRepo.count(), "The emailWorkflowRule does not exist!");
    }

    @Override
    @Test
    public void testCreate() {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);

        assertEquals(1, organizationRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName(), "Saved entity did not contain the correct name!");
        assertEquals(parentCategory.getName(), parentOrganization.getCategory().getName(), "Saved entity did not contain the correct category name!");
        assertEquals(true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule), "Saved entity did not have the emailWorkflow rule!");

        childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);

        assertEquals(parentOrganization, childOrganization.getParentOrganization(), "The parent organization was not atached to the child!");
        assertEquals(1, parentOrganization.getChildrenOrganizations().size(), "The child organization was not atached to the parent!");
        assertEquals(childOrganization.getId(), ((Organization) parentOrganization.getChildrenOrganizations().toArray()[0]).getId(), "The child organization's id was incorrect!");
        assertEquals(parentOrganization.getId(), childOrganization.getParentOrganization().getId(), "The parent's organization's id was incorrect!");
    }

    @Override
    @Test
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

        assertEquals(2, organizationRepo.count(), "The repository duplicated entity!");
    }

    @Override
    @Test
    public void testDelete() {
        Organization organization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        organizationRepo.delete(organization);
        assertEquals(0, organizationRepo.count(), "Entity did not delete!");
    }

    @Override
    @Test
    public void testCascade() {

        // create categories
        OrganizationCategory childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        OrganizationCategory grandChildCategory = organizationCategoryRepo.create(TEST_GRAND_CHILD_CATEGORY_NAME);

        // create organizations
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        childCategory = organizationCategoryRepo.findById(childCategory.getId()).get();
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, childOrganization, grandChildCategory);
        grandChildCategory = organizationCategoryRepo.findById(grandChildCategory.getId()).get();
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        Organization severableParentOrganization = organizationRepo.create(TEST_SEVERABLE_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization childOrganizationToDisinherit = organizationRepo.create(TEST_SEVERABLE_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        childCategory = organizationCategoryRepo.findById(childCategory.getId()).get();
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        // create organization workflow steps
        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        WorkflowStep childWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, childOrganization);
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        WorkflowStep grandChildWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, grandChildOrganization);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        WorkflowStep severableParentWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        WorkflowStep severableChildWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, childOrganization);
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        // add emails to organizations
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        childOrganization.addEmail(TEST_CHILD_EMAIL);
        grandChildOrganization.addEmail(TEST_GRAND_CHILD_EMAIL);
        severableParentOrganization.addEmail(TEST_SEVERABLE_PARENT_EMAIL);
        childOrganizationToDisinherit.addEmail(TEST_SEVERABLE_CHILD_EMAIL);

        // add emailworkflow rule to organizations
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);

        parentOrganization = organizationRepo.save(parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        severableParentOrganization = organizationRepo.findById(severableParentOrganization.getId()).get();
        childOrganizationToDisinherit = organizationRepo.findById(childOrganizationToDisinherit.getId()).get();

        assertEquals(1, emailWorkflowRuleRepo.count(), "The emailWorkflowRule does not exist!");
        assertEquals(emailWorkflowRule.getId(), ((EmailWorkflowRuleByStatus) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId(), "The emailWorkflowRule does not exist on parent organization!");

        // check workflow step
        assertEquals(true, parentOrganization.getOriginalWorkflowSteps().contains(severableParentWorkflowStep), "The parent organization did not add the workflow step!");
        assertEquals(true, childOrganization.getOriginalWorkflowSteps().contains(severableChildWorkflowStep), "The parent organization did not add the workflow step!");

        // check workflow
        assertEquals(true, childOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep), "The child organization did not add the workflow step!");
        assertEquals(true, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep), "The child organization did not add the workflow step!");
        assertEquals(true, grandChildOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep), "The grand child organization did not add the workflow step!");
        assertEquals(true, grandChildOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep), "The grand child organization did not add the workflow step!");
        assertEquals(true, grandChildOrganization.getAggregateWorkflowSteps().contains(grandChildWorkflowStep), "The grand child organization did not add the workflow step!");

        // check workflow inheritance
        assertEquals(true, childOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "The child organization did not inherit parent workflow step!");
        assertEquals(true, grandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "The grand child organization did not inherit parent workflow step!");
        assertEquals(true, grandChildOrganization.getAggregateWorkflowSteps().contains(childWorkflowStep), "The grand child organization did not inherit child workflow step!");

        // verify parent organization
        parentOrganization = (Organization) childOrganization.getParentOrganization();

        assertEquals(TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName(), "The parent organization did not have the correct name!");
        assertEquals(TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName(), "The parent organization category dit not have the correct name!");
        assertEquals(true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule), "The parent organization has the wrong email workflow rule");

        // check number of child organizations of parent organization
        assertEquals(2, parentOrganization.getChildrenOrganizations().size(), "The parent organization had incorrect number of children!");

        // verify child organization
        assertTrue(parentOrganization.getChildrenOrganizations().contains(childOrganization), "The parent did not have the child organization!");
        assertTrue(parentOrganization.getChildrenOrganizations().contains(childOrganizationToDisinherit), "The parent did not have the severable child organization!");
        assertEquals(TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName(), "The parent's child organization category did not have the correct Name!");

        // check number of child(grand child) organizations of child organization
        assertEquals(1, childOrganization.getChildrenOrganizations().size(), "The child organization had incorrect number of grand children!");

        // verify grand child organization
        assertTrue(childOrganization.getChildrenOrganizations().contains(grandChildOrganization), "The parent did not have the severable child organization!");
        assertEquals(TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName(), "The grand child organization category dit not have the correct Name!");

        // test remove severable workflow steps
        parentOrganization.removeOriginalWorkflowStep(severableParentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        assertEquals(1, parentOrganization.getOriginalWorkflowSteps().size(), "The severable workflow step was not removed!");
        assertNotEquals(null, workflowStepRepo.findByName(severableParentWorkflowStep.getName()), "The sererable workflow step was not deleted!");

        childOrganization.removeOriginalWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        assertEquals(false, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep), "The severable workflow step was not removed!");
        assertNotEquals(null, workflowStepRepo.findByName(severableChildWorkflowStep.getName()), "The sererable workflow step was not deleted!");

        // check workflowstep removal inheritance
        assertEquals(false, parentOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep), "The parent organization did not remove the workflow step!");
        assertEquals(false, childOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep), "The child organization did not remove the workflow step!");
        assertEquals(false, childOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep), "The child organization did not remove the workflow step!");
        assertEquals(false, grandChildOrganization.getAggregateWorkflowSteps().contains(severableParentWorkflowStep), "The grand child organization did not remove the workflow step!");
        assertEquals(false, grandChildOrganization.getAggregateWorkflowSteps().contains(severableChildWorkflowStep), "The grand child organization did not remove the workflow step!");

        severableParentWorkflowStep = workflowStepRepo.findById(severableParentWorkflowStep.getId()).get();

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        // reattach severable workflow steps
        parentOrganization.addOriginalWorkflowStep(severableParentWorkflowStep);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        assertEquals(2, parentOrganization.getAggregateWorkflowSteps().size(), "The parent organization had incorrect number of workflow steps!");

        childOrganization.addOriginalWorkflowStep(severableChildWorkflowStep);
        childOrganization = organizationRepo.save(childOrganization);

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        assertEquals(4, childOrganization.getAggregateWorkflowSteps().size(), "The child organization had incorrect number of workflow steps!");

        // test remove severable child organization
        parentOrganization.removeChildOrganization(childOrganizationToDisinherit);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganizationToDisinherit = organizationRepo.findById(childOrganizationToDisinherit.getId()).get();
        assertNotEquals(null, childOrganizationToDisinherit, "The severable child organization was deleted!");

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        assertEquals(1, parentOrganization.getChildrenOrganizations().size(), "The parent organization had incorrect number of children!");

        parentOrganization.removeEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);

        childOrganization = organizationRepo.findById(childOrganization.getId()).get();
        assertEquals(0, parentOrganization.getEmailWorkflowRules().size(), "The email workflow rule was not removed from the parent organization");

        // reattach severable child organization
        parentOrganization.addChildOrganization(childOrganizationToDisinherit);
        parentOrganization = organizationRepo.save(parentOrganization);
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        assertEquals(2, parentOrganization.getChildrenOrganizations().size(), "The parent organization had incorrect number of children!");

        // test delete severable child organization
        assertNotEquals(null, organizationRepo.findById(childOrganizationToDisinherit.getId()), "The organization does not exist!");

        childOrganizationToDisinherit = organizationRepo.findById(childOrganizationToDisinherit.getId()).get();

        organizationRepo.delete(childOrganizationToDisinherit);

        assertEquals(false, organizationRepo.findById(childOrganizationToDisinherit.getId()).isPresent(), "The organization was not deleted!");
        assertNotEquals(null, parentOrganization, "The parent organization was deleted!");

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        assertEquals(1, parentOrganization.getChildrenOrganizations().size(), "The parent organization had incorrect number of children!");

        // test delete severable parent organization
        assertEquals(true, organizationRepo.findById(severableParentOrganization.getId()).isPresent(), "The organization does not exist!");

        organizationRepo.delete(severableParentOrganization);

        assertEquals(false, organizationRepo.findById(severableParentOrganization.getId()).isPresent(), "The organization was not deleted!");
        assertNotEquals(null, childOrganization, "The child organization was deleted!");

        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        assertEquals(parentOrganization, childOrganization.getParentOrganization(), "The child organization had wrong parents!");

        // to test the orphan removal of the email workflow rule
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);

        parentOrganization = organizationRepo.save(parentOrganization);

        assertEquals(1, emailWorkflowRuleRepo.count(), "The emailWorkflowRule does not exist!");
        assertEquals(emailWorkflowRule.getId(), ((EmailWorkflowRuleByStatus) parentOrganization.getEmailWorkflowRules().toArray()[0]).getId(), "The emailWorkflowRule does not exist on parent organization!");

        // test delete parent organization
        assertNotEquals(null, organizationRepo.findById(parentOrganization.getId()), "The organization does not exist!");

        organizationRepo.delete(parentOrganization);

        assertEquals(false, organizationRepo.findById(parentOrganization.getId()).isPresent(), "The organization was not deleted!");

        assertEquals(false, organizationRepo.findById(parentOrganization.getId()).isPresent(), "The parent organization was not deleted!");
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();
        assertNotEquals(null, childOrganization, "The child organization was deleted!");
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        assertNotEquals(null, grandChildOrganization, "The grand child organization was deleted!");
        assertEquals(null, childOrganization.getParentOrganization(), "The child organization had a parent when it was not supposed to!");

        assertEquals(false, workflowStepRepo.findById(parentWorkflowStep.getId()).isPresent(), "The parent workflowstep was not deleted!");
        assertEquals(false, childOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep), "The child contained workflowsteps with a detached originatingOrganization!");

        // test delete child organization
        assertEquals(true, organizationRepo.findById(childOrganization.getId()).isPresent(), "The organization does not exist!");
        organizationRepo.delete(childOrganization);

        assertEquals(false, organizationRepo.findById(childOrganization.getId()).isPresent(), "The organization was not deleted!");

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        assertNotEquals(null, grandChildOrganization, "The grand child organization was deleted!");
        assertEquals(null, grandChildOrganization.getParentOrganization(), "The grand child organization had a parent when it was not supposed to!");

        assertEquals(false, grandChildOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep), "The grand child contained workflowsteps with a detached originatingOrganization!");

        // test delete grand child organization
        assertEquals(true, organizationRepo.findById(grandChildOrganization.getId()).isPresent(), "The organization does not exist!");
        organizationRepo.delete(grandChildOrganization);

        assertEquals(false, organizationRepo.findById(grandChildOrganization.getId()).isPresent(), "The organization was not deleted!");

        assertEquals(false, organizationRepo.findById(grandChildOrganization.getId()).isPresent(), "The grand child organization was deleted!");

        assertEquals(0, organizationRepo.count(), "All organizations were not deleted!");
        assertEquals(0, workflowStepRepo.count(), "The workflow steps were orphaned!");
        assertEquals(0, emailWorkflowRuleRepo.count(), "The email workflow rule was orphaned!");
        assertEquals(3, organizationCategoryRepo.count(), "An organization category was deleted!");
    }

    @Test
    public void testDeleteInterior() {

        Organization topOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Long topOrganizationId = topOrganization.getId();
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization middleOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, topOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization leafOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, middleOrganization, parentCategory);
        Long leafOrgId = leafOrganization.getId();
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        topOrganization = organizationRepo.findById(topOrganization.getId()).get();
        middleOrganization = organizationRepo.findById(middleOrganization.getId()).get();

        organizationRepo.delete(middleOrganization);

        topOrganization = organizationRepo.findById(topOrganizationId).get();
        leafOrganization = organizationRepo.findById(leafOrgId).get();

        assertEquals(2, organizationRepo.count(), "Middle organization did not delete!");

        assertEquals(topOrganization, leafOrganization.getParentOrganization(), "Hierarchy was not preserved when middle was deleted.  Leaf node " + leafOrganization.getName() + " (" + leafOrganization.getId() + ") didn't get it's grandparent " + topOrganization.getName() + " (" + topOrganization.getId() + ") as new parent.");
    }

    @Test
    @Transactional
    public void syncWithParent() {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);

        childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));
        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);

        WorkflowStep parentWSOne = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        FieldProfile fieldProfileOne = fieldProfileRepo.create(parentWSOne, fieldPredicate, inputType, TEST_GLOSS, false, false, false, false, false, false, null);
        parentWSOne.addAggregateFieldProfile(fieldProfileOne);
        parentWSOne = workflowStepRepo.save(parentWSOne);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep parentWSTwo = workflowStepRepo.create("Parent Step 2", parentOrganization);
        FieldProfile fieldProfileTwo = fieldProfileRepo.create(parentWSTwo, fieldPredicate, inputType, TEST_GLOSS, false, false, false, false, false, false, null);
        parentWSOne.addAggregateFieldProfile(fieldProfileTwo);
        parentWSOne = workflowStepRepo.save(parentWSOne);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentOrganization, childCategory);
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, childOrganization, childCategory);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        WorkflowStep childWSOne = workflowStepRepo.create("Child Step 1", childOrganization);
        FieldProfile fieldProfileThree = fieldProfileRepo.create(childWSOne, fieldPredicate, inputType, TEST_GLOSS, false, false, false, false, false, false, null);
        childWSOne.addAggregateFieldProfile(fieldProfileThree);
        childWSOne = workflowStepRepo.save(childWSOne);
        childOrganization = organizationRepo.findById(childOrganization.getId()).get();

        WorkflowStep childWSTwo = workflowStepRepo.create("Child Step 2", childOrganization);
        FieldProfile fieldProfileFour = fieldProfileRepo.create(childWSTwo, fieldPredicate, inputType, TEST_GLOSS, false, false, false, false, false, false, null);
        childWSTwo.addAggregateFieldProfile(fieldProfileFour);
        childWSTwo = workflowStepRepo.save(childWSTwo);

        childOrganization = organizationRepo.findById(childOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        WorkflowStep grandChildWSOne = workflowStepRepo.create("Grand Child Step 1", grandChildOrganization);
        FieldProfile fieldProfileFive = fieldProfileRepo.create(grandChildWSOne, fieldPredicate, inputType, TEST_GLOSS, false, false, false, false, false, false, null);
        grandChildWSOne.addAggregateFieldProfile(fieldProfileFive);
        grandChildWSOne.removeAggregateFieldProfile(fieldProfileOne);
        grandChildWSOne = workflowStepRepo.save(grandChildWSOne);

        childOrganization = organizationRepo.restoreDefaults(childOrganization);
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        assertEquals(0, childOrganization.getOriginalWorkflowSteps().size(), "childOrganization's original workflow steps are not empty!");
        assertEquals(parentOrganization.getAggregateWorkflowSteps(), childOrganization.getAggregateWorkflowSteps(), "childOrganization's aggregated workflow steps do not equal parents!");

        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "grandChildOrganization's original workflow steps are not empty!");
        assertEquals(parentOrganization.getAggregateWorkflowSteps(), grandChildOrganization.getAggregateWorkflowSteps(), "grandChildOrganization's aggregated workflow steps do not equal parents!");

    }

    @Test
    public void testSanity() {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);

        workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        workflowStepRepo.create("Step 2", parentOrganization);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        workflowStepRepo.create("Step 3", parentOrganization);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        workflowStepRepo.create("Step 4", parentOrganization);
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        assertEquals(4, parentOrganization.getOriginalWorkflowSteps().size(), "The number of original workflowsteps was off!");
        assertEquals(4, parentOrganization.getAggregateWorkflowSteps().size(), "The number of aggregate workflowsteps was off!");
    }

    @AfterEach
    public void cleanUp() {

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });

        organizationRepo.deleteAll();

        organizationCategoryRepo.deleteAll();

        emailWorkflowRuleRepo.deleteAll();
        submissionStatusRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }

}
