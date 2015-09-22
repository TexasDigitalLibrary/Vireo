package org.tdl.vireo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.ALL;

@Entity
public class Submission extends BaseEntity {
	
	//TODO: add person entity
	
	@OneToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false, orphanRemoval = false)
	private SubmissionState state;
	
	@ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
	private Set<Organization> organizations;
	
	@OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
	private Set<FieldValue> fieldValues;
	
	public Submission() {
		setOrganizations(new HashSet<Organization>());
		setFieldValues(new HashSet<FieldValue>());
	}
	
	public Submission(SubmissionState state) {
		this();
		setState(state);
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
	 * 
	 * @param organization
	 */
	public void addOrganization(Organization organization) {
		getOrganizations().add(organization);
	}
	
	/**
	 * 
	 * @param organization
	 */
	public void removeOrganization(Organization organization) {
		getOrganizations().remove(organization);
	}

	/**
	 * @return the fieldvalues
	 */
	public Set<FieldValue> getFieldValues() {
		return fieldValues;
	}

	/**
	 * @param fieldvalues
	 *            the fieldvalues to set
	 */
	public void setFieldValues(Set<FieldValue> fieldvalues) {
		this.fieldValues = fieldvalues;
	}
	
	/**
	 * 
	 * @param fieldValue
	 */
	public void addFieldValue(FieldValue fieldValue) {
		getFieldValues().add(fieldValue);
	}
	
	/**
	 * 
	 * @param fieldValue
	 */
	public void removeFieldValue(FieldValue fieldValue) {
		getFieldValues().remove(fieldValue);
	}
	
}
