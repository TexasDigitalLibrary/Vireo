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
	public State cancelState = null;

	@Override
	public State getState(String beanName) {
		for (State state : allStates)
			if (beanName.equals(state.getBeanName()))
				return state;
		return null;
	}

	@Override
	public List<State> getAllStates() {
		return new ArrayList<State>(allStates);
	}
	
	@Override
	public State getInitialState() {
		return initialState;
	}

	/**
	 * @param state
	 *            Set the initial state where all submissions start with.
	 */
	public void setInitialState(State state) {
		this.initialState = state;
	}
	
	@Override
	public State getCancelState() {
		return cancelState;
	}
	
	/**
	 * @param state
	 *            Set the state where all canceled submission should be assigned too.
	 */
	public void setCancelState(State state) {
		this.cancelState = state;
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
