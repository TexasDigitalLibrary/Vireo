package org.tdl.vireo.state.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.model.MockEmbargoType;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.state.State;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the very simple state implementation. Basically, does it hold the values
 * injected by Spring? To test the transition mechanism we've created some
 * dynamic proxy objects.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class StateImplTest extends UnitTest {

	/**
	 * Test all the spring defined states
	 */
	@Test
	public void testSpringDefinedStates() {
		
		Map<String, State> beanMap = Spring.getBeansOfType(State.class);

		for (State state : beanMap.values()) {
			assertNotNull(state.getBeanName());
			assertNotNull(state.getDisplayName());	
		}
		
		
	}
	
	/**
	 * Test all the properties of a new state.
	 */
	@Test
	public void testCustomDefinedStates() {
		
		StateImpl state = new StateImpl();
		state.setBeanName("state");
		state.setDisplayName("display");
		state.setInProgress(true);
		state.setActive(true);
		state.setArchived(true);
		state.setEditableByStudent(true);
		state.setEditableByReviewer(true);
		state.setDeletable(true);
		state.setDepositable(true);
		state.setApproved(true);
		
		assertEquals("state",state.getBeanName());
		assertEquals("display",state.getDisplayName());
		assertTrue(state.isInProgress());
		assertTrue(state.isActive());
		assertTrue(state.isArchived());
		assertTrue(state.isEditableByStudent());
		assertTrue(state.isEditableByReviewer());
		assertTrue(state.isDeletable());
		assertTrue(state.isDepositable());
		assertTrue(state.isApproved());

	}
	
	/**
	 * Test the transitions from the state for the various conditions such as
	 * weather the submission is embargoed and what transitions have been
	 * defined.
	 */
	@Test
	public void testTransitions() {
		
		/// Setup a state
		StateImpl one = new StateImpl();
		StateImpl two = new StateImpl();
		StateImpl three = new StateImpl();

		one.setBeanName("one");
		two.setBeanName("two");
		three.setBeanName("three");

		List<State> transitions = new ArrayList<State>();
		transitions.add(two);
		
		List<State> embargoTransitions = new ArrayList<State>();
		embargoTransitions.add(three);

		one.setTransitions(transitions);
		one.setEmbargoTransitions(embargoTransitions);
		
		
		// No embargo set at all, it's null.
		MockSubmission nullEmbargoedSub = new MockSubmission();
		
		// Normaly embargoed submission (12 months)
		MockSubmission normalEmbargoedSub = new MockSubmission();
		normalEmbargoedSub.embargoType = new MockEmbargoType();
		normalEmbargoedSub.embargoType.duration = 12;
		
		// Embargoe type set, but is duration zero. 
		MockSubmission zeroEmbargoedSub = new MockSubmission();
		zeroEmbargoedSub.embargoType = new MockEmbargoType();
		zeroEmbargoedSub.embargoType.duration = 0;
		
		// Test an embargoed submission with two sets of transitions defined.
		List<State> resultWhenEmbargoed = one.getTransitions(normalEmbargoedSub);
		assertEquals(1,resultWhenEmbargoed.size());
		assertFalse(resultWhenEmbargoed.contains(two));
		assertTrue(resultWhenEmbargoed.contains(three));
		
		// Test an un-embargoed submission with two sets of transitions defined.
		List<State> resultWhenNotEmbargoed = one.getTransitions(nullEmbargoedSub);
		assertEquals(1,resultWhenNotEmbargoed.size());
		assertTrue(resultWhenNotEmbargoed.contains(two));
		assertFalse(resultWhenNotEmbargoed.contains(three));
		
		// Test an zero embargoed submission with two sets of transitions defined.
		List<State> resultWhenZeroEmbargoed = one.getTransitions(zeroEmbargoedSub);
		assertEquals(1,resultWhenZeroEmbargoed.size());
		assertTrue(resultWhenZeroEmbargoed.contains(two));
		assertFalse(resultWhenZeroEmbargoed.contains(three));
		
		// Test an embargoed submission with no embargo transitions defined.
		one.setEmbargoTransitions(new ArrayList<State>());
		List<State> resultWhenEmbargoedWithNoTransitions = one.getTransitions(normalEmbargoedSub);
		assertEquals(1,resultWhenEmbargoedWithNoTransitions.size());
		assertTrue(resultWhenEmbargoedWithNoTransitions.contains(two));
		assertFalse(resultWhenEmbargoedWithNoTransitions.contains(three));
		
	}

}
