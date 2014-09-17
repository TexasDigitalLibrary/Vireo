package org.tdl.vireo.model;

import org.tdl.vireo.state.State;

/**
 * This abstract parent interface extends the base AbstractModel and serves
 * starting point for any future worklflow rules. Common elements to all workflow
 * rules will be condition, action and state.
 * 
 * @author Jeremy Huff, huff@library.tamu.edu 
 */
public abstract interface AbstractWorkflowRule extends AbstractModel {
	
	/**
	 * @return The status to which this rule has been attached  
	 */
	public State getAssociatedState();

	/**
	 * @param state
	 *            The status to which this rule will be attached  
	 */
	public void setAssociatedState(State state);
	
	/**
	 * @return The condition upon which this rule relies 
	 */
	public String getCondition();

	/**
	 * @param condition
	 *            The condition upon which the rule relies. This could be a college or
	 *            a department or a program, etc.
	 */
	public void setCondition(College condition);
	
	/**
	 * @param condition
	 *            The condition upon which the rule relies. This could be a college or
	 *            a department or a program, etc.
	 */
	public void setCondition(Department condition);
	
	/**
	 * @param condition
	 *            The condition upon which the rule relies. This could be a college or
	 *            a department or a program, etc.
	 */
	public void setCondition(Program condition);
	

}
