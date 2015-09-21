package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FieldValue extends BaseEntity {
	
	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
	private FieldProfile fieldProfile;
	
	@Column(columnDefinition = "TEXT", nullable = true)
	private String value;
	
	public FieldValue() {}
	
	public FieldValue(FieldProfile fieldProfile) {
		setFieldProfile(fieldProfile);
	}

	/**
	 * @return the fieldProfile
	 */
	public FieldProfile getFieldProfile() {
		return fieldProfile;
	}

	/**
	 * @param fieldProfile
	 *            the fieldProfile to set
	 */
	public void setFieldProfile(FieldProfile fieldProfile) {
		this.fieldProfile = fieldProfile;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
