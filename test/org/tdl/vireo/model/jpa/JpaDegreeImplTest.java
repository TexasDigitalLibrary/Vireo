package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the degree interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaDegreeImplTest extends UnitTest {
	
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
	 * Test creating an degree
	 */
	@Test
	public void testCreate() {
		
		Degree degree = settingRepo.createDegree("degree",DegreeLevel.DOCTORAL);
		
		assertNotNull(degree);
		assertEquals("degree",degree.getName());
		assertEquals(DegreeLevel.DOCTORAL,degree.getLevel());
		
		degree.delete();
	}
	
	/**
	 * Test creating the degree without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createDegree(null,DegreeLevel.MASTERS);
			fail("Able to create null degree");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createDegree("", DegreeLevel.UNDERGRADUATE);
			fail("Able to create blank degree");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createDegree("degree", null);
			fail("Able to create degree with a null level");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate degree
	 */
	@Test
	public void testCreateDuplicate() {
		
		Degree degree = settingRepo.createDegree("degree",DegreeLevel.UNDERGRADUATE).save();
		
		try {
			settingRepo.createDegree("degree",DegreeLevel.UNDERGRADUATE).save();
			fail("Able to create duplicate degree");
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
		
		Degree degree = settingRepo.createDegree("degree",DegreeLevel.MASTERS).save();

		assertNotNull(degree.getId());
		
		degree.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		Degree degree = settingRepo.createDegree("degree",DegreeLevel.DOCTORAL).save();

		
		Degree retrieved = settingRepo.findDegree(degree.getId());
		
		assertEquals(degree.getName(), retrieved.getName());
		
		retrieved.delete();
	}

	/**
	 * Test retrieval by name.
	 *
	 */
	@Test
	public void testFindByName() {
		Degree degree = settingRepo.createDegree("named degree",DegreeLevel.DOCTORAL).save();

		Degree retrieved = settingRepo.findDegreeByName("named degree");

		assertNotNull(retrieved);
		assertEquals(degree.getId(), retrieved.getId());

		retrieved.delete();
	}
	
	/**
	 * Test retrieving all degrees
	 */
	@Test
	public void testFindAllDegrees() {

		int initialSize = settingRepo.findAllDegrees().size();
		
		Degree degree1 = settingRepo.createDegree("degree1",DegreeLevel.MASTERS).save();
		Degree degree2 = settingRepo.createDegree("degree2",DegreeLevel.UNDERGRADUATE).save();

		int postSize = settingRepo.findAllDegrees().size();
		
		assertEquals(initialSize +2, postSize);
		
		degree1.delete();
		degree2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		Degree degree = settingRepo.createDegree("degree",DegreeLevel.MASTERS).save();
		Degree test = settingRepo.createDegree("test",DegreeLevel.UNDERGRADUATE).save();
		
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
			test.setName("degree");
			test.setLevel(DegreeLevel.MASTERS);
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
		Degree degree4 = settingRepo.createDegree("degree4",DegreeLevel.MASTERS).save();
		Degree degree1 = settingRepo.createDegree("degree1",DegreeLevel.MASTERS).save();
		Degree degree3 = settingRepo.createDegree("degree3",DegreeLevel.DOCTORAL).save();
		Degree degree2 = settingRepo.createDegree("degree2",DegreeLevel.MASTERS).save();
		
		degree1.setDisplayOrder(0);
		degree2.setDisplayOrder(1);
		degree3.setDisplayOrder(3);
		degree4.setDisplayOrder(4);
		
		degree1.save();
		degree2.save();
		degree3.save();
		degree4.save();
		
		List<Degree> degrees = settingRepo.findAllDegrees();
		
		int index1 = degrees.indexOf(degree1);
		int index2 = degrees.indexOf(degree2);
		int index3 = degrees.indexOf(degree3);
		int index4 = degrees.indexOf(degree4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		degree1.delete();
		degree2.delete();
		degree3.delete();
		degree4.delete();
	}
	
	/**
	 * Test that the degree is persistence
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
		
		Degree degree = settingRepo.createDegree("degree",DegreeLevel.MASTERS).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Degree retrieved = settingRepo.findDegree(degree.getId());
		
		assertEquals(degree.getId(),retrieved.getId());
		assertEquals(degree.getName(),retrieved.getName());
		assertEquals(degree.getLevel(),retrieved.getLevel());
		
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
		settingRepo.createDegree("degree",DegreeLevel.MASTERS).save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createDegree("degree",DegreeLevel.MASTERS).save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
