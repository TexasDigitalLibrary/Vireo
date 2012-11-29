package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the committee member role type interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaCommitteeMemberRoleTypeTest extends UnitTest {
	
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
	 * Test creating an role type
	 */
	@Test
	public void testCreate() {
		
		CommitteeMemberRoleType type = settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.DOCTORAL);
		
		assertNotNull(type);
		assertEquals("type",type.getName());
		assertEquals(DegreeLevel.DOCTORAL,type.getLevel());
		
		type.delete();
	}
	
	/**
	 * Test creating the role type without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createCommitteeMemberRoleType(null,DegreeLevel.MASTERS);
			fail("Able to create null role type");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createCommitteeMemberRoleType("", DegreeLevel.UNDERGRADUATE);
			fail("Able to create blank role type");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createCommitteeMemberRoleType("type", null);
			fail("Able to create role type with a null level");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate role type
	 */
	@Test
	public void testCreateDuplicate() {
		
		CommitteeMemberRoleType type = settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.UNDERGRADUATE).save();
		
		try {
			settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.UNDERGRADUATE).save();
			fail("Able to create duplicate type");
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
		
		CommitteeMemberRoleType type = settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.MASTERS).save();

		assertNotNull(type.getId());
		
		type.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		CommitteeMemberRoleType type = settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.DOCTORAL).save();

		
		CommitteeMemberRoleType retrieved = settingRepo.findCommitteeMemberRoleType(type.getId());
		
		assertEquals(type.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all role types
	 */
	@Test
	public void testFindAllCommitteeMemberRoleTypes() {

		int initialSize = settingRepo.findAllCommitteeMemberRoleTypes().size();
		
		CommitteeMemberRoleType type1 = settingRepo.createCommitteeMemberRoleType("type1",DegreeLevel.MASTERS).save();
		CommitteeMemberRoleType type2 = settingRepo.createCommitteeMemberRoleType("type2",DegreeLevel.UNDERGRADUATE).save();

		int postSize = settingRepo.findAllCommitteeMemberRoleTypes().size();
		
		assertEquals(initialSize +2, postSize);
		
		type1.delete();
		type2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		CommitteeMemberRoleType type = settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.MASTERS).save();
		CommitteeMemberRoleType test = settingRepo.createCommitteeMemberRoleType("test",DegreeLevel.UNDERGRADUATE).save();
		
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
			test.setName("type");
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
		CommitteeMemberRoleType type4 = settingRepo.createCommitteeMemberRoleType("type4",DegreeLevel.MASTERS).save();
		CommitteeMemberRoleType type1 = settingRepo.createCommitteeMemberRoleType("type1",DegreeLevel.MASTERS).save();
		CommitteeMemberRoleType type3 = settingRepo.createCommitteeMemberRoleType("type3",DegreeLevel.DOCTORAL).save();
		CommitteeMemberRoleType type2 = settingRepo.createCommitteeMemberRoleType("type2",DegreeLevel.MASTERS).save();
		
		type1.setDisplayOrder(0);
		type2.setDisplayOrder(1);
		type3.setDisplayOrder(3);
		type4.setDisplayOrder(4);
		
		type1.save();
		type2.save();
		type3.save();
		type4.save();
		
		List<CommitteeMemberRoleType> types = settingRepo.findAllCommitteeMemberRoleTypes();
		
		int index1 = types.indexOf(type1);
		int index2 = types.indexOf(type2);
		int index3 = types.indexOf(type3);
		int index4 = types.indexOf(type4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		type1.delete();
		type2.delete();
		type3.delete();
		type4.delete();
	}
	
	/**
	 * Test that the role type is persistence
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
		
		CommitteeMemberRoleType type = settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.MASTERS).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		CommitteeMemberRoleType retrieved = settingRepo.findCommitteeMemberRoleType(type.getId());
		
		assertEquals(type.getId(),retrieved.getId());
		assertEquals(type.getName(),retrieved.getName());
		assertEquals(type.getLevel(),retrieved.getLevel());
		
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
		settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.MASTERS).save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createCommitteeMemberRoleType("type",DegreeLevel.MASTERS).save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
