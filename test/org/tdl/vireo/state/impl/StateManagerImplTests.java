package org.tdl.vireo.state.impl;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.state.State;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the simple state manager implementation. Intrinsically these tests are
 * tied to the spring configuration for states, however I will try my best to
 * make these tests independent of the configuration so that no matter how your
 * states are configured this test should still pass.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class StateManagerImplTests extends UnitTest {

	public static StateManagerImpl manager = Spring.getBeanOfType(StateManagerImpl.class);
	
	/**
	 * Test that all states defined in Spring's configuration can be resolved.
	 */
	@Test
	public void testGetState() {
		
		Map<String, State> beanMap = Spring.getBeansOfType(State.class);
		
		// This test is meaningless unless there are some states defined
		assertTrue(beanMap.size() > 0);
		
		for (String beanName: beanMap.keySet()) {
			
			State expected = beanMap.get(beanName);
			State retrieved = manager.getState(beanName);
			
			assertEquals(expected,retrieved);
		}
	}

	/**
	 * Test that there is an initial state definined.
	 */
	@Test
	public void testInitialState() {

		assertNotNull(manager.getInitialState());
	}
	
	/**
	 * Test that there is a state for canceled submissions.
	 */
	@Test
	public void testCancelState() {

		assertNotNull(manager.getCancelState());
	}

	/**
	 * Test that the manager knows about all states, and nothing more.
	 */
	@Test
	public void testAllStates() {
		
		Map<String, State> beanMap = Spring.getBeansOfType(State.class);
		
		// This test is meaningless unless there are some states defined
		assertTrue(beanMap.size() > 0);
		
		List<State> allStates = manager.getAllStates();
		
		for (State state : beanMap.values()) {
			assertTrue(allStates.contains(state));
			allStates.remove(state);
		}

		assertEquals(0,allStates.size());
	}
}
