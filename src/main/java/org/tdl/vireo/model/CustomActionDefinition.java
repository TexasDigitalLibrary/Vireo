package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class CustomActionDefinition extends BaseOrderedEntity {
	
	@Column(nullable = false, unique = true, length = 255)
	@Size(min=1, max=255)
	private String label;
	
	@Column(nullable = false)
	@JsonProperty("isStudentVisible")
	@NotNull
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
	@JsonIgnore
	public Boolean isStudentVisible() {
		return isStudentVisible;
	}

	/**
	 * @param isStudentVisible the isStudentVisible to set
	 */
	@JsonIgnore
	public void isStudentVisible(Boolean isStudentVisible) {
		this.isStudentVisible = isStudentVisible;
	}
}
