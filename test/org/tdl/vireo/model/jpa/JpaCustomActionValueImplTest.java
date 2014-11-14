package org.tdl.vireo.model.jpa;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

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
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
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
		context.login(MockPerson.getAdministrator());
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person).save();
		def = settingRepo.createCustomActionDefinition("custom action", false).save();
	}
	
	/**
	 * Cleanup the person & submission after each test.
	 */
	@After
	public void cleanup() {
		JPA.em().clear();
		if (sub != null)
			subRepo.findSubmission(sub.getId()).delete();
		
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		
		if (def != null)
			settingRepo.findCustomActionDefinition(def.getId()).delete();
		
		context.logout();
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
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
		
		sub = null;
		person = null;
		def = null;
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
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

		List<CustomActionValue> actions = sub.getCustomActions();
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
	 * Test that action logs are generated appropriately.
	 */
	@Test
	public void testActionLogGeneration() {

		State initialState = stateManager.getInitialState();
		State nextState = initialState.getTransitions(sub).get(0);
		sub.setState(nextState);
		sub.save();
		
		CustomActionValue value = sub.addCustomAction(def, true).save();
		value.setValue(false);
		value.save();
		value.delete();
				
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Iterator<ActionLog> logItr = logs.iterator();
		
		sub.delete();
		sub = null;	
		
		assertEquals("Custom action "+def.getLabel()+" unset", logItr.next().getEntry());
		assertEquals("Custom action "+def.getLabel()+" unset", logItr.next().getEntry());
		assertEquals("Custom action "+def.getLabel()+" set", logItr.next().getEntry());
		assertEquals("Custom action custom action set",logItr.next().getEntry());
		assertEquals("Submission status changed to 'Submitted'",logItr.next().getEntry());
		assertEquals("Submission created",logItr.next().getEntry());
		assertFalse(logItr.hasNext());
	}
	
	
	/**
	 * Test the persistence of values.
	 */
	@Test
	public void testPersistance() {
		
		if (sub.getId() != null)
			sub.delete();
		
		if (person.getId() != null)
			person.delete();
		
		if (def.getId() != null)
			def.delete();
		
		// Commit and reopen a new transaction because some of the other tests
		// may have caused exceptions which set the transaction to be rolled
		// back.
		if (JPA.em().getTransaction().getRollbackOnly())
			JPA.em().getTransaction().rollback();
		else
			JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person).save();
		def = settingRepo.createCustomActionDefinition("custom action", false).save();

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
	
	/**
	 * Test who has access to add/modify/delete action values.
	 */
	@Test
	public void testAccess() {		
		try {
		// Test that the owner can add an action
		context.login(person);
		CustomActionValue value1 = sub.addCustomAction(def, true).save();
		value1.setValue(false);
		value1.save();
		
		// Test that a reviewer can add an action
		context.login(MockPerson.getReviewer());
		value1.setValue(false);
		value1.save();

		// Test that a someone else can not add an action.
		context.login(MockPerson.getStudent());
		try {
			sub.addCustomAction(def, true).save();
			fail("Someone else was able to add an action value to a submission.");
		} catch (SecurityException se) {
			/* yay */
		}	
		} finally {
		context.login(MockPerson.getAdministrator());
		}
	}
	
}
