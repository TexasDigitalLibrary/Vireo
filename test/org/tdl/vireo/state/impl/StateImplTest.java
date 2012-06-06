package org.tdl.vireo.state.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import org.junit.Test;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.impl.StateImpl;

import play.Play;
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
		
		assertEquals("state",state.getBeanName());
		assertEquals("display",state.getDisplayName());
		assertTrue(state.isInProgress());
		assertTrue(state.isActive());
		assertTrue(state.isArchived());
		assertTrue(state.isEditableByStudent());
		assertTrue(state.isEditableByReviewer());
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
		
		
		// Test an embargoed submission with two sets of transitions defined.
		List<State> resultWhenEmbargoed = one.getTransitions(
				(Submission) MockEmbargoedSubmission.newInstance(true)
			);
		assertEquals(1,resultWhenEmbargoed.size());
		assertFalse(resultWhenEmbargoed.contains(two));
		assertTrue(resultWhenEmbargoed.contains(three));
		
		// Test an un-embargoed submission with two sets of transitions defined.
		List<State> resultWhenNotEmbargoed = one.getTransitions(
				(Submission) MockEmbargoedSubmission.newInstance(false)
			);
		assertEquals(1,resultWhenNotEmbargoed.size());
		assertTrue(resultWhenNotEmbargoed.contains(two));
		assertFalse(resultWhenNotEmbargoed.contains(three));
		
		// Test an embargoed submission with no embargo transitions defined.
		one.setEmbargoTransitions(new ArrayList<State>());
		List<State> resultWhenEmbargoedWithNoTransitions = one.getTransitions(
				(Submission) MockEmbargoedSubmission.newInstance(true)
			);
		assertEquals(1,resultWhenEmbargoedWithNoTransitions.size());
		assertTrue(resultWhenEmbargoedWithNoTransitions.contains(two));
		assertFalse(resultWhenEmbargoedWithNoTransitions.contains(three));
		
	}
	
	
	
	/**
	 * This is a dynamic mock submission object, true I could have created a
	 * regular old mock class but I (Scott) wanted to play around with java's
	 * reflex proxy classes so I chose to do it this way.
	 * 
	 * The dynamic submission object returned will only work for one method, the
	 * "getEmbargoType" and it will either return null or an embargoType (also
	 * another dynamic object, see below) object depending on the boolean in the
	 * constructor.
	 * 
	 * 
	 */
	public static class MockEmbargoedSubmission implements
			InvocationHandler {

		// Weather this dynamic mock object should have an embargo type associated with it.
		private boolean embargoed = false;
		
		/**
		 * Construct a new instance of the dynamic proxy submission object.
		 * @param embargo Weather the submission object should have an embargo type defined.
		 * @return The new dynamic submission object.
		 */
		public static Object newInstance(Boolean embargo) {
			
			Class[] iface = {Submission.class};
			return Proxy.newProxyInstance(
					Play.classloader, 
					iface,
					new MockEmbargoedSubmission(embargo)
				);
		}

		/**
		 * Private constructor.
		 * @param embargo Weather the submission object is embargoed
		 */
		private MockEmbargoedSubmission(boolean embargo) {
			this.embargoed = embargo;
		}

		/**
		 * When ever any method is invoked on the dynamic submission object this
		 * method is called. We only check for the one particular method we are
		 * looking for everything else throws an OperationNotSupported
		 * expection.
		 */
		public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
			
			if ("getEmbargoType".equals(m.getName())) {
			
				if (embargoed) {
					// The stateImpl only checks for a null value so as long as
					// we return a non null value we should be fine.
					return MockEmbargoType.newInstance();
				} else {
					// We're not embargoed so just return null.
					return null;
				}
				
			} else {
				throw new OperationNotSupportedException("This mock submission class only supports the getTransition method.");
			}
		}
	}
	
	/**
	 * 
	 * Another dynamic object like above, but this time for the EmbargoType
	 * interface. This dynamic object does not implement anything on the
	 * interface, it's just a placeholder because the StateImpl only checks for
	 * null or not null. It dosen't actually care what the embargo type is.
	 */
	public static class MockEmbargoType implements InvocationHandler {
		/**
		 * @return A new non-functioning dynamic EmbargoType
		 */
		public static Object newInstance() {

			Class[] iface = { EmbargoType.class };

			return Proxy.newProxyInstance(
					Play.classloader,
					iface, 
					new MockEmbargoType()
				);
		}

		/**
		 * Private constructor
		 */
		private MockEmbargoType() {}

		/**
		 * We don't implement anything, so no matter what method is call return null.
		 */
		public Object invoke(Object proxy, Method m, Object[] args)
				throws Throwable {

			// We don't do anything
			return null;
		}
}

}
