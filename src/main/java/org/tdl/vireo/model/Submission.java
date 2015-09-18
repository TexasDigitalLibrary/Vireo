package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;

@Entity
public class Submission extends BaseEntity {
	@ManyToMany
	Set<Organization> organizations;
	Set<FieldValue> fieldvalues;
	@OneToOne
	SubmissionState state;

	/**
	 * @return the organizations
	 */
	public Set<Organization> getOrganizations() {
		return organizations;
	}

	/**
	 * @param organizations
	 *            the organizations to set
	 */
	public void setOrganizations(Set<Organization> organizations) {
		this.organizations = organizations;
	}

	/**
	 * @return the fieldvalues
	 */
	public Set<FieldValue> getFieldvalues() {
		return fieldvalues;
	}

	/**
	 * @param fieldvalues
	 *            the fieldvalues to set
	 */
	public void setFieldvalues(Set<FieldValue> fieldvalues) {
		this.fieldvalues = fieldvalues;
	}

	/**
	 * @return the state
	 */
	public SubmissionState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(SubmissionState state) {
		this.state = state;
	}
}
