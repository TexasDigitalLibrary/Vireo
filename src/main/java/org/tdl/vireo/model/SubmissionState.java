package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class SubmissionState extends BaseEntity {
	String name;
	Boolean archived, publishable, deletable, editableByReviewer, editableByStudent, active;
	@ManyToOne(targetEntity = org.tdl.vireo.model.SubmissionState.class)
	Set<SubmissionState> transitionSubmissionStates;

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
	 * @return the archived
	 */
	public Boolean getArchived() {
		return archived;
	}

	/**
	 * @param archived
	 *            the archived to set
	 */
	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	/**
	 * @return the publishable
	 */
	public Boolean getPublishable() {
		return publishable;
	}

	/**
	 * @param publishable
	 *            the publishable to set
	 */
	public void setPublishable(Boolean publishable) {
		this.publishable = publishable;
	}

	/**
	 * @return the deletable
	 */
	public Boolean getDeletable() {
		return deletable;
	}

	/**
	 * @param deletable
	 *            the deletable to set
	 */
	public void setDeletable(Boolean deletable) {
		this.deletable = deletable;
	}

	/**
	 * @return the editableByReviewer
	 */
	public Boolean getEditableByReviewer() {
		return editableByReviewer;
	}

	/**
	 * @param editableByReviewer
	 *            the editableByReviewer to set
	 */
	public void setEditableByReviewer(Boolean editableByReviewer) {
		this.editableByReviewer = editableByReviewer;
	}

	/**
	 * @return the editableByStudent
	 */
	public Boolean getEditableByStudent() {
		return editableByStudent;
	}

	/**
	 * @param editableByStudent
	 *            the editableByStudent to set
	 */
	public void setEditableByStudent(Boolean editableByStudent) {
		this.editableByStudent = editableByStudent;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the transitionSubmissionStates
	 */
	public Set<SubmissionState> getTransitionSubmissionStates() {
		return transitionSubmissionStates;
	}

	/**
	 * @param transitionSubmissionStates
	 *            the transitionSubmissionStates to set
	 */
	public void setTransitionSubmissionStates(Set<SubmissionState> transitionSubmissionStates) {
		this.transitionSubmissionStates = transitionSubmissionStates;
	}
}
