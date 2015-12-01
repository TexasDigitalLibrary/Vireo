package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Workflow extends BaseEntity {
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Boolean inheritable;
	
	@OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
	private List<WorkflowStep> workflowSteps;
	
	public Workflow() {
		setWorkflowSteps(new ArrayList<WorkflowStep>());
	}
	
	/**
	 * 
	 * @param name
	 * @param inheritable
	 */
	public Workflow(String name, Boolean inheritable) {
		this();
		setName(name);
		setInheritability(inheritable);
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
	 * @return the inheritable
	 */
	public Boolean isInheritable() {
		return inheritable;
	}

	/**
	 * @param inheritable
	 *            the inheritable to set
	 */
	public void setInheritability(Boolean inheritable) {
		this.inheritable = inheritable;
	}
	
	/**
	 * @return the workflowSteps
	 */
	public List<WorkflowStep> getWorkflowSteps() {
		return workflowSteps;
	}

	/**
	 * @param workflowSteps
	 *            the workflowSteps to set
	 */
	public void setWorkflowSteps(List<WorkflowStep> workflowSteps) {
		this.workflowSteps = workflowSteps;
	}
	
	/**
	 * 
	 * @param workflowStep
	 */
	public void addWorkflowStep(WorkflowStep workflowStep) {
		getWorkflowSteps().add(workflowStep);
	}
	
	/**
	 * 
	 * @param workflowStep
	 */
	public void removeWorkflowStep(WorkflowStep workflowStep) {
		getWorkflowSteps().remove(workflowStep);
	}
}
