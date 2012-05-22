package org.tdl.vireo.model.jpa;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the Jpa specefic implementation of custom action values
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class JpaCustomActionValueImplTest extends UnitTest {

	// Persistence repositories
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	
	// Share the same person & submission
	public static Person person;
	public static Submission sub;
	public static CustomActionDefinition def;
	
	/**
	 * Create a new person & submission for each test.
	 */
	@Before
	public void setup() {
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person).save();
		def = settingRepo.createCustomActionDefinition("custom action").save();
	}
	
	/**
	 * Cleanup the person & submission after each test.
	 */
	@After
	public void cleanup() {
		if (sub != null)
			subRepo.findSubmission(sub.getId()).delete();
		
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		
		if (def != null)
			settingRepo.findCustomActionDefinition(def.getId()).delete();
	}
	
	/**
	 * Test creating a new action value.
	 */
	@Test
	public void testCreateCustomActionValue() {
		
		CustomActionValue value = sub.addCustomAction(def, true);
		
		assertEquals(def,value.getDefinition());
		assertTrue(value.getValue());
	}
	
	/**
	 * Test the negative case of creating an new value.
	 */
	@Test
	public void testBadCreateCustomActionValue() {
		
		try {
			sub.addCustomAction(null, true);
			fail("Able to create custom actiov with a null value");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Attempt to create a duplicate value.
	 */
	@Test
	public void testCreateDuplicate() {
		CustomActionValue value = sub.addCustomAction(def, true);
		value.save();
		
		try {
			sub.addCustomAction(def, false).save();
			fail("Able to create duplicate custom action value for a single submission");
		} catch (RuntimeException re) {
			/* yay */
		}
		
		JPA.em().clear();
		
	}
	
	/**
	 * Return the unique id of this value.
	 */
	@Test
	public void testId() {
		CustomActionValue value = sub.addCustomAction(def, true);
		value.save();
		
		assertNotNull(value.getId());
	}
	
	/**
	 * Test retrieving the value by id.
	 */
	@Test 
	public void findById() {
		CustomActionValue value = sub.addCustomAction(def, true);
		value.save();
		
		CustomActionValue retrieved = subRepo.findCustomActionValue(value.getId());
		assertNotNull(retrieved);
		assertEquals(value.getId(),retrieved.getId());
		
	}
	
	/**
	 * Test retrieving all custom actions.
	 */
	@Test
	public void findCustomActionSet() {
		CustomActionValue value = sub.addCustomAction(def, true);

		Set<CustomActionValue> actions = sub.getCustomActions();
		assertEquals(1,actions.size());
		assertEquals(value.getId(),actions.iterator().next().getId());
	}
	
	/**
	 * Test retrieving a specific custom action.
	 */
	@Test
	public void findSpecificCustomAction() {
		CustomActionValue value = sub.addCustomAction(def, true).save();

		assertEquals(value.getId(),sub.getCustomAction(def).getId());
	}
	
	/**
	 * Test modifying the value
	 */
	@Test
	public void testModifyValue() {
		CustomActionValue value = sub.addCustomAction(def, true);
		value.save();
		
		value.setValue(false);
		
		assertEquals(false,value.getValue());
	}
	
	/**
	 * Test the persistence of values.
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
		
		CustomActionValue value = sub.addCustomAction(def, true);
		value.save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		
		CustomActionValue retrieved = subRepo.findCustomActionValue(value.getId());
		
		assertEquals(value.getId(),retrieved.getId());
		assertEquals(value.getValue(),retrieved.getValue());
		assertEquals(value.getDefinition().getId(),retrieved.getDefinition().getId());
		
		
		subRepo.findSubmission(sub.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
		settingRepo.findCustomActionDefinition(def.getId()).delete();
		
		sub = null;
		person = null;
		def = null;

		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test deleting values.
	 */
	@Test
	public void testDelete() {
		
		CustomActionValue value = sub.addCustomAction(def, true);
		
		value.delete();
		
		assertEquals(0, sub.getCustomActions().size());
		
	}
	
}
