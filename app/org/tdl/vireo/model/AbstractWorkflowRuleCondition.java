package org.tdl.vireo.model;

public interface AbstractWorkflowRuleCondition extends AbstractOrderedModel {
	
	/**
	 * @return The name of this workflow rule condition
	 */
	public String getConditionName();
	
	/**
	 * @return The name of the type of this workflow rule condition
	 */
	public ConditionType getConditionType();

	public enum ConditionType{Always, College, Department, Program};
}
