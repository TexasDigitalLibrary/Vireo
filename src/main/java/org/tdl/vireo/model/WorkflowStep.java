package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class WorkflowStep extends BaseEntity {

	@Column(nullable = false)
	private String name;

	@ManyToMany(cascade = { DETACH, REFRESH, MERGE, PERSIST }, fetch = EAGER)
	private Set<FieldProfile> fieldProfiles;

	public WorkflowStep() {
		setFieldProfiles(new HashSet<FieldProfile>());
	}
	
	public WorkflowStep(String name) {
		this();
		setName(name);
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
	public Set<FieldProfile> getFieldProfiles() {
		return fieldProfiles;
	}

	/**
	 * 
	 * @param param
	 */
	public void setFieldProfiles(Set<FieldProfile> param) {
		this.fieldProfiles = param;
	}
	
	/**
	 * 
	 * @param fieldProfile
	 */
	public void addFieldProfile(FieldProfile fieldProfile) {
		getFieldProfiles().add(fieldProfile);
	}
	
	/**
	 * 
	 * @param fieldProfile
	 */
	public void removeFieldProfile(FieldProfile fieldProfile) {
		getFieldProfiles().remove(fieldProfile);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof WorkflowStep)) {
			return false;
		}
		WorkflowStep other = (WorkflowStep) obj;
		return id.equals(other.id);
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {
	    return id == null ? 0 : 29 * id.hashCode() + 31 * name.hashCode();
	}
	
}
