package org.tdl.vireo.state;

import java.util.List;

import org.tdl.vireo.model.Submission;

/**
 * A discrete vireo submission state.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface State {

	/**
	 * @return The technical name of this state.
	 */
	public String getBeanName();

	/**
	 * @return The english display name of this state.
	 */
	public String getDisplayName();

	/**
	 * @return Whether this state is considered to be in-progress by the
	 *         student. I.e. are they still editing it prior to submission.
	 */
	public boolean isInProgress();

	/**
	 * @return Whether this state is considered under active review by
	 *         reviewers. I.e. after a student has submitted the submission. But
	 *         before it has been approved & published.
	 */
	public boolean isActive();

	/**
	 * @return Whether this state is in a terminal state such as published,
	 *         cancelled, etc.,
	 */
	public boolean isArchived();

	/**
	 * @return Whether the student can edit the submission during this state.
	 */
	public boolean isEditableByStudent();

	/**
	 * @return Whether a reviewer can edit the submission during this state.
	 */
	public boolean isEditableByReviewer();

	/**
	 * @return Whether this state should display the option to permanently
	 *         remove the submission.
	 */
	public boolean isDeletable();

	/**
	 * @return Whether this state should be deposited in to a repository when
	 *         transitioning into this state.
	 */
	public boolean isDepositable();
	
	/**
	 * @return Whether a submission transitioned into this state should be
	 *         considered approved. If so then the approvalDate will be set of
	 *         the submission.
	 */
	public boolean isApproved();

	/**
	 * Return a list of valid transitions for this submission.
	 * 
	 * @param submission
	 *            The submission in its current state.
	 * @return A list of transitions.
	 */
	public List<State> getTransitions(Submission submission);

}
