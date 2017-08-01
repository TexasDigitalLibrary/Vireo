package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the program interface
 * 
 * @author Micah Cooper
 */
public class JpaProgramImplTest extends UnitTest {
	
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
	 * Test creating an program
	 */
	@Test
	public void testCreate() {
		
		Program program = settingRepo.createProgram("program");
		
		assertNotNull(program);
		assertEquals("program",program.getName());
		
		program.delete();
	}
	
	/**
	 * Test creating the program without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createProgram(null);
			fail("Able to create null program");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createProgram("");
			fail("Able to create blank program");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate program
	 */
	@Test
	public void testCreateDuplicate() {
		
		settingRepo.createProgram("program").save();
		
		try {
			settingRepo.createProgram("program").save();
			fail("Able to create duplicate program");
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
		
		Program program = settingRepo.createProgram("program").save();

		assertNotNull(program.getId());
		
		program.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		Program program = settingRepo.createProgram("program").save();

		
		Program retrieved = settingRepo.findProgram(program.getId());
		
		assertEquals(program.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all programs
	 */
	@Test
	public void testFindAllprograms() {

		int initialSize = settingRepo.findAllPrograms().size();
		
		Program program1 = settingRepo.createProgram("program1").save();
		Program program2 = settingRepo.createProgram("program2").save();

		int postSize = settingRepo.findAllPrograms().size();
		
		assertEquals(initialSize +2, postSize);
		
		program1.delete();
		program2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		Program program = settingRepo.createProgram("program").save();
		Program test = settingRepo.createProgram("test").save();
		
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
			test.setName("program");
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
		Program program4 = settingRepo.createProgram("program4").save();
		Program program1 = settingRepo.createProgram("program1").save();
		Program program3 = settingRepo.createProgram("program3").save();
		Program program2 = settingRepo.createProgram("program2").save();
		
		program1.setDisplayOrder(0);
		program2.setDisplayOrder(1);
		program3.setDisplayOrder(3);
		program4.setDisplayOrder(4);
		
		program1.save();
		program2.save();
		program3.save();
		program4.save();
		
		List<Program> programs = settingRepo.findAllPrograms();
		
		int index1 = programs.indexOf(program1);
		int index2 = programs.indexOf(program2);
		int index3 = programs.indexOf(program3);
		int index4 = programs.indexOf(program4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		program1.delete();
		program2.delete();
		program3.delete();
		program4.delete();
	}
	
	/**
	 * Test that the program is persistence
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
		
		Program program = settingRepo.createProgram("program").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Program retrieved = settingRepo.findProgram(program.getId());
		
		assertEquals(program.getId(),retrieved.getId());
		assertEquals(program.getName(),retrieved.getName());
		
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
		settingRepo.createProgram("program").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createProgram("program").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
