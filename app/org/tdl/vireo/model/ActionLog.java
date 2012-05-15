package org.tdl.vireo.model;

import java.util.Date;

import org.tdl.vireo.state.State;

/**
 * Each action log represents a discrete change to a submission. Taken together
 * all action logs list the chronologically all the changes that have occurred.
 * Since action logs by their nature or intended to be merely for auditing and
 * documentation purposes the interface does not provide a way to modify an
 * existing log entry. The only modifiable property is the private flag which
 * indicates weather the log should be visible to students.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface ActionLog extends AbstractModel {

	/**
	 * @return The submission affected by this action log.
	 */
	public Submission getSubmission();

	/**
	 * @return The state of this submission, if the submission changed state
	 *         during the event covered by this action log then the new state
	 *         of the submission is returned.
	 */
	public State getSubmissionState();

	/**
	 * @return The person who made or is responsible for the the change.
	 */
	public Person getPerson();

	/**
	 * @return Date this action was performed.
	 */
	public Date getActionDate();

	/**
	 * @return If this action affected a attachment then the attachment is returned,
	 *         otherwise null is returned.
	 */
	public Attachment getAttachment();

	/**
	 * @return An English description of the action.
	 */
	public String getEntry();

	/**
	 * @return True if this action log should only be visible my vireo staff,
	 *         otherwise false.
	 */
	public boolean isPrivate();

	/**
	 * @param privateFlag
	 *            Set weather this action log should be visible by students.
	 */
	public void setPrivate(boolean privateFlag);

}
