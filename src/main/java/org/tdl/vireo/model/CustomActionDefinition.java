package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class CustomActionDefinition extends BaseEntity {
	
	@Column(nullable = false, unique = true, length = 255)
	private String label;
	
	@Column(nullable = false)
	@JsonProperty("isStudentVisible")
	private Boolean isStudentVisible;
	
	public CustomActionDefinition() { }
	
	public CustomActionDefinition(String label, Boolean isStudentVisible) { 
		this();
		setLabel(label);
		isStudentVisible(isStudentVisible);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the isStudentVisible
	 */
	public Boolean isStudentVisible() {
		return isStudentVisible;
	}

	/**
	 * @param isStudentVisible the isStudentVisible to set
	 */
	public void isStudentVisible(Boolean isStudentVisible) {
		this.isStudentVisible = isStudentVisible;
	}
}
