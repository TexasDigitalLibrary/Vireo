package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the graduation month interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaGraduationMonthImplTest extends UnitTest {
	
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
	 * Test creating an month
	 */
	@Test
	public void testCreate() {
		
		GraduationMonth month = settingRepo.createGraduationMonth(0);
		
		assertNotNull(month);
		assertEquals(0,month.getMonth());
		assertEquals("January", month.getMonthName());
		
		month.delete();
	}
	
	/**
	 * Test creating the grad month out of bounds.
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createGraduationMonth(-1);
			fail("Able to create a month out of bounds");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createGraduationMonth(12);
			fail("Able to create a month ouf of bounds");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate month
	 */
	@Test
	public void testCreateDuplicate() {
		
		GraduationMonth month = settingRepo.createGraduationMonth(0).save();
		
		try {
			settingRepo.createGraduationMonth(0).save();
			fail("Able to create duplicate grad month");
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
		
		GraduationMonth month = settingRepo.createGraduationMonth(0).save();

		assertNotNull(month.getId());
		
		month.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		GraduationMonth month = settingRepo.createGraduationMonth(0).save();

		
		GraduationMonth retrieved = settingRepo.findGraduationMonth(month.getId());
		
		assertEquals(month.getMonth(), retrieved.getMonth());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all months
	 */
	@Test
	public void testFindAllMonths() {

		int initialSize = settingRepo.findAllGraduationMonths().size();
		
		GraduationMonth month1 = settingRepo.createGraduationMonth(0).save();
		GraduationMonth month2 = settingRepo.createGraduationMonth(1).save();

		int postSize = settingRepo.findAllGraduationMonths().size();
		
		assertEquals(initialSize +2, postSize);
		
		month1.delete();
		month2.delete();
	}
	
	/**
	 * Test the validation 
	 */
	@Test 
	public void testValidation() {
		GraduationMonth january = settingRepo.createGraduationMonth(0).save();
		GraduationMonth test = settingRepo.createGraduationMonth(1).save();
		
		try {
			test.setMonth(-1);
			fail("Able to change month out of bounds");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setMonth(12);
			fail("Able to change month out of bounds");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setMonth(0);
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
		GraduationMonth month4 = settingRepo.createGraduationMonth(0).save();
		GraduationMonth month1 = settingRepo.createGraduationMonth(1).save();
		GraduationMonth month3 = settingRepo.createGraduationMonth(2).save();
		GraduationMonth month2 = settingRepo.createGraduationMonth(3).save();
		
		month1.setDisplayOrder(0);
		month2.setDisplayOrder(1);
		month3.setDisplayOrder(3);
		month4.setDisplayOrder(4);
		
		month1.save();
		month2.save();
		month3.save();
		month4.save();
		
		List<GraduationMonth> months = settingRepo.findAllGraduationMonths();
		
		int index1 = months.indexOf(month1);
		int index2 = months.indexOf(month2);
		int index3 = months.indexOf(month3);
		int index4 = months.indexOf(month4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		month1.delete();
		month2.delete();
		month3.delete();
		month4.delete();
	}
	
	/**
	 * Test that the grad month is persistence
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
		
		GraduationMonth month = settingRepo.createGraduationMonth(0).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		GraduationMonth retrieved = settingRepo.findGraduationMonth(month.getId());
		
		assertEquals(month.getId(),retrieved.getId());
		assertEquals(month.getMonth(),retrieved.getMonth());
		
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
		settingRepo.createGraduationMonth(0).save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createGraduationMonth(0).save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
