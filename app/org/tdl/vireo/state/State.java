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
	 * @return Weather this state is considered to be in-progress by the
	 *         student. I.e. are they still editing it prior to submission.
	 */
	public boolean isInProgress();

	/**
	 * @return Weather this state is considered under active review by
	 *         reviewers. I.e. after a student has submitted the submission. But
	 *         before it has been approved & published.
	 */
	public boolean isActive();

	/**
	 * @return Weather this state is in a terminal state such as published,
	 *         cancelled, etc.,
	 */
	public boolean isArchived();

	/**
	 * @return Weather the student can edit the submission during this state.
	 */
	public boolean isEditableByStudent();

	/**
	 * @return Weather a reviewer can edit the submission during this state.
	 */
	public boolean isEditableByReviewer();

	/**
	 * Return a list of valid transitions for this submission.
	 * 
	 * @param submission
	 *            The submission in its current state.
	 * @return A list of transitions.
	 */
	public List<State> getTransitions(Submission submission);

}
