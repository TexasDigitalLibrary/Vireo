package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Workflow extends BaseEntity {
	Set<WorkflowStep> workflowSteps;
	String name;
	Boolean isInheritable;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isInheritable
	 */
	public Boolean getIsInheritable() {
		return isInheritable;
	}

	/**
	 * @param isInheritable
	 *            the isInheritable to set
	 */
	public void setIsInheritable(Boolean isInheritable) {
		this.isInheritable = isInheritable;
	}
}
