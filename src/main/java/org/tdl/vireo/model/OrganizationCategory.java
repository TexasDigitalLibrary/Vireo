package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import org.tdl.vireo.model.Organization;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"name" , "level"})})
public class OrganizationCategory extends BaseEntity {
	
	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private int level;
	
	@OneToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = LAZY, mappedBy = "category")
	private Set<Organization> organizations;
	
	public OrganizationCategory() {
		setOrganizations(new HashSet<Organization>());
	}
	
	public OrganizationCategory(String name, int level) {
		this();
		setName(name);
		setLevel(level);
	}
	
	/**
	 * 
	 * @return String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 
	 * @return Set<Organization> organizations
	 */
	public Set<Organization> getOrganizations() {
	    return organizations;
	}

	/**
	 * 
	 * @param organizations
	 */
	public void setOrganizations(Set<Organization> organizations) {
	    this.organizations = organizations;
	}
	
	/**
	 * 
	 * @param organization
	 * @return boolean success
	 */
	public boolean addOrganization(Organization organization) {
		return getOrganizations().add(organization);
	}
	
	/**
	 * 
	 * @param organization
	 * @return boolean success
	 */
	public boolean removeOrganization(Organization organization) {
		return getOrganizations().remove(organization);
	}

}
