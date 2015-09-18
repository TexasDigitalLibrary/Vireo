package org.tdl.vireo.model;

import javax.persistence.Entity;

@Entity
public class FieldValue extends BaseEntity {
	FieldProfile fieldProfile;
	Submission submission;
	String value;

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
	 * @return the submission
	 */
	public Submission getSubmission() {
		return submission;
	}

	/**
	 * @param submission
	 *            the submission to set
	 */
	public void setSubmission(Submission submission) {
		this.submission = submission;
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
