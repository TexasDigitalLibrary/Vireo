package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class OrganizationCategoryTest extends AbstractEntityTest {

    @Override
    @Test
    public void testCreate() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals(1, organizationCategoryRepo.count(), "The repository did not save the Entity!");
        assertEquals(TEST_CATEGORY_NAME, category.getName(), "Saved entity did not contain the correct Name!");
    }

    @Override
    @Test
    public void testDuplication() {
        organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        try {
            organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals(1, organizationCategoryRepo.count(), "The repository duplicated Entity!");
    }

    @Override
    @Test
    public void testDelete() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organizationCategoryRepo.delete(category);
        assertEquals(0, organizationCategoryRepo.count(), "Entity did not delete!");
    }

    @Override
    @Test
    public void testCascade() {
        OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME);

        Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, category);

        assertEquals(1, organizationCategoryRepo.count(), "The organization category repository is empty!");

        assertEquals(1, organizationRepo.count(), "The organization repository is empty!");

        assertEquals(TEST_ORGANIZATION_NAME, organization.getName(), "Saved entity did not contain the correct Name!");

        assertEquals(TEST_CATEGORY_NAME, organization.getCategory().getName(), "Organization category dit not have the correct Name!");

        try {
            organizationCategoryRepo.delete(category);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }

        assertEquals(1, organizationCategoryRepo.count(), "Organization category which is assigned to an organization was deleted!");

        assertEquals(1, organizationRepo.count(), "Organization was deleted!");

        OrganizationCategory anotherCategory = organizationCategoryRepo.create("Another Catagory");

        assertEquals(2, organizationCategoryRepo.count(), "The organization category repository had the incorrect count!");

        organization.setCategory(anotherCategory);

        organizationRepo.save(organization);

        organizationCategoryRepo.delete(category);

        assertEquals(1, organizationCategoryRepo.count(), "Organization category was not deleted!");

        assertEquals(1, organizationRepo.count(), "Organization was deleted!");
    }

    @AfterEach
    public void cleanUp() {
        organizationRepo.deleteAll();

        organizationCategoryRepo.deleteAll();
    }
}
