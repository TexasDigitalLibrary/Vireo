package org.tdl.vireo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class Workflow extends BaseEntity {
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String name;
	
	@ManyToMany
	private Set<WorkflowStep> workflowSteps;
	
	@Column(nullable = false)
	private Boolean isInheritable;
	
	public Workflow() {
		setWorkflowSteps(new HashSet<WorkflowStep>());
	}
	
	public Workflow(String name, Boolean isInheritable) {
		this();
		setName(name);
		setIsInheritable(isInheritable);
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
	 * 
	 * @param workflowStep
	 */
	public void addWorkflowStep(WorkflowStep workflowStep) {
		if(!getWorkflowSteps().contains(workflowStep)) {
			getWorkflowSteps().add(workflowStep);
		}
	}
	
	/**
	 * 
	 * @param workflowStep
	 */
	public void removeWorkflowStep(WorkflowStep workflowStep) {
		getWorkflowSteps().remove(workflowStep);
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
