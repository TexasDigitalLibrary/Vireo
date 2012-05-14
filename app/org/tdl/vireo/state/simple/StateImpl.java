package org.tdl.vireo.state.simple;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanNameAware;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;


/**
 * Simple implementation of the vireo state interface. This class basically just is a holder for the configuration injected from spring, this allows for the state transitions to be defined within spring so that they may be customized between different installations.
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
	 * @param inProgress Weather this state is considered inProgress
	 */
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @param active Weather this state is considered under active review
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isArchived() {
		return archived;
	}
	
	/**
	 * @param archived Weather this state is considered archived.
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public boolean isEditableByStudent() {
		return editableByStudent;
	}
	
	/**
	 * @param editable Weather the submission may be edited by the student.
	 */
	public void setEditableByStudent(boolean editable) {
		this.editableByStudent = editable;
	}

	@Override
	public boolean isEditableByReviewer() {
		return editableByReviewer;
	}
	
	/**
	 * @param editable Weather the submission may be edited by the reviewer.
	 */
	public void setEditableByReviewer(boolean editable) {
		this.editableByReviewer = editable;
	}

	@Override
	public List<State> getTransitions(Submission submission) {
		
		if (submission.getEmbargoType() == null || embargoTransitions.size() == 0)
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
