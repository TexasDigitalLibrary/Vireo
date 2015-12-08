package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public class OrganizationCategoryTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
        assertEquals("The repository did not save the Entity!", 1, organizationCategoryRepo.count());
        assertEquals("Saved entity did not contain the correct Name!", TEST_CATEGORY_NAME, category.getName());
        assertEquals("Saved entity did not contain the correct Level!", TEST_CATEGORY_LEVEL, category.getLevel());
    }

    @Override
    public void testDuplication() {
        organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
        try {
            organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated Entity!", 1, organizationCategoryRepo.count());
    }

    @Override
    public void testDelete() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
        organizationCategoryRepo.delete(category);
        assertEquals("Entity did not delete!", 0, organizationCategoryRepo.count());
    }

    @Override
    @Transactional
    public void testCascade() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
        Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        assertEquals("The organization category repository is empty!", 1, organizationCategoryRepo.count());
        assertEquals("The organization repository is empty!", 1, organizationRepo.count());

        assertEquals("Saved entity did not contain the correct Name!", TEST_ORGANIZATION_NAME, organization.getName());

        assertEquals("Organization category dit not have the correct Name!", TEST_CATEGORY_NAME, organization.getCategory().getName());
        assertEquals("Organization category dit not have the correct Level!", TEST_CATEGORY_LEVEL, organization.getCategory().getLevel());

        category = organizationCategoryRepo.findByNameAndLevel(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);

        Set<Organization> organizations = category.getOrganizations();
        assertEquals("Category does not have the organization!", true, organizations.contains(organization));

        organizationCategoryRepo.delete(category);

        assertEquals("Entity did not delete!", 0, organizationCategoryRepo.count());

        assertEquals("Child entity did not delete by cascade!", 0, organizationRepo.count());
    }

    @After
    public void cleanUp() {
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
    }
}
