package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.enums.RecipientType;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OrganizationTest {

    private static final int TEST_PARENT_CATEGORY_LEVEL      = 0;
    private static final int TEST_CHILD_CATEGORY_LEVEL       = 1;
    private static final int TEST_GRAND_CHILD_CATEGORY_LEVEL = 2;

    private static final String TEST_PARENT_CATEGORY_NAME      = "Test Parent Category";
    private static final String TEST_CHILD_CATEGORY_NAME       = "Test Child Category";
    private static final String TEST_GRAND_CHILD_CATEGORY_NAME = "Test Grand Child Category";

    private static final String TEST_PARENT_ORGANIZATION_NAME            = "Test Parent Organization";
    private static final String TEST_CHILD_ORGANIZATION_NAME             = "Test Child Organization";
    private static final String TEST_GRAND_CHILD_ORGANIZATION_NAME       = "Test Grand Child Organization";
    private static final String TEST_DETACHABLE_PARENT_ORGANIZATION_NAME = "Test Detachable Parent Organization";
    private static final String TEST_DETACHABLE_CHILD_ORGANIZATION_NAME  = "Test Detachable Child Organization";

    private static final String TEST_PARENT_WORKFLOW_NAME            = "Test Parent Workflow";
    private static final String TEST_CHILD_WORKFLOW_NAME             = "Test Child Workflow";
    private static final String TEST_GRAND_CHILD_WORKFLOW_NAME       = "Test Grand Child Workflow";
    private static final String TEST_DETACHABLE_PARENT_WORKFLOW_NAME = "Test Detachable Parent Workflow";
    private static final String TEST_DETACHABLE_CHILD_WORKFLOW_NAME  = "Test Detachable Child Workflow";

    private static final boolean TEST_PARENT_WORKFLOW_INHERITABILITY            = true;
    private static final boolean TEST_CHILD_WORKFLOW_INHERITABILITY             = true;
    private static final boolean TEST_GRAND_CHILD_WORKFLOW_INHERITABILITY       = true;
    private static final boolean TEST_DETACHABLE_PARENT_WORKFLOW_INHERITABILITY = true;
    private static final boolean TEST_DETACHABLE_CHILD_WORKFLOW_INHERITABILITY  = true;

    private static final String TEST_PARENT_EMAIL            = "Test Parent Email";
    private static final String TEST_CHILD_EMAIL             = "Test Child Email";
    private static final String TEST_GRAND_CHILD_EMAIL       = "Test Grand Child Email";
    private static final String TEST_DETACHABLE_PARENT_EMAIL = "Test Detachable Parent Email";
    private static final String TEST_DETACHABLE_CHILD_EMAIL  = "Test Detachable Child Email";
    
    private static final String TEST_EMAIL_TEMPLATE_NAME      = "Test Email Template Name";
    private static final String TEST_EMAIL_TEMPLATE_MESSAGE      = "Test Email Template Message";
    private static final String TEST_EMAIL_TEMPLATE_SUBJECT      = "Test Email Template Subject";
    
    private static final String TEST_SUBMISSION_STATE_NAME                   = "Test Parent Submission State";
    private static final String TEST_CHILD_SUBMISSION_STATE_NAME             = "Test Child Submission State";
    private static final String TEST_DETACHABLE_PARENT_SUBMISSION_STATE_NAME = "Test Detachable Parent Submission State";
    private static final String TEST_DETACHABLE_CHILD_SUBMISSION_STATE_NAME  = "Test Detachable Child Submission State";
    
    private static final boolean TEST_SUBMISSION_STATE_ARCHIVED             = true;
    private static final boolean TEST_SUBMISSION_STATE_PUBLISHABLE          = true;
    private static final boolean TEST_SUBMISSION_STATE_DELETABLE            = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT  = true;
    private static final boolean TEST_SUBMISSION_STATE_ACTIVE               = true;

    private OrganizationCategory parentCategory;
    private static SubmissionState submissionState;
    private static EmailTemplate emailTemplate;
    private static EmailWorkflowRule emailWorkflowRule;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private WorkflowRepo workflowRepo;
    
    @Autowired
    private SubmissionStateRepo submissionStateRepo;
    
    @Autowired
	private EmailTemplateRepo emailTemplateRepo;
    
    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Before
    public void setUp() {
        assertEquals("The organization repository was not empty!", 0, organizationRepo.count());
        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME, TEST_PARENT_CATEGORY_LEVEL);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, 
        											 TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submissionState does not exist!", 1, submissionStateRepo.count());
        emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals("The emailTemplate does not exist!", 1, emailTemplateRepo.count());
        emailWorkflowRule = emailWorkflowRuleRepo.create(submissionState, RecipientType.DEPARTMENT, emailTemplate);
        assertEquals("The emailWorkflowRule does not exist!", 1, emailWorkflowRuleRepo.count());
    }

    @Test
    @Order(value = 1)
    public void testCreate() {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Workflow parentWorkflow = workflowRepo.create(TEST_PARENT_WORKFLOW_NAME, TEST_PARENT_WORKFLOW_INHERITABILITY);
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

    @Test
    @Order(value = 2)
    public void testDuplication() {
        organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        try {
            organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, organizationRepo.count());
    }

    @Test
    @Order(value = 3)
    public void testFind() {
        organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization parentOrganization = organizationRepo.findByNameAndCategory(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        assertNotEquals("Did not find entity!", null, parentOrganization);
        assertEquals("Found entity did not contain the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        Organization organization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        organizationRepo.delete(organization);
        assertEquals("Entity did not delete!", 0, organizationRepo.count());
    }

    @Test
    @Transactional
    @Order(value = 5)
    public void testCascade() {
        // create categories
        OrganizationCategory childCategory = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME, TEST_CHILD_CATEGORY_LEVEL);
        OrganizationCategory grandChildCategory = organizationCategoryRepo.create(TEST_GRAND_CHILD_CATEGORY_NAME, TEST_GRAND_CHILD_CATEGORY_LEVEL);

        // create organizations
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, childCategory);
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, grandChildCategory);
        Organization detachableParentOrganization = organizationRepo.create(TEST_DETACHABLE_PARENT_ORGANIZATION_NAME, parentCategory);
        Organization detachableChildOrganization = organizationRepo.create(TEST_DETACHABLE_CHILD_ORGANIZATION_NAME, childCategory);

        // create and add workflows to organizations
        parentOrganization.setWorkflow(workflowRepo.create(TEST_PARENT_WORKFLOW_NAME, TEST_PARENT_WORKFLOW_INHERITABILITY));
        childOrganization.setWorkflow(workflowRepo.create(TEST_CHILD_WORKFLOW_NAME, TEST_CHILD_WORKFLOW_INHERITABILITY));
        grandChildOrganization.setWorkflow(workflowRepo.create(TEST_GRAND_CHILD_WORKFLOW_NAME, TEST_GRAND_CHILD_WORKFLOW_INHERITABILITY));
        detachableParentOrganization.setWorkflow(workflowRepo.create(TEST_DETACHABLE_PARENT_WORKFLOW_NAME, TEST_DETACHABLE_PARENT_WORKFLOW_INHERITABILITY));
        detachableChildOrganization.setWorkflow(workflowRepo.create(TEST_DETACHABLE_CHILD_WORKFLOW_NAME, TEST_DETACHABLE_CHILD_WORKFLOW_INHERITABILITY));

        // add emails to organizations
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        childOrganization.addEmail(TEST_CHILD_EMAIL);
        grandChildOrganization.addEmail(TEST_GRAND_CHILD_EMAIL);
        detachableParentOrganization.addEmail(TEST_DETACHABLE_PARENT_EMAIL);
        detachableChildOrganization.addEmail(TEST_DETACHABLE_CHILD_EMAIL);
        
        //add emailworkflow rule to organizations
        parentOrganization.addEmailWorkflowRule(emailWorkflowRule);
        
        
        // add children to parent organizations
        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization.addChildOrganization(detachableChildOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);

        detachableParentOrganization.addChildOrganization(childOrganization);
        detachableParentOrganization = organizationRepo.save(detachableParentOrganization);

        // add children(grand children) to child organization
        childOrganization.addChildOrganization(grandChildOrganization);
        childOrganization = organizationRepo.save(childOrganization);

        // check number of parent organizations of child organization
        assertEquals("The organization had incorrect number of parents!", 2, childOrganization.getParentOrganizations().size());

        // verify parent organization
        parentOrganization = (Organization) childOrganization.getParentOrganizations().toArray()[0]; //TODO: this sometimes fails because array position is not always the same
        assertEquals("The parent organization did not have the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("The parent organization category dit not have the correct name!", TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName());
        assertEquals("The parent organization category dit not have the correct level!", TEST_PARENT_CATEGORY_LEVEL, parentOrganization.getCategory().getLevel());
        assertEquals("The parent organization has the wrong email workflow rule", true,parentOrganization.getEmailWorkflowRules().contains(emailWorkflowRule));
        
        // check number of child organizations of parent organization
        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // verify child organization
        childOrganization = (Organization) parentOrganization.getChildrenOrganizations().toArray()[0];
        assertEquals("The parent's child organization did not have the correct name!", TEST_CHILD_ORGANIZATION_NAME, childOrganization.getName());
        assertEquals("The parent's child organization category did not have the correct Name!", TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName());
        assertEquals("The parent's child organization category did not have the correct Level!", TEST_CHILD_CATEGORY_LEVEL, childOrganization.getCategory().getLevel());

        // check number of child(grand child) organizations of child organization
        assertEquals("The child organization had incorrect number of grand children!", 1, childOrganization.getChildrenOrganizations().size());

        // verify grand child organization
        grandChildOrganization = (Organization) childOrganization.getChildrenOrganizations().toArray()[0];
        assertEquals("The grand child organization did not have the correct name!", TEST_GRAND_CHILD_ORGANIZATION_NAME, grandChildOrganization.getName());
        assertEquals("The grand child organization category dit not have the correct Name!", TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName());
        assertEquals("The grand child organization category dit not have the correct Level!", TEST_GRAND_CHILD_CATEGORY_LEVEL, grandChildOrganization.getCategory().getLevel());

        // check the number of parent organizations of the grand child organization
        assertEquals("The grand child organization had incorrect number of parents!", 1, grandChildOrganization.getParentOrganizations().size());

        // test detach detachable child organization
        parentOrganization.removeChildOrganization(detachableChildOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);
        detachableChildOrganization = organizationRepo.findOne(detachableChildOrganization.getId());
        assertNotEquals("The detachable child organization was deleted!", null, detachableChildOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());
        parentOrganization.removeEmailWorkflowRule(emailWorkflowRule);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The email workflow rule was not removed from the parent organization",0, parentOrganization.getEmailWorkflowRules().size());
        

        // reattach detachable child organization
        parentOrganization.addChildOrganization(detachableChildOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);
        assertEquals("The parent organization had incorrect number of children!", 2, parentOrganization.getChildrenOrganizations().size());

        // test detach detachable parent organization
        childOrganization.removeParentOrganization(detachableParentOrganization);
        childOrganization = organizationRepo.save(childOrganization);
        detachableParentOrganization = organizationRepo.findOne(detachableParentOrganization.getId());
        assertNotEquals("The detachable parent organization was deleted!", null, detachableParentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization had incorrect number of parents!", 1, childOrganization.getParentOrganizations().size());

        // reattach detachable parent organization
        detachableParentOrganization.addChildOrganization(childOrganization);
        detachableParentOrganization = organizationRepo.save(detachableParentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization had incorrect number of parents!", 2, childOrganization.getParentOrganizations().size());

        // test delete detachable child organization
        organizationRepo.delete(detachableChildOrganization);
        assertNotEquals("The parent organization was deleted!", null, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        assertEquals("The parent organization had incorrect number of children!", 1, parentOrganization.getChildrenOrganizations().size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 4, workflowRepo.count());

        // test delete detachable parent organization
        organizationRepo.delete(detachableParentOrganization);
        assertNotEquals("The child organization was deleted!", null, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertEquals("The child organization had incorrect number of parents!", 1, childOrganization.getParentOrganizations().size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 3, workflowRepo.count());
        
        //to test the orphan removal of the email workflow rule 
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
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();        
        workflowRepo.deleteAll();
        emailWorkflowRuleRepo.deleteAll();
        submissionStateRepo.deleteAll();
        emailTemplateRepo.deleteAll();
        
    }

}
