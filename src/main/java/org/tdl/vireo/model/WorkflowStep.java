package org.tdl.vireo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class WorkflowStep extends BaseEntity {
	@ManyToMany
	Set<Workflow> workflows = new HashSet<Workflow>();
	String name;

	/**
	 * @return the workflows
	 */
	public Set<Workflow> getWorkflows() {
		return workflows;
	}

	/**
	 * @param workflows
	 *            the workflows to set
	 */
	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows = workflows;
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
}
