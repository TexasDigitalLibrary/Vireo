package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Custom actions are check lists that may be associated with a submission.
 * These actions are intended to record internal status of various workflow
 * processes performed by the review staff using Vireo. Vireo managers are able
 * to modify the list of custom actions that are available, this is the list of
 * definitions.
 * 
 */
@Entity
@Table(name = "custom_action_definition")
public class CustomActionDefinition extends BaseEntity {
	
	@Column(nullable = false)
	private int displayOrder;

	@Column(nullable = false, unique = true, length = 255)
	private String label;

	@Column(nullable = false, columnDefinition="BOOLEAN DEFAULT false")
	private Boolean isStudentVisible;
	
	public CustomActionDefinition() {
		//this.displayOrder = 0;
	}
	
	public CustomActionDefinition(String label, Boolean isStudentVisible) { 
		//this();
		setLabel(label);
		setIsStudentVisible(isStudentVisible);
	}

	/**
	 * @return the displayOrder
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * @param displayOrder the displayOrder to set
	 */
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
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
	public Boolean getIsStudentVisible() {
		return isStudentVisible;
	}

	/**
	 * @param isStudentVisible the isStudentVisible to set
	 */
	public void setIsStudentVisible(Boolean isStudentVisible) {
		this.isStudentVisible = isStudentVisible;
	}
	


	//TODO to be deleted 

	/**
	 * @return The label of this custom action.
	 */
	//public String getLabel();

	/**
	 * @param label
	 *            The new label of this custom action.
	 */
	//public void setLabel(String label);
	
	/**
	 * @return The boolean value of this custom action being visible to the student
	 */
	//public Boolean isStudentVisible();
	
	/**
	 * @param isStudentVisible
	 * 				The boolean value of this custom action being visible to the student
	 */
	//public void setIsStudentVisible(Boolean isStudentVisible);
}
