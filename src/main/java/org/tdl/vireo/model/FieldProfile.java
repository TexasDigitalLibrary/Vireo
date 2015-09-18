package org.tdl.vireo.model;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

@Entity
public class FieldProfile extends BaseEntity {
	Set<WorkflowStep> workflowSteps;
	Map<String, FieldGloss> fieldGlosses;
	FieldPredicate predicate;
	Set<ControlledVocabulary> controlledVocab;
	Boolean repeatable, required;
	String type;

	/**
	 * @return the workflowSteps
	 */
	public Set<WorkflowStep> getWorkflowSteps() {
		return workflowSteps;
	}

	/**
	 * @param workflowSteps
	 *            the workflowSteps to set
	 */
	public void setWorkflowSteps(Set<WorkflowStep> workflowSteps) {
		this.workflowSteps = workflowSteps;
	}

	/**
	 * @return the fieldGlosses
	 */
	public Map<String, FieldGloss> getFieldGlosses() {
		return fieldGlosses;
	}

	/**
	 * @param fieldGlosses
	 *            the fieldGlosses to set
	 */
	public void setFieldGlosses(Map<String, FieldGloss> fieldGlosses) {
		this.fieldGlosses = fieldGlosses;
	}

	/**
	 * @return the predicate
	 */
	public FieldPredicate getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate
	 *            the predicate to set
	 */
	public void setPredicate(FieldPredicate predicate) {
		this.predicate = predicate;
	}

	/**
	 * @return the controlledVocab
	 */
	public Set<ControlledVocabulary> getControlledVocab() {
		return controlledVocab;
	}

	/**
	 * @param controlledVocab
	 *            the controlledVocab to set
	 */
	public void setControlledVocab(Set<ControlledVocabulary> controlledVocab) {
		this.controlledVocab = controlledVocab;
	}

	/**
	 * @return the repeatable
	 */
	public Boolean getRepeatable() {
		return repeatable;
	}

	/**
	 * @param repeatable
	 *            the repeatable to set
	 */
	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	/**
	 * @return the required
	 */
	public Boolean getRequired() {
		return required;
	}

	/**
	 * @param required
	 *            the required to set
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
