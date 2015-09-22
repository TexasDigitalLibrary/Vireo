package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"name" , "category_id"})})
public class Organization extends BaseEntity {

	@Column(columnDefinition = "TEXT", nullable = false)
	private String name;
	
	@ManyToOne(fetch = EAGER, optional = false)
	private OrganizationCategory category;

	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = LAZY, optional = true)
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
	
	public Organization(String name, OrganizationCategory category) {
		this();
		setName(name);
		setCategory(category);
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
	 * 
	 * @return
	 */
	public OrganizationCategory getCategory() {
		return category;
	}

	/**
	 * 
	 * @param catagory
	 */
	public void setCategory(OrganizationCategory category) {
		this.category = category;
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
		getParentOrganizations().add(parentOrganization);
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
		getChildrenOrganizations().add(childOrganization);
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
		getEmails().add(email);
	}
	
	/**
	 * 
	 * @param email
	 */
	public void removeEmail(String email) {
		getEmails().remove(email);
	}
	
}
