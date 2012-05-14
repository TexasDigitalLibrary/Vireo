package org.tdl.vireo.state;

import java.util.List;

public interface StateManager {
	
	public State getState(String beanName);
	
	public State getInitialState();
	
	public List<State> getAllStates();
}
