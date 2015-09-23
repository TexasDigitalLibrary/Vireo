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
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OrginzationTest {
	
    static final String TEST_CATEGORY_NAME = "Test Category";
    static final int TEST_CATEGORY_LEVEL = 1;

    static final String TEST_ORGANIZATION_NAME = "Test Organization";

    OrganizationCategory category;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @BeforeClass
    public static void init() {

    }

    @Before
    public void setUp() {
        assertEquals("The repository was not empty!", 0, organizationRepo.count());

        category = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);

        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
    }

    @Test
    @Order(value = 1)
    public void testCreate() {
        Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        assertEquals("The repository did not save the Entity!", 1, organizationRepo.count());
        assertEquals("Saved entity did not contain the correct Name!", TEST_ORGANIZATION_NAME, organization.getName());
        assertEquals("Saved entity did not contain the correct Category!", category, organization.getCategory());
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        try {
            organizationRepo.create(TEST_ORGANIZATION_NAME, category);
        } catch (Exception e) {

        }

        assertEquals("The repository duplicated Entity!", 1, organizationRepo.count());

    }

    @Test
    @Order(value = 3)
    public void testFind() {
        organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        Organization organization = organizationRepo.findByNameAndCategory(TEST_ORGANIZATION_NAME, category);

        assertNotEquals("Did not find entity!", null, organization);

        assertEquals("Found entity did not contain the correct Name!", TEST_ORGANIZATION_NAME, organization.getName());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        organizationRepo.delete(organization);

        assertEquals("Entity did not delete!", 0, organizationRepo.count());
    }

    @Test
    @Transactional
    @Order(value = 5)
    public void testCascade() {
        Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        assertEquals("The organization category repository is empty!", 1, organizationCategoryRepo.count());
        assertEquals("The organization repository is empty!", 1, organizationRepo.count());

        assertEquals("Saved entity did not contain the correct Name!", TEST_ORGANIZATION_NAME, organization.getName());

        assertEquals("Organization category did not have the correct Name!", TEST_CATEGORY_NAME, organization.getCategory().getName());
        assertEquals("Organization category did not have the correct Level!", TEST_CATEGORY_LEVEL, organization.getCategory().getLevel());

        // create and add a parent organization with a parent category

        OrganizationCategory parentCategory = organizationCategoryRepo.create("Test Parent Category", 0);
        Organization parentOrganization = organizationRepo.create("Test Parent Organization", parentCategory);

        parentOrganization.addChildOrganization(organization);
        organizationRepo.save(parentOrganization);

        organization = organizationRepo.findOne(organization.getId());

        assertEquals("The organization category repository has incorrect count!", 2, organizationCategoryRepo.count());
        assertEquals("The organization repository has incorrect count!", 2, organizationRepo.count());

        Set<Organization> parentOrganizations = organization.getParentOrganizations();

        assertEquals("The organization had incorrect number of parents!", 1, parentOrganizations.size());

        parentOrganization = organization.getParentById(parentOrganization.getId());

        assertEquals("The parent organization did not have the correct name!", "Test Parent Organization", parentOrganization.getName());

        assertEquals("The parent organization category dit not have the correct Name!", "Test Parent Category", parentOrganization.getCategory().getName());
        assertEquals("The parent organization category dit not have the correct Level!", 0, parentOrganization.getCategory().getLevel());

        // check the parent organizations children

        Set<Organization> parentsChildrenOrganizations = parentOrganization.getChildrenOrganizations();

        assertEquals("The parent organization had incorrect number of children!", 1, parentsChildrenOrganizations.size());

        Organization parentsChildOrganization = (Organization) parentsChildrenOrganizations.toArray()[0];

        assertEquals("The parent's child organization did not have the correct name!", TEST_ORGANIZATION_NAME, parentsChildOrganization.getName());

        assertEquals("The parent's child organization category did not have the correct Name!", TEST_CATEGORY_NAME, parentsChildOrganization.getCategory().getName());
        assertEquals("The parent's child organization category did not have the correct Level!", TEST_CATEGORY_LEVEL, parentsChildOrganization.getCategory().getLevel());

        // create and add a child organization with a child category

        OrganizationCategory childCategory = organizationCategoryRepo.create("Test Child Category", 2);
        Organization childOrganization = organizationRepo.create("Test Child Organization", childCategory);

        organization.addChildOrganization(childOrganization);

        organization = organizationRepo.save(organization);

        assertEquals("The organization category repository has incorrect count!", 3, organizationCategoryRepo.count());
        assertEquals("The organization repository has incorrect count!", 3, organizationRepo.count());

        Set<Organization> childOrganizations = organization.getChildrenOrganizations();

        assertEquals("The organization had incorrect number of parents!", 1, childOrganizations.size());

        childOrganization = organization.getChildById(childOrganization.getId());

        assertEquals("The child organization did not have the correct name!", "Test Child Organization", childOrganization.getName());

        assertEquals("The child organization category dit not have the correct Name!", "Test Child Category", childOrganization.getCategory().getName());
        assertEquals("The child organization category dit not have the correct Level!", 2, childOrganization.getCategory().getLevel());

        // check the child organizations parent

        Set<Organization> childrensParentOrganizations = childOrganization.getParentOrganizations();

        assertEquals("The child organization had incorrect number of parents!", 1, childrensParentOrganizations.size());

        Organization childrensParentOrganization = (Organization) childrensParentOrganizations.toArray()[0];

        assertEquals("The child's parent organization did not have the correct name!", TEST_ORGANIZATION_NAME, childrensParentOrganization.getName());

        assertEquals("The child's parent organization category did not have the correct Name!", TEST_CATEGORY_NAME, childrensParentOrganization.getCategory().getName());
        assertEquals("The child's parent organization category did not have the correct Level!", TEST_CATEGORY_LEVEL, childrensParentOrganization.getCategory().getLevel());

    }

    @After
    public void cleanUp() {
        organizationCategoryRepo.deleteAll();
        organizationRepo.deleteAll();
    }

}
