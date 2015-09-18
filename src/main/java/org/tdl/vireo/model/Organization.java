package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Organization extends BaseEntity {

	@Column(columnDefinition = "TEXT", nullable = false)
	private String name;

	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = LAZY, optional = false)
	private Workflow workflow;

	@ManyToMany(cascade = { DETACH, REFRESH })
	private Set<Organization> parentOrganizations;

	@ManyToMany(cascade = { DETACH, REFRESH, PERSIST })
	private Set<Organization> childrenOrganizations;

	@ElementCollection
	private Set<String> emails;

	public Organization() {
		setParentOrganizations(new HashSet<Organization>());
		setChildrenOrganizations(new HashSet<Organization>());
		setEmails(new HashSet<String>());
	}

	public Organization(String name, Workflow workflow) {
		this();
		setName(name);
		setWorkflow(workflow);
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
	 * @return the workflow
	 */
	public Workflow getWorkflow() {
		return workflow;
	}

	/**
	 * @param workflow
	 *            the workflow to set
	 */
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	/**
	 * @return the parentOrganizations
	 */
	public Set<Organization> getParentOrganizations() {
		return parentOrganizations;
	}

	/**
	 * @param parentOrganizations
	 *            the parentOrganizations to set
	 */
	public void setParentOrganizations(Set<Organization> parentOrganizations) {
		this.parentOrganizations = parentOrganizations;
	}
	
	/**
	 * 
	 * @param parentOrganization
	 */
	public void addParentOrganization(Organization parentOrganization) {
		if(!getParentOrganizations().contains(parentOrganization)) {
			getParentOrganizations().add(parentOrganization);
		}
	}
	
	/**
	 * 
	 * @param parentOrganization
	 */
	public void removeParentOrganization(Organization parentOrganization) {
		getParentOrganizations().remove(parentOrganization);
	}

	/**
	 * @return the childrenOrganizations
	 */
	public Set<Organization> getChildrenOrganizations() {
		return childrenOrganizations;
	}

	/**
	 * @param childrenOrganizations
	 *            the childrenOrganizations to set
	 */
	public void setChildrenOrganizations(Set<Organization> childrenOrganizations) {
		this.childrenOrganizations = childrenOrganizations;
	}
	
	/**
	 * 
	 * @param childOrganization
	 */
	public void addChildOrganization(Organization childOrganization) {
		if(!getChildrenOrganizations().contains(childOrganization)) {
			getChildrenOrganizations().add(childOrganization);
		}
	}
	
	/**
	 * 
	 * @param childOrganization
	 */
	public void removeChildOrganization(Organization childOrganization) {
		getChildrenOrganizations().remove(childOrganization);
	}

	/**
	 * @return the emails
	 */
	public Set<String> getEmails() {
		return emails;
	}

	/**
	 * @param emails
	 *            the emails to set
	 */
	public void setEmails(Set<String> emails) {
		this.emails = emails;
	}
	
	/**
	 * 
	 * @param email
	 */
	public void addEmail(String email) {
		if(!getEmails().contains(email)) {
			getEmails().add(email);
		}
	}
	
	/**
	 * 
	 * @param email
	 */
	public void removeEmail(String email) {
		getEmails().remove(email);
	}
}
