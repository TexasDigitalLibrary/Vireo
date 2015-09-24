package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;

import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OrginzationTest {
		
	static final int TEST_PARENT_CATEGORY_LEVEL      = 0;
    static final int TEST_CHILD_CATEGORY_LEVEL       = 1;
    static final int TEST_GRAND_CHILD_CATEGORY_LEVEL = 2;
	    
    static final String TEST_PARENT_CATEGORY_NAME      = "Test Parent Category";
    static final String TEST_CHILD_CATEGORY_NAME       = "Test Child Category";
    static final String TEST_GRAND_CHILD_CATEGORY_NAME = "Test Grand Child Category";
    
    static final String TEST_PARENT_ORGANIZATION_NAME            = "Test Parent Organization";
    static final String TEST_CHILD_ORGANIZATION_NAME             = "Test Child Organization";
    static final String TEST_GRAND_CHILD_ORGANIZATION_NAME       = "Test Grand Child Organization";    
    static final String TEST_DETACHABLE_PARENT_ORGANIZATION_NAME = "Test Detachable Parent Organization";
    static final String TEST_DETACHABLE_CHILD_ORGANIZATION_NAME  = "Test Detachable Child Organization";
    
    static final String TEST_PARENT_WORKFLOW_NAME            = "Test Parent Workflow";
    static final String TEST_CHILD_WORKFLOW_NAME             = "Test Child Workflow";
    static final String TEST_GRAND_CHILD_WORKFLOW_NAME       = "Test Grand Child Workflow";
    static final String TEST_DETACHABLE_PARENT_WORKFLOW_NAME = "Test Detachable Parent Workflow";
    static final String TEST_DETACHABLE_CHILD_WORKFLOW_NAME  = "Test Detachable Child Workflow";
    
    static final boolean TEST_PARENT_WORKFLOW_INHERITABILITY            = true;
    static final boolean TEST_CHILD_WORKFLOW_INHERITABILITY             = true;
    static final boolean TEST_GRAND_CHILD_WORKFLOW_INHERITABILITY       = true;
    static final boolean TEST_DETACHABLE_PARENT_WORKFLOW_INHERITABILITY = true;
    static final boolean TEST_DETACHABLE_CHILD_WORKFLOW_INHERITABILITY  = true;
    
    static final String TEST_PARENT_EMAIL            = "Test Parent Email";
    static final String TEST_CHILD_EMAIL             = "Test Child Email";
    static final String TEST_GRAND_CHILD_EMAIL       = "Test Grand Child Email";
    static final String TEST_DETACHABLE_PARENT_EMAIL = "Test Detachable Parent Email";
    static final String TEST_DETACHABLE_CHILD_EMAIL  = "Test Detachable Child Email";
    
    OrganizationCategory parentCategory;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private WorkflowRepo workflowRepo;

    @BeforeClass
    public static void init() {

    }

    @Before
    public void setUp() {
        assertEquals("The organization repository was not empty!", 0, organizationRepo.count());
        parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME, TEST_PARENT_CATEGORY_LEVEL);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
    }

    @Test
    @Order(value = 1)
    public void testCreate() {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);        
        Workflow parentWorkflow = new Workflow(TEST_PARENT_WORKFLOW_NAME, TEST_PARENT_WORKFLOW_INHERITABILITY);
        parentOrganization.setWorkflow(parentWorkflow);
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        assertEquals("The repository did not save the entity!", 1, organizationRepo.count());
        assertEquals("Saved entity did not contain the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("Saved entity did not contain the correct category!", parentCategory, parentOrganization.getCategory());
        assertEquals("Saved entity did not have the correct workflow name!", TEST_PARENT_WORKFLOW_NAME, parentOrganization.getWorkflow().getName());
        assertEquals("Saved entity did not have the correct workflow inheritability!", TEST_PARENT_WORKFLOW_INHERITABILITY, parentOrganization.getWorkflow().isInheritable());
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        try {
            organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        } catch (Exception e) { /* SUCCESS */ }
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
    	OrganizationCategory childCategory      = organizationCategoryRepo.create(TEST_CHILD_CATEGORY_NAME, TEST_CHILD_CATEGORY_LEVEL);
    	OrganizationCategory grandChildCategory = organizationCategoryRepo.create(TEST_GRAND_CHILD_CATEGORY_NAME, TEST_GRAND_CHILD_CATEGORY_LEVEL);
    	
    	// create organizations
    	Organization parentOrganization           = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
    	Organization childOrganization            = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, childCategory);
    	Organization grandChildOrganization       = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, grandChildCategory);
    	Organization detachableParentOrganization = organizationRepo.create(TEST_DETACHABLE_PARENT_ORGANIZATION_NAME, parentCategory);
    	Organization detachableChildOrganization  = organizationRepo.create(TEST_DETACHABLE_CHILD_ORGANIZATION_NAME, childCategory);
    	
    	// create workflows
    	Workflow parentWorkflow           = new Workflow(TEST_PARENT_WORKFLOW_NAME, TEST_PARENT_WORKFLOW_INHERITABILITY);
    	Workflow childWorkflow            = new Workflow(TEST_CHILD_WORKFLOW_NAME, TEST_CHILD_WORKFLOW_INHERITABILITY);
    	Workflow grandChildWorkflow       = new Workflow(TEST_GRAND_CHILD_WORKFLOW_NAME, TEST_GRAND_CHILD_WORKFLOW_INHERITABILITY);
    	Workflow detachableParentWorkflow = new Workflow(TEST_DETACHABLE_PARENT_WORKFLOW_NAME, TEST_DETACHABLE_PARENT_WORKFLOW_INHERITABILITY);
    	Workflow detachableChildWorkflow  = new Workflow(TEST_DETACHABLE_CHILD_WORKFLOW_NAME, TEST_DETACHABLE_CHILD_WORKFLOW_INHERITABILITY);
    	
        // add workflows to organizations
        parentOrganization.setWorkflow(parentWorkflow);
        childOrganization.setWorkflow(childWorkflow);
        grandChildOrganization.setWorkflow(grandChildWorkflow);
        detachableParentOrganization.setWorkflow(detachableParentWorkflow);
        detachableChildOrganization.setWorkflow(detachableChildWorkflow);
        
        // add emails to organizations
        parentOrganization.addEmail(TEST_PARENT_EMAIL);
        childOrganization.addEmail(TEST_CHILD_EMAIL);
        grandChildOrganization.addEmail(TEST_GRAND_CHILD_EMAIL);
        detachableParentOrganization.addEmail(TEST_DETACHABLE_PARENT_EMAIL);
        detachableChildOrganization.addEmail(TEST_DETACHABLE_CHILD_EMAIL);
    	
    	
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
        Set<Organization> parentOrganizations = childOrganization.getParentOrganizations();
        assertEquals("The organization had incorrect number of parents!", 2, parentOrganizations.size());

        // duck typing of parent organization
        parentOrganization = childOrganization.getParentById(parentOrganization.getId());
        assertEquals("The parent organization did not have the correct name!", TEST_PARENT_ORGANIZATION_NAME, parentOrganization.getName());
        assertEquals("The parent organization category dit not have the correct name!", TEST_PARENT_CATEGORY_NAME, parentOrganization.getCategory().getName());
        assertEquals("The parent organization category dit not have the correct level!", TEST_PARENT_CATEGORY_LEVEL, parentOrganization.getCategory().getLevel());

        
        // check number of child organizations of parent organization
        Set<Organization> childOrganizations = parentOrganization.getChildrenOrganizations();
        assertEquals("The parent organization had incorrect number of children!", 2, childOrganizations.size());

        // duck typing of child organization
        childOrganization = parentOrganization.getChildById(childOrganization.getId());
        assertEquals("The parent's child organization did not have the correct name!", TEST_CHILD_ORGANIZATION_NAME, childOrganization.getName());
        assertEquals("The parent's child organization category did not have the correct Name!", TEST_CHILD_CATEGORY_NAME, childOrganization.getCategory().getName());
        assertEquals("The parent's child organization category did not have the correct Level!", TEST_CHILD_CATEGORY_LEVEL, childOrganization.getCategory().getLevel());

        
        // check number of child(grand child) organizations of child organization
        Set<Organization> grandChildOrganizations = childOrganization.getChildrenOrganizations();
        assertEquals("The child organization had incorrect number of grand children!", 1, grandChildOrganizations.size());

        // duck typing of grand child organization
        grandChildOrganization = childOrganization.getChildById(grandChildOrganization.getId());
        assertEquals("The grand child organization did not have the correct name!", TEST_GRAND_CHILD_ORGANIZATION_NAME, grandChildOrganization.getName());
        assertEquals("The grand child organization category dit not have the correct Name!", TEST_GRAND_CHILD_CATEGORY_NAME, grandChildOrganization.getCategory().getName());
        assertEquals("The grand child organization category dit not have the correct Level!", TEST_GRAND_CHILD_CATEGORY_LEVEL, grandChildOrganization.getCategory().getLevel());

        
        // check the number of parent organizations of the grand child organization
        childOrganizations = grandChildOrganization.getParentOrganizations();
        assertEquals("The grand child organization had incorrect number of parents!", 1, childOrganizations.size());
        
        
        // test detach detachable child organization        
        parentOrganization.removeChildOrganization(detachableChildOrganization);
        detachableChildOrganization = organizationRepo.findOne(detachableChildOrganization.getId());
        assertNotEquals("The detachable child organization was deleted!", null, detachableChildOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganizations = parentOrganization.getChildrenOrganizations();
        assertEquals("The parent organization had incorrect number of children!", 1, childOrganizations.size());
        
        // reatach detachable child organization
        parentOrganization.addChildOrganization(detachableChildOrganization);    	
    	parentOrganization = organizationRepo.save(parentOrganization);
        childOrganizations = parentOrganization.getChildrenOrganizations();
        assertEquals("The parent organization had incorrect number of children!", 2, childOrganizations.size());
        
        // test detach detachable parent organization
        childOrganization.removeParentOrganization(detachableParentOrganization);
        detachableParentOrganization = organizationRepo.findOne(detachableParentOrganization.getId());
        assertNotEquals("The detachable parent organization was deleted!", null, detachableParentOrganization);        
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        parentOrganizations = childOrganization.getParentOrganizations();
        assertEquals("The child organization had incorrect number of parents!", 1, parentOrganizations.size());
        
        // reatach detachable parent organization
        detachableParentOrganization.addChildOrganization(childOrganization);
        detachableParentOrganization = organizationRepo.save(detachableParentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        parentOrganizations = childOrganization.getParentOrganizations();
        assertEquals("The child organization had incorrect number of parents!", 2, parentOrganizations.size());
        
        // test delete detachable child organization
        organizationRepo.delete(detachableChildOrganization);
        assertNotEquals("The parent organization was deleted!", null, parentOrganization);
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganizations = parentOrganization.getChildrenOrganizations();
        assertEquals("The parent organization had incorrect number of children!", 1, childOrganizations.size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 4, workflowRepo.count());
        
        // test delete detachable parent organization
        organizationRepo.delete(detachableParentOrganization);
        assertNotEquals("The child organization was deleted!", null, childOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());        
        parentOrganizations = childOrganization.getParentOrganizations();
        assertEquals("The child organization had incorrect number of parents!", 1, parentOrganizations.size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 3, workflowRepo.count());
        
        // test delete parent organization
        organizationRepo.delete(parentOrganization);
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        assertNotEquals("The child organization was deleted!", null, childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertNotEquals("The grand child organization was deleted!", null, childOrganization);
        parentOrganizations = childOrganization.getParentOrganizations();
        assertEquals("The child organization had incorrect number of parents!", 0, parentOrganizations.size());
        assertEquals("An organization category was deleted!", 3, organizationCategoryRepo.count());
        assertEquals("The workflow orphans were not removed!", 2, workflowRepo.count());
                
        // test delete child organization
        organizationRepo.delete(childOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        assertNotEquals("The grand child organization was deleted!", null, grandChildOrganization);        
        childOrganizations = grandChildOrganization.getParentOrganizations();
        assertEquals("The grand child organization had incorrect number of parents!", 0, childOrganizations.size());
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
        organizationCategoryRepo.deleteAll();
        organizationRepo.deleteAll();
    }

}
