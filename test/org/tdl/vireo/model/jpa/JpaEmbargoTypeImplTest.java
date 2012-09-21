package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the embargo type interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaEmbargoTypeImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	public static Indexer indexer = Spring.getBeanOfType(Indexer.class);
	
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
	 * Test creating an embargo
	 */
	@Test
	public void testCreate() {
		
		EmbargoType type = settingRepo.createEmbargoType("name", "description", 12, true).save();
		
		assertNotNull(type);
		assertTrue(type.isActive());
		assertEquals("description",type.getDescription());
		assertEquals(Integer.valueOf(12),type.getDuration());
		
		type.delete();
	}
	
	/**
	 * Test creating the embargo without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createEmbargoType(null, "description", 12, true);
			fail("Able to create null name");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmbargoType("", "description", 12, true);
			fail("Able to create blank name");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmbargoType("name", null, 12, true);
			fail("Able to create null description");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmbargoType("name", "", 12, true);
			fail("Able to create blank description");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmbargoType("name", "description", -1, true);
			fail("Able to create negative duration");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Create a duplicate embargo type by name
	 */
	@Test
	public void testCreateDuplicate() {
		EmbargoType type = settingRepo.createEmbargoType("name", "description", 12, true).save();

		try {
			settingRepo.createEmbargoType("name", "other description", 13, false).save();
			fail("able to create duplicate embargo type");
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
		
		EmbargoType type = settingRepo.createEmbargoType("name", "description", 12,true).save();

		assertNotNull(type.getId());
		
		type.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		EmbargoType type = settingRepo.createEmbargoType("name", "description", 12, true).save();

		
		EmbargoType retrieved = settingRepo.findEmbargoType(type.getId());
		
		assertEquals(type.getDescription(), retrieved.getDescription());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all embargos
	 */
	@Test
	public void testFindAllEmbargos() {

		int initialSize = settingRepo.findAllEmbargoTypes().size();
		
		EmbargoType type1 = settingRepo.createEmbargoType("name1", "description", 12, true).save();
		EmbargoType type2 = settingRepo.createEmbargoType("name2", "description", 12, false).save();

		int postSize = settingRepo.findAllEmbargoTypes().size();
		
		assertEquals(initialSize +2, postSize);
		
		type1.delete();
		type2.delete();
	}

	@Test
	public void testFindAllActiveEmbargos() {

		int initialSize = settingRepo.findAllActiveEmbargoTypes().size();
		
		EmbargoType type1 = settingRepo.createEmbargoType("name1", "description", 12, true).save();
		EmbargoType type2 = settingRepo.createEmbargoType("name2", "description", 12, false).save();

		int postSize = settingRepo.findAllActiveEmbargoTypes().size();
		
		assertEquals(initialSize + 1, postSize);
		
		for(EmbargoType e : settingRepo.findAllActiveEmbargoTypes()) {
			assertNotNull(e.isActive());
		}

		type1.delete();
		type2.delete();
	}
	
	/**
	 * Test the validation when modifying the name and description
	 */
	@Test 
	public void testValidation() {
		EmbargoType type = settingRepo.createEmbargoType("name", "description", 12, true).save();
		EmbargoType test = settingRepo.createEmbargoType("test", "description", 12, false).save();
		
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
			test.setDescription(null);
			fail("Able to change description to null");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setDescription("");
			fail("Able to change description to blank");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setDuration(-1);
			fail("Able to change duration to be negative");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setName("name");
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
		
		EmbargoType type4 = settingRepo.createEmbargoType("name4", "description", 12, true);
		EmbargoType type1 = settingRepo.createEmbargoType("name1", "description", 12, true);
		EmbargoType type3 = settingRepo.createEmbargoType("name3", "description", 12, true);
		EmbargoType type2 = settingRepo.createEmbargoType("name2", "description", 12, true);
		
		type1.setDisplayOrder(0);
		type2.setDisplayOrder(1);
		type3.setDisplayOrder(3);
		type4.setDisplayOrder(4);
		
		type1.save();
		type2.save();
		type3.save();
		type4.save();
		
		List<EmbargoType> types = settingRepo.findAllEmbargoTypes();
		
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
	 * Test that you can delete an embargo type and have submision associated with the type be cleared.
	 */
	@Test
	public void testDeletetion() {
		EmbargoType embargo = settingRepo.createEmbargoType("name", "description", 0, true).save();
		Person person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Submission sub = subRepo.createSubmission(person);
		sub.setEmbargoType(embargo);
		sub.save();
		
		// Clear out the indexer transaction.
		indexer.rollback();
		assertFalse(indexer.isUpdated(sub));

		embargo.delete();
		
		// Check that the submission was queued up in the indexer.
		assertTrue(indexer.isUpdated(sub));
		
		// check that the value associated with it was also deleted, once refreshed
		JPA.em().clear();
		sub = subRepo.findSubmission(sub.getId());
		
		assertNull(sub.getEmbargoType());
		
		subRepo.findSubmission(sub.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
	}
	
	/**
	 * Test that the embargo is persistence
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
		
		EmbargoType type = settingRepo.createEmbargoType("name", "description", 12, true).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		EmbargoType retrieved = settingRepo.findEmbargoType(type.getId());
		
		assertEquals(type.getId(),retrieved.getId());
		assertEquals(type.getName(),retrieved.getName());
		assertEquals(type.getDescription(),retrieved.getDescription());
		assertEquals(type.getDuration(),retrieved.getDuration());
		assertEquals(type.isActive(),retrieved.isActive());

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
		settingRepo.createEmbargoType("name", "description", 12, true).save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createEmbargoType("name", "description", 12, true).save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
