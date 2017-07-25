package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the college interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaCollegeImplTest extends UnitTest {
	
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
	 * Test creating an college
	 */
	@Test
	public void testCreate() {
		
		College college = settingRepo.createCollege("college");
		
		assertNotNull(college);
		assertEquals("college",college.getName());
		
		college.delete();
	}
	
	/**
	 * Test creating the college without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createCollege(null);
			fail("Able to create null college");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createCollege("");
			fail("Able to create blank college");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate college
	 */
	@Test
	public void testCreateDuplicate() {
		
		settingRepo.createCollege("college").save();
		
		try {
			settingRepo.createCollege("college").save();
			fail("Able to create duplicate college");
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
		
		College college = settingRepo.createCollege("college").save();

		assertNotNull(college.getId());
		
		college.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		College college = settingRepo.createCollege("college").save();

		
		College retrieved = settingRepo.findCollege(college.getId());
		
		assertEquals(college.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all colleges
	 */
	@Test
	public void testFindAllColleges() {

		int initialSize = settingRepo.findAllColleges().size();
		
		College college1 = settingRepo.createCollege("college1").save();
		College college2 = settingRepo.createCollege("college2").save();

		int postSize = settingRepo.findAllColleges().size();
		
		assertEquals(initialSize +2, postSize);
		
		college1.delete();
		college2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		College college = settingRepo.createCollege("college").save();
		College test = settingRepo.createCollege("test").save();
		
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
			test.setName("college");
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
		College college4 = settingRepo.createCollege("college4").save();
		College college1 = settingRepo.createCollege("college1").save();
		College college3 = settingRepo.createCollege("college3").save();
		College college2 = settingRepo.createCollege("college2").save();
		
		college1.setDisplayOrder(0);
		college2.setDisplayOrder(1);
		college3.setDisplayOrder(3);
		college4.setDisplayOrder(4);
		
		college1.save();
		college2.save();
		college3.save();
		college4.save();
		
		List<College> colleges = settingRepo.findAllColleges();
		
		int index1 = colleges.indexOf(college1);
		int index2 = colleges.indexOf(college2);
		int index3 = colleges.indexOf(college3);
		int index4 = colleges.indexOf(college4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		college1.delete();
		college2.delete();
		college3.delete();
		college4.delete();
	}
	
	/**
	 * Test that the college is persistence
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
		
		College college = settingRepo.createCollege("college").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		College retrieved = settingRepo.findCollege(college.getId());
		
		assertEquals(college.getId(),retrieved.getId());
		assertEquals(college.getName(),retrieved.getName());
		
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
		settingRepo.createCollege("college").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createCollege("college").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
