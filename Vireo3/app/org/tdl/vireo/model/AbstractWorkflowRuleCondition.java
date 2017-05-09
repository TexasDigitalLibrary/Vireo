package org.tdl.vireo.model;

public interface AbstractWorkflowRuleCondition extends AbstractOrderedModel {
	
	/**
	 * @return The id of this workflow rule condition
	 */
	public Long getConditionId();
	
	/**
	 * 
	 * @return
	 */
	public String getConditionIdDisplayName();
	
	/**
	 * 
	 * @param id
	 */
	public void setConditionId(Long id);
	
	/**
	 * @return The name of the type of this workflow rule condition
	 */
	public ConditionType getConditionType();
	
	/**
	 * 
	 * @param conditionType
	 */
	public void setConditionType(ConditionType conditionType);
}
