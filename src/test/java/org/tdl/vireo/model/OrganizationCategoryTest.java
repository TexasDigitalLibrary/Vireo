package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
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
public class OrganizationCategoryTest {

	static final String TEST_CATEGORY_NAME = "Test Category";
	static final int TEST_CATEGORY_LEVEL = 0;
	
	static final String TEST_ORGANIZATION_NAME = "Test Organization";

	@Autowired
	private OrganizationCategoryRepo organizationCategoryRepo;
	
	@Autowired
	private OrganizationRepo organizationRepo;

	@Before
	public void setUp() {
		assertEquals("The repository was not empty!", 0, organizationCategoryRepo.count());
	}

	@Test
	@Order(value = 1)
	public void testCreate() {		
		OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);		
		assertEquals("The repository did not save the Entity!", 1, organizationCategoryRepo.count());
		assertEquals("Saved entity did not contain the correct Name!", TEST_CATEGORY_NAME, category.getName());
		assertEquals("Saved entity did not contain the correct Level!", TEST_CATEGORY_LEVEL, category.getLevel());
	}

	@Test
	@Order(value = 2)
	public void testDuplication() {		
		organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);		
		try {
			organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
		}
		catch(Exception e) { /* SUCCESS */ }		
		assertEquals("The repository duplicated Entity!", 1, organizationCategoryRepo.count());
	}

	@Test
	@Order(value = 3)
	public void testFind() {
		organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);		
		OrganizationCategory category = organizationCategoryRepo.findByNameAndLevel(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);		
		assertNotEquals("Did not find entity!", null, category);		
		assertEquals("Found entity did not contain the correct Name!", TEST_CATEGORY_NAME, category.getName());
		assertEquals("Found entity did not contain the correct Level!", TEST_CATEGORY_LEVEL, category.getLevel());
	}

	@Test
	@Order(value = 4)
	public void testDelete() {		
		OrganizationCategory category = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);		
		organizationCategoryRepo.delete(category);		
		assertEquals("Entity did not delete!", 0, organizationCategoryRepo.count());
	}

	@Test
	@Transactional
	@Order(value = 5)
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
		organizationCategoryRepo.deleteAll();
		organizationRepo.deleteAll();
	}

}
