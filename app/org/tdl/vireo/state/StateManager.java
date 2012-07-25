package org.tdl.vireo.state;

import java.util.List;

/**
 * The state manager.
 * 
 * The purpose of this singleton class is to manage the various states that may
 * be available within vireo. This manager identifies the initial state for all
 * submissions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface StateManager {
	
	/**
	 * Return the full state object identified by the technical bean name.
	 * 
	 * @param beanName
	 *            The technical bean name of the state.
	 * @return The full state object.
	 */
	public State getState(String beanName);

	/**
	 * @return A list of all states.
	 */
	public List<State> getAllStates();

	/**
	 * @return The initial state that all submissions should be start with.
	 */
	public State getInitialState();
	
	/**
	 * @return The state where submissions should be assigned if they are
	 *         cancelled.
	 */
	public State getCancelState();
	

}
