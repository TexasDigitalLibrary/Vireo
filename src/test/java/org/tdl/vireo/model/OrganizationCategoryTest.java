package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class OrganizationCategoryTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals("The repository did not save the Entity!", 1, organizationCategoryRepo.count());
        assertEquals("Saved entity did not contain the correct Name!", TEST_CATEGORY_NAME, category.getName());
    }

    @Override
    public void testDuplication() {
        organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        try {
            organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        }
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated Entity!", 1, organizationCategoryRepo.count());
    }

    @Override
    public void testDelete() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organizationCategoryRepo.delete(category);
        assertEquals("Entity did not delete!", 0, organizationCategoryRepo.count());
    }

    @Override
    public void testCascade() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME);

        Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        assertEquals("The organization category repository is empty!", 1, organizationCategoryRepo.count());

        assertEquals("The organization repository is empty!", 1, organizationRepo.count());

        assertEquals("Saved entity did not contain the correct Name!", TEST_ORGANIZATION_NAME, organization.getName());

        assertEquals("Organization category dit not have the correct Name!", TEST_CATEGORY_NAME, organization.getCategory().getName());

        category = organizationCategoryRepo.findByName(TEST_CATEGORY_NAME);

        Set<Organization> organizations = category.getOrganizations();

        assertEquals("Category does not have the organization!", true, organizations.contains(organization));

        organizationCategoryRepo.delete(category);


        assertEquals("Entity did not deleted!", 0, organizationCategoryRepo.count());

        assertEquals("Child entity did not delete by cascade!", 0, organizationRepo.count());

    }

    @After
    public void cleanUp() {

        organizationCategoryRepo.deleteAll();

        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });

    }
}
