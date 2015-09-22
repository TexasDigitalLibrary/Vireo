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
import org.tdl.vireo.model.repo.OrganizationRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OrginzationTest {
	
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
		
		category = organizationCategoryRepo.create("Test Category", 0);
		
		assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
	}
	
	@Test
	public void testCreate() {
		Organization organization = organizationRepo.create(TEST_ORGANIZATION_NAME, category);
		
		assertEquals("The repository did not save the Entity!", 1, organizationRepo.count());
		assertEquals("Saved entity did not contain the correct Name!", TEST_ORGANIZATION_NAME, organization.getName());
		assertEquals("Saved entity did not contain the correct Category!", category, organization.getCategory());
	}
	
	@Test
	public void testDuplication() {
		organizationRepo.create(TEST_ORGANIZATION_NAME, category);
				
		try {
			organizationRepo.create(TEST_ORGANIZATION_NAME, category);
		}
		catch(Exception e) {
			
		}
		
		assertEquals("The repository duplicated Entity!", 1, organizationRepo.count());
		
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
		organizationRepo.deleteAll();
	}

}
