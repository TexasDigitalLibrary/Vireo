package org.tdl.vireo.state.impl;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;


/**
 * This is a simple state manager, which knows about all the possible states
 * available to vireo. And it also knows the state from which all submissions
 * start with.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class StateManagerImpl implements StateManager {

	/** Injected properties **/
	public List<State> allStates = new ArrayList<State>();
	public State initialState = null;

	@Override
	public State getState(String beanName) {
		for (State state : allStates)
			if (beanName.equals(state.getBeanName()))
				return state;
		return null;
	}

	@Override
	public State getInitialState() {
		return initialState;
	}

	@Override
	public List<State> getAllStates() {
		return new ArrayList<State>(allStates);
	}

	/**
	 * @param state
	 *            Set the initial state where all submissions start with.
	 */
	public void setInitialState(State state) {
		this.initialState = state;
	}

	/**
	 * @param allStates
	 *            Set the list of all possible states that submissions may be
	 *            within.
	 */
	public void setAllStates(List<State> allStates) {
		this.allStates = allStates;
	}
}
