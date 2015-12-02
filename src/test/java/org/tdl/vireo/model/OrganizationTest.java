package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.RecipientType;

public class OrganizationTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The organization repository was not empty!", 0, organizationRepo.count());
        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME, TEST_PARENT_CATEGORY_LEVEL);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submissionState does not exist!", 1, submissionStateRepo.count());
        emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals("The emailTemplate does not exist!", 1, emailTemplateRepo.count());
        emailWorkflowRule = emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
    }

    @Override
    public void testCreate() {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Workflow parentWorkflow = workflowRepo.create(TEST_PARENT_WORKFLOW_NAME, TEST_PARENT_WORKFLOW_INHERITABILITY, parentOrganization);
        parentOrganization.setWorkflow(parentWorkflow);
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The repository did not save the entity!", 1, organizationRepo.count());
        assertEquals("Saved entity did not contain the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("Saved entity did not contain the correct category name!", parentCategory.getName(), parentOrganization.getCategory().getName());
        assertEquals("Saved entity did not contain the correct category level!", parentCategory.getLevel(), parentOrganization.getCategory().getLevel());
        assertEquals("Saved entity did not have the correct workflow name!", TEST_PARENT_WORKFLOW_NAME, parentOrganization.getWorkflow().getName());
        assertEquals("Saved entity did not have the correct workflow inheritability!", TEST_PARENT_WORKFLOW_INHERITABILITY, parentOrganization.getWorkflow().isInheritable());
        assertEquals("Saved entity did not have the emailWorkflow rule!", true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule));
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
    public void testDelete() {
        Organization organization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        organizationRepo.delete(organization);
        assertEquals("Entity did not delete!", 0, organizationRepo.count());
    }

    @Override
    @Transactional
    public void testCascade() {
        // create categories
        OrganizationCategory childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME, TEST_CHILD_CATEGORY_LEVEL);
        OrganizationCategory grandChildCategory = organizationCategoryRepo.create(TEST_GRAND_CHILD_CATEGORY_NAME, TEST_GRAND_CHILD_CATEGORY_LEVEL);

        // create organizations
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, childCategory);
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, grandChildCategory);
        Organization severableParentOrganization = organizationRepo.create(TEST_SEVERABLE_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization severableChildOrganization = organizationRepo.create(TEST_SEVERABLE_CHILD_ORGANIZATION_NAME, childCategory);

        // create and add workflows to organizations
        parentOrganization.setWorkflow(workflowRepo.create(TEST_PARENT_WORKFLOW_NAME, TEST_PARENT_WORKFLOW_INHERITABILITY, parentOrganization));
        childOrganization.setWorkflow(workflowRepo.create(TEST_CHILD_WORKFLOW_NAME, TEST_CHILD_WORKFLOW_INHERITABILITY, childOrganization));
        grandChildOrganization.setWorkflow(workflowRepo.create(TEST_GRAND_CHILD_WORKFLOW_NAME, TEST_GRAND_CHILD_WORKFLOW_INHERITABILITY, grandChildOrganization));
        severableParentOrganization.setWorkflow(workflowRepo.create(TEST_SEVERABLE_PARENT_WORKFLOW_NAME, TEST_SEVERABLE_PARENT_WORKFLOW_INHERITABILITY, severableParentOrganization));
        severableChildOrganization.setWorkflow(workflowRepo.create(TEST_SEVERABLE_CHILD_WORKFLOW_NAME, TEST_SEVERABLE_CHILD_WORKFLOW_INHERITABILITY, severableChildOrganization));

        // add emails to organizations
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        childOrganization.addEmail(TEST_CHILD_EMAIL);
        grandChildOrganization.addEmail(TEST_GRAND_CHILD_EMAIL);
        severableParentOrganization.addEmail(TEST_SEVERABLE_PARENT_EMAIL);
        severableChildOrganization.addEmail(TEST_SEVERABLE_CHILD_EMAIL);

        // add emailworkflow rule to organizations
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);

        // add children to parent organizations
        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization.addChildOrganization(severableChildOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);

        severableParentOrganization.addChildOrganization(childOrganization);
        severableParentOrganization = organizationRepo.save(severableParentOrganization);

        // add children(grand children) to child organization
        childOrganization.addChildOrganization(grandChildOrganization);
        childOrganization = organizationRepo.save(childOrganization);

        // check number of parent organizations of child organization
        assertEquals("The organization had incorrect number of parents!", 2, childOrganization.getParentOrganizations().size());

        // verify parent organization
        // TODO: this sometimes fails because array position is not always the same
        // can add convinence method to extract from list by id to avoid
        parentOrganization = (Organization) childOrganization.getParentOrganizations().toArray()[0]; 
        
        assertEquals("The parent organization did not have the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("The parent organization category dit not have the correct name!", TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName());
        assertEquals("The parent organization category dit not have the correct level!", TEST_PARENT_CATEGORY_LEVEL, parentOrganization.getCategory().getLevel());
        assertEquals("The parent organization has the wrong email workflow rule", true, parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule));

        // check number of child organizations of parent organization
        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // verify child organization
        childOrganization = (Organization) parentOrganization.getChildrenOrganizations().toArray()[0];
        assertEquals("The parent's child organization did not have the correct name!", TEST_CHILD_ORGANIZATION_NAME, childOrganization.getName());
        assertEquals("The parent's child organization category did not have the correct Name!", TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName());
        assertEquals("The parent's child organization category did not have the correct Level!", TEST_CHILD_CATEGORY_LEVEL, childOrganization.getCategory().getLevel());

        // check number of child(grand child) organizations of child
        // organization
        assertEquals("The child organization had incorrect number of grand children!", 1, childOrganization.getChildrenOrganizations().size());

        // verify grand child organization
        grandChildOrganization = (Organization) childOrganization.getChildrenOrganizations().toArray()[0];
        assertEquals("The grand child organization did not have the correct name!", TEST_GRAND_CHILD_ORGANIZATION_NAME, grandChildOrganization.getName());
        assertEquals("The grand child organization category dit not have the correct Name!", TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName());
        assertEquals("The grand child organization category dit not have the correct Level!", TEST_GRAND_CHILD_CATEGORY_LEVEL, grandChildOrganization.getCategory().getLevel());

        // check the number of parent organizations of the grand child
        // organization
        assertEquals("The grand child organization had incorrect number of parents!", 1, grandChildOrganization.getParentOrganizations().size());

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
        organizationRepo.delete(severableChildOrganization);
        assertNotEquals("The parent organization was deleted!", null, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 4, workflowRepo.count());

        // test delete severable parent organization
        organizationRepo.delete(severableParentOrganization);
        assertNotEquals("The child organization was deleted!", null, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization had incorrect number of parents!", 1, childOrganization.getParentOrganizations().size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 3, workflowRepo.count());

        // to test the orphan removal of the email workflow rule
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);

        // test delete parent organization
        organizationRepo.delete(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertNotEquals("The child organization was deleted!", null, childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertNotEquals("The grand child organization was deleted!", null, childOrganization);
        assertEquals("The child organization had incorrect number of parents!", 0, childOrganization.getParentOrganizations().size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 2, workflowRepo.count());
        assertEquals("The email workflow rule was orphaned!", 0, emailWorkflowRuleRepo.count());

        // test delete child organization
        organizationRepo.delete(childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertNotEquals("The grand child organization was deleted!", null, grandChildOrganization);
        assertEquals("The grand child organization had incorrect number of parents!", 0, grandChildOrganization.getParentOrganizations().size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 1, workflowRepo.count());

        // test delete grand child organization
        organizationRepo.delete(grandChildOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertEquals("The grand child organization was deleted!", null, grandChildOrganization);
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 0, workflowRepo.count());

        // test to make sure all organizations are gone
        assertEquals("An organization was not deleted!", 0, organizationRepo.count());
    }

    @After
    public void cleanUp() {
        workflowRepo.deleteAll();
        assertEquals("The workflow orphans were not removed!", 0, workflowRepo.count());
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        emailWorkflowRuleRepo.deleteAll();
        submissionStateRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }

}
