package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.AdministrativeGroup;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the administrative group interface
 * 
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 */
public class JpaAdministrativeGroupImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
	}
	
	@After
	public void cleanup() {
		JPA.em().clear();
		context.logout();
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test creating an administrative group
	 */
	@Test
	public void testCreate() {
		
		AdministrativeGroup adminGroup = settingRepo.createAdministrativeGroup("adminGroup");
		
		assertNotNull(adminGroup);
		assertEquals("adminGroup",adminGroup.getName());
		
		adminGroup.delete();
	}
	
	/**
	 * Test creating the administrative group without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createAdministrativeGroup(null);
			fail("Able to create null administrative group");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createAdministrativeGroup("");
			fail("Able to create blank administrative group");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate administrative group
	 */
	@Test
	public void testCreateDuplicate() {
		
		settingRepo.createAdministrativeGroup("adminGroup").save();
		
		try {
			settingRepo.createAdministrativeGroup("adminGroup").save();
			fail("Able to create duplicate administrative group");
		} catch (RuntimeException re) {
			/* yay */
		}
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test the id.
	 */
	@Test
	public void testId() {
		
		AdministrativeGroup adminGroup = settingRepo.createAdministrativeGroup("adminGroup").save();

		assertNotNull(adminGroup.getId());
		
		adminGroup.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		AdministrativeGroup adminGroup = settingRepo.createAdministrativeGroup("adminGroup").save();

		
		AdministrativeGroup retrieved = settingRepo.findAdministrativeGroup(adminGroup.getId());
		
		assertEquals(adminGroup.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all administrative groups
	 */
	@Test
	public void testFindAllAdministrativeGroups() {

		int initialSize = settingRepo.findAllAdministrativeGroups().size();
		
		AdministrativeGroup adminGroup1 = settingRepo.createAdministrativeGroup("adminGroup1").save();
		AdministrativeGroup adminGroup2 = settingRepo.createAdministrativeGroup("adminGroup2").save();

		int postSize = settingRepo.findAllAdministrativeGroups().size();
		
		assertEquals(initialSize +2, postSize);
		
		adminGroup1.delete();
		adminGroup2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		AdministrativeGroup adminGroup = settingRepo.createAdministrativeGroup("adminGroup").save();
		AdministrativeGroup test = settingRepo.createAdministrativeGroup("test").save();
		
		try {
			test.setName(null);
			fail("Able to change name to null");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setName("");
			fail("Able to change name to blank");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setName("adminGroup");
			test.save();
			fail("Able to modify object into duplicate.");
		} catch(RuntimeException re) {
			/* yay */
		}
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test the display order attribute.
	 */
	@Test
	public void testOrder() {
		AdministrativeGroup adminGroup4 = settingRepo.createAdministrativeGroup("adminGroup4").save();
		AdministrativeGroup adminGroup1 = settingRepo.createAdministrativeGroup("adminGroup1").save();
		AdministrativeGroup adminGroup3 = settingRepo.createAdministrativeGroup("adminGroup3").save();
		AdministrativeGroup adminGroup2 = settingRepo.createAdministrativeGroup("adminGroup2").save();
		
		adminGroup1.setDisplayOrder(0);
		adminGroup2.setDisplayOrder(1);
		adminGroup3.setDisplayOrder(3);
		adminGroup4.setDisplayOrder(4);
		
		adminGroup1.save();
		adminGroup2.save();
		adminGroup3.save();
		adminGroup4.save();
		
		List<AdministrativeGroup> adminGroups = settingRepo.findAllAdministrativeGroups();
		
		int index1 = adminGroups.indexOf(adminGroup1);
		int index2 = adminGroups.indexOf(adminGroup2);
		int index3 = adminGroups.indexOf(adminGroup3);
		int index4 = adminGroups.indexOf(adminGroup4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		adminGroup1.delete();
		adminGroup2.delete();
		adminGroup3.delete();
		adminGroup4.delete();
	}
	
	/**
	 * Test that the administrative group is persistence
	 */
	@Test
	public void testPersistance() {
		// Commit and reopen a new transaction because some of the other tests
		// may have caused exceptions which set the transaction to be rolled
		// back.
		if (JPA.em().getTransaction().getRollbackOnly())
			JPA.em().getTransaction().rollback();
		else
			JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		AdministrativeGroup adminGroup = settingRepo.createAdministrativeGroup("adminGroup").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		AdministrativeGroup retrieved = settingRepo.findAdministrativeGroup(adminGroup.getId());
		
		assertEquals(adminGroup.getId(),retrieved.getId());
		assertEquals(adminGroup.getName(),retrieved.getName());
		
		retrieved.delete();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that managers have access and other don't.
	 */
	@Test
	public void testAccess() {
		
		context.login(MockPerson.getManager());
		settingRepo.createAdministrativeGroup("adminGroup").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createAdministrativeGroup("adminGroup").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
