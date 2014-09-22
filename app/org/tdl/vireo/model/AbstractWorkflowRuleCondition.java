package org.tdl.vireo.model;

public interface AbstractWorkflowRuleCondition extends AbstractOrderedModel {
	
	/**
	 * @return The id of this workflow rule condition
	 */
	public Long getConditionId();
	public String getConditionDisplayName();
	public void setConditionId(Long id);
	
	/**
	 * @return The name of the type of this workflow rule condition
	 */
	public ConditionType getConditionType();
	public void setConditionType(ConditionType conditionType);

	public enum ConditionType{Always, College, Department, Program};
}
