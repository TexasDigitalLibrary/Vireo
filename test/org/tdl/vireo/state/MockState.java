package org.tdl.vireo.state;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.model.Submission;

/**
 * Mock implementation of the state interface.
 * 
 * Note, many uses of the state object require that it be listed in the state
 * manager. These states will not be resolvable via the manager so their use may
 * be limited.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockState implements State {

	public String beanName = "MockState";
	public String displayName = "Mock State";
	public boolean inProgress = false;
	public boolean isActive = false;
	public boolean isArchived = false;
	public boolean isEditableByStudent = false;
	public boolean isEditableByReviewer = false;
	public boolean isDeletable = false;
	public boolean isDepositable = false;
	public boolean isApproved = false;
	public List<MockState> transitions = new ArrayList<MockState>();

	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public boolean isInProgress() {
		return inProgress;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public boolean isArchived() {
		return isArchived;
	}

	@Override
	public boolean isEditableByStudent() {
		return isEditableByStudent;
	}

	@Override
	public boolean isEditableByReviewer() {
		return isEditableByReviewer;
	}

	@Override
	public boolean isDeletable() {
		return isDeletable;
	}

	@Override
	public boolean isDepositable() {
		return isDepositable;
	}
	
	@Override
	public boolean isApproved() {
		return isApproved;
	}

	@Override
	public List<State> getTransitions(Submission submission) {
		return (List) transitions;
	}

}
