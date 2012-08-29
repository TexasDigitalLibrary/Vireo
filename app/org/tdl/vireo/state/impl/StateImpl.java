package org.tdl.vireo.state.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;


/**
 * Simple implementation of the vireo state interface. This class basically just
 * is a holder for the configuration injected from spring, this allows for the
 * state transitions to be defined within spring so that they may be customized
 * between different installations.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class StateImpl implements State, BeanNameAware {

	/** Injected properties **/
	public String beanName;
	public String displayName = "Unknown";
	public boolean inProgress = false;
	public boolean active = false;
	public boolean archived = false;
	public boolean editableByStudent = false;
	public boolean editableByReviewer = false;
	public boolean depositable = false;
	public boolean approved = false;
	public boolean deletable = false;
	
	
	public List<State> transitions = new ArrayList<State>();
	
	public List<State> embargoTransitions = new ArrayList<State>();
	
	
	@Override
	public String getBeanName() {
		return beanName;
	}
	
	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * @param displayName The displayable name for this state.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public boolean isInProgress() {
		return inProgress;
	}

	/**
	 * @param inProgress Whether this state is considered inProgress
	 */
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @param active Whether this state is considered under active review
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isArchived() {
		return archived;
	}
	
	/**
	 * @param archived Whether this state is considered archived.
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public boolean isEditableByStudent() {
		return editableByStudent;
	}
	
	/**
	 * @param editable Whether the submission may be edited by the student.
	 */
	public void setEditableByStudent(boolean editable) {
		this.editableByStudent = editable;
	}

	@Override
	public boolean isEditableByReviewer() {
		return editableByReviewer;
	}
	
	/**
	 * @param editable Whether the submission may be edited by the reviewer.
	 */
	public void setEditableByReviewer(boolean editable) {
		this.editableByReviewer = editable;
	}
	
	@Override
	public boolean isDeletable() {
		return deletable;
	}
	
	/**
	 * @param deletable Whether the submission may be permanently deleted by the reviewer.
	 */
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}
	
	@Override
	public boolean isDepositable() {
		return depositable;
	}
	
	/**
	 * @param deletable Whether the submission may be permanently deleted by the reviewer.
	 */
	public void setDepositable(boolean depositable) {
		this.depositable = depositable;
	}
	
	@Override
	public boolean isApproved() {
		return approved;
	}
	
	/**
	 * @param approved Whether the submission should be approved when transitioned into this state.
	 */
	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	@Override
	public List<State> getTransitions(Submission submission) {
		
		boolean embargoed = false;
		if (submission.getEmbargoType() != null) {
			Integer duration = submission.getEmbargoType().getDuration();
			
			// Null duration means it is indefinitely embargoed.
			// A duration of anything greater than zero is defined embargo period.
			// However a duration of zero, is the same as having no embargo type specified. (it's the none type)
			if (duration == null || duration > 0)
				embargoed = true;
		}
		
		if (!embargoed || embargoTransitions.size() == 0)
			return transitions;
		
		return embargoTransitions;
	}
	
	/**
	 * @param transitions The transitions available when the submission has no embargo listed.
	 */
	public void setTransitions(List<State> transitions) {
		this.transitions = transitions;
	}
	
	/**
	 * @param transitions The transitions available when the submission is under embargo.
	 */
	public void setEmbargoTransitions(List<State> transitions) {
		this.embargoTransitions = transitions;
	}
	

}
