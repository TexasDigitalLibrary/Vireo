package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OrganizationCategoryTest {

	static final String TEST_CATEGORY = "Test Category";
	static final int TEST_CATEGORY_LEVEL = 0;

	@Autowired
	private OrganizationCategoryRepo organizationCategoryRepo;

	@BeforeClass
	public static void init() {

	}

	@Before
	public void setUp() {

	}

	@Test
	public void testCreate() {
		assertEquals("The repository was not empty!", 0, organizationCategoryRepo.count());
		OrganizationCategory organizationCategory = organizationCategoryRepo.create(TEST_CATEGORY, TEST_CATEGORY_LEVEL);
		assertEquals("The repository did not save the Entity!", 1, organizationCategoryRepo.count());
		assertEquals("Saved entity did not contain the correct Name!", TEST_CATEGORY, organizationCategory.getName());
		assertEquals("Saved entity did not contain the correct Level!", TEST_CATEGORY_LEVEL, organizationCategory.getLevel());
	}

	@Test
	public void testDuplication() {

	}

	@Test
	public void testFind() {

	}

	@Test
	public void testDelete() {

	}

	@Test
	public void testCascade() {

	}

	@After
	public void cleanUp() {
		organizationCategoryRepo.deleteAll();
	}

}
