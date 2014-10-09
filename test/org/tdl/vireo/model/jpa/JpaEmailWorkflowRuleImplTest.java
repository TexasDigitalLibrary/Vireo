/**
 * 
 */
package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 *
 */
public class JpaEmailWorkflowRuleImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	public static State state = stateManager.getState("Submitted");
	public static State state1 = stateManager.getState("CorrectionsReceived");
	public static State state2 = stateManager.getState("Approved");
	public static State state3 = stateManager.getState("PendingPublication");
	public static State state4 = stateManager.getState("Published");
	public static State testState = stateManager.getState("NeedsCorrection");
	
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
	 * Test creating an email workflow rule
	 */
	@Test
	public void testCreate() {
		
		EmailWorkflowRule wflRule = settingRepo.createEmailWorkflowRule(state);
		
		assertNotNull(wflRule);
		assertEquals(state,wflRule.getAssociatedState());
		
		wflRule.delete();
	}
	
	/**
	 * Test creating the email workflow rule without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createEmailWorkflowRule(null);
			fail("Able to create null email workflow rule");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmailWorkflowRule(stateManager.getState(""));
			fail("Able to create blank email workflow rule");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test the id.
	 */
	@Test
	public void testId() {
		
		EmailWorkflowRule wflRule = settingRepo.createEmailWorkflowRule(state).save();

		assertNotNull(wflRule.getId());
		
		wflRule.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		EmailWorkflowRule wflRule = settingRepo.createEmailWorkflowRule(state).save();

		
		EmailWorkflowRule retrieved = settingRepo.findEmailWorkflowRule(wflRule.getId());
		
		assertEquals(wflRule.getAssociatedState(), retrieved.getAssociatedState());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all email workflow rule
	 */
	@Test
	public void testfindAllEmailWorkflowRules() {

		int initialSize = settingRepo.findAllEmailWorkflowRules().size();
		
		EmailWorkflowRule wflRule1 = settingRepo.createEmailWorkflowRule(state1).save();
		EmailWorkflowRule wflRule2 = settingRepo.createEmailWorkflowRule(state2).save();

		int postSize = settingRepo.findAllEmailWorkflowRules().size();
		
		assertEquals(initialSize +2, postSize);
		
		wflRule1.delete();
		wflRule2.delete();
	}
	
	/**
	 * Test the validation when modifying the state
	 */
	@Test 
	public void testStateValidation() {
		EmailWorkflowRule wflRule = settingRepo.createEmailWorkflowRule(state).save();
		EmailWorkflowRule test = settingRepo.createEmailWorkflowRule(testState).save();
		
		try {
			test.setAssociatedState(null);
			fail("Able to change state to null");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setAssociatedState(stateManager.getState(""));
			fail("Able to change state to blank");
		} catch (IllegalArgumentException iae) {
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
		EmailWorkflowRule wflRule4 = settingRepo.createEmailWorkflowRule(state4).save();
		EmailWorkflowRule wflRule1 = settingRepo.createEmailWorkflowRule(state1).save();
		EmailWorkflowRule wflRule3 = settingRepo.createEmailWorkflowRule(state3).save();
		EmailWorkflowRule wflRule2 = settingRepo.createEmailWorkflowRule(state2).save();
		
		wflRule1.setDisplayOrder(0);
		wflRule2.setDisplayOrder(1);
		wflRule3.setDisplayOrder(3);
		wflRule4.setDisplayOrder(4);
		
		wflRule1.save();
		wflRule2.save();
		wflRule3.save();
		wflRule4.save();
		
		List<EmailWorkflowRule> wflRules = settingRepo.findAllEmailWorkflowRules();
		
		int index1 = wflRules.indexOf(wflRule1);
		int index2 = wflRules.indexOf(wflRule2);
		int index3 = wflRules.indexOf(wflRule3);
		int index4 = wflRules.indexOf(wflRule4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		wflRule1.delete();
		wflRule2.delete();
		wflRule3.delete();
		wflRule4.delete();
	}
	
	/**
	 * Test that the email workflow rule is persistence
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
		
		EmailWorkflowRule wflRule = settingRepo.createEmailWorkflowRule(state).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		EmailWorkflowRule retrieved = settingRepo.findEmailWorkflowRule(wflRule.getId());
		
		assertEquals(wflRule.getId(),retrieved.getId());
		assertEquals(wflRule.getAssociatedState(),retrieved.getAssociatedState());
		
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
		settingRepo.createEmailWorkflowRule(state).save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createEmailWorkflowRule(state).save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}