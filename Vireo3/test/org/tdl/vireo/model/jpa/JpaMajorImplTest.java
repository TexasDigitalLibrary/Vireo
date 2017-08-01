package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the major interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaMajorImplTest extends UnitTest {
	
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
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test creating an major
	 */
	@Test
	public void testCreate() {
		
		Major major = settingRepo.createMajor("major");
		
		assertNotNull(major);
		assertEquals("major",major.getName());
		
		major.delete();
	}
	
	/**
	 * Test creating the major without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createMajor(null);
			fail("Able to create null major");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createMajor("");
			fail("Able to create blank major");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate major
	 */
	@Test
	public void testCreateDuplicate() {
		
		Major major = settingRepo.createMajor("major").save();
		
		try {
			settingRepo.createMajor("major").save();
			fail("Able to create duplicate major");
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
		
		Major major = settingRepo.createMajor("major").save();

		assertNotNull(major.getId());
		
		major.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		Major major = settingRepo.createMajor("major").save();

		
		Major retrieved = settingRepo.findMajor(major.getId());
		
		assertEquals(major.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all majors
	 */
	@Test
	public void testFindAllMajors() {

		int initialSize = settingRepo.findAllMajors().size();
		
		Major major1 = settingRepo.createMajor("major1").save();
		Major major2 = settingRepo.createMajor("major2").save();

		int postSize = settingRepo.findAllMajors().size();
		
		assertEquals(initialSize +2, postSize);
		
		major1.delete();
		major2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		Major major = settingRepo.createMajor("major").save();
		Major test = settingRepo.createMajor("test").save();
		
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
			test.setName("major");
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
		Major major4 = settingRepo.createMajor("major4").save();
		Major major1 = settingRepo.createMajor("major1").save();
		Major major3 = settingRepo.createMajor("major3").save();
		Major major2 = settingRepo.createMajor("major2").save();
		
		major1.setDisplayOrder(0);
		major2.setDisplayOrder(1);
		major3.setDisplayOrder(3);
		major4.setDisplayOrder(4);
		
		major1.save();
		major2.save();
		major3.save();
		major4.save();
		
		List<Major> majors = settingRepo.findAllMajors();
		
		int index1 = majors.indexOf(major1);
		int index2 = majors.indexOf(major2);
		int index3 = majors.indexOf(major3);
		int index4 = majors.indexOf(major4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		major1.delete();
		major2.delete();
		major3.delete();
		major4.delete();
	}
	
	/**
	 * Test that the major is persistence
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
		
		Major major = settingRepo.createMajor("major").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Major retrieved = settingRepo.findMajor(major.getId());
		
		assertEquals(major.getId(),retrieved.getId());
		assertEquals(major.getName(),retrieved.getName());
		
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
		settingRepo.createMajor("major").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createMajor("major").save().save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
