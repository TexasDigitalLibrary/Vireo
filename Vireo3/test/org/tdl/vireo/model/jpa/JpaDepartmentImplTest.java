package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the department interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaDepartmentImplTest extends UnitTest {
	
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
	 * Test creating an department
	 */
	@Test
	public void testCreate() {
		
		Department department = settingRepo.createDepartment("department");
		
		assertNotNull(department);
		assertEquals("department",department.getName());
		
		department.delete();
	}
	
	/**
	 * Test creating the department without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createDepartment(null);
			fail("Able to create null department");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createDepartment("");
			fail("Able to create blank department");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate department
	 */
	@Test
	public void testCreateDuplicate() {
		
		Department department = settingRepo.createDepartment("department").save();
		
		try {
			settingRepo.createDepartment("department").save();
			fail("Able to create duplicate department");
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
		
		Department department = settingRepo.createDepartment("department").save();

		assertNotNull(department.getId());
		
		department.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		Department department = settingRepo.createDepartment("department").save();

		
		Department retrieved = settingRepo.findDepartment(department.getId());
		
		assertEquals(department.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all departments
	 */
	@Test
	public void testFindAllDepartments() {

		int initialSize = settingRepo.findAllDepartments().size();
		
		Department department1 = settingRepo.createDepartment("department1").save();
		Department department2 = settingRepo.createDepartment("department2").save();

		int postSize = settingRepo.findAllDepartments().size();
		
		assertEquals(initialSize +2, postSize);
		
		department1.delete();
		department2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		Department department = settingRepo.createDepartment("department").save();
		Department test = settingRepo.createDepartment("test").save();
		
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
			test.setName("department");
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
		Department department4 = settingRepo.createDepartment("department4").save();
		Department department1 = settingRepo.createDepartment("department1").save();
		Department department3 = settingRepo.createDepartment("department3").save();
		Department department2 = settingRepo.createDepartment("department2").save();
		
		department1.setDisplayOrder(0);
		department2.setDisplayOrder(1);
		department3.setDisplayOrder(3);
		department4.setDisplayOrder(4);
		
		department1.save();
		department2.save();
		department3.save();
		department4.save();
		
		List<Department> departments = settingRepo.findAllDepartments();
		
		int index1 = departments.indexOf(department1);
		int index2 = departments.indexOf(department2);
		int index3 = departments.indexOf(department3);
		int index4 = departments.indexOf(department4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		department1.delete();
		department2.delete();
		department3.delete();
		department4.delete();
	}
	
	/**
	 * Test that the department is persistence
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
		
		Department department = settingRepo.createDepartment("department").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Department retrieved = settingRepo.findDepartment(department.getId());
		
		assertEquals(department.getId(),retrieved.getId());
		assertEquals(department.getName(),retrieved.getName());
		
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
		settingRepo.createDepartment("department").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createDepartment("department").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
