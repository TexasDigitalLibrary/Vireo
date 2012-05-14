package org.tdl.vireo.model;

import java.util.List;
import java.util.Set;

/**
 * The Vireo persistent repository for submissions. This object follows the
 * spring repository pattern, where this is the source for creating and locating
 * all persistent model objects. It is intended that this object will be
 * injected into all other spring beans that need access to vireo submissions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */

public interface SubmissionRepository {

	// //////////////////
	// Submission Model
	// //////////////////

	/**
	 * Start a brand new submission for this submitter.
	 * 
	 * @param submitter
	 *            The submitter of the submission.
	 * @return A new submission.
	 */
	public Submission createSubmission(Person submitter);

	/**
	 * Find a submission by id.
	 * 
	 * @param id
	 *            The unique id of the submission.
	 * @return The submission object or null if not found.
	 */
	public Submission findSubmission(Long id);

	/**
	 * Find a submission by email hash
	 * 
	 * @param id
	 *            The email hash of the submission.
	 * @return The submission object or null if not found.
	 */
	public Submission findSubmissionByEmailHash(String emailHash);

	/**
	 * Find all submissions for a particular submitter.
	 * 
	 * @param Submitter
	 *            The submitter
	 * @return A list of all submissions for the submitter, or an empty set if
	 *         there are none.
	 */
	public List<Submission> findSubmission(Person Submitter);

	/**
	 * Perform a filter search of submissions returning the result.
	 * 
	 * @param filter
	 *            The filter object to search by.
	 * @param orderBy
	 *            The order of submissions
	 * @param direction
	 *            Weather the order is ascending or descending.
	 * @param offset
	 *            What index to off set the list into.
	 * @param limit
	 *            How many submissions to returned.
	 * @return The list of submissions.
	 */
	public List<Submission> filterSearchSubmissions(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit);

	// //////////////////////////////////////////////////////////////
	// Attachment, Committee Member, and Custom Action Value Models
	// //////////////////////////////////////////////////////////////

	/**
	 * Find an attachment by id.
	 * 
	 * @param id
	 *            The unique id of the attachment.
	 * @return The attachment object or null if not found.
	 */
	public Attachment findAttachment(Long id);

	/**
	 * Find an committee member by id.
	 * 
	 * @param id
	 *            The unique id of the committee member.
	 * @return The committee member object or null if not found.
	 */
	public CommitteeMember findCommitteeMember(Long id);

	/**
	 * Find an custom action value by id.
	 * 
	 * @param id
	 *            The unique id of the custom action value.
	 * @return The custom action value object or null if not found.
	 */
	public CustomActionValue findCustomActionValue(Long id);

	// //////////////////
	// Action Log Model
	// //////////////////

	/**
	 * Find an action log by unique id.
	 * 
	 * @param id
	 *            The id of the action log.
	 * @return The action log or null if not found.
	 */
	public ActionLog findActionLog(Long id);

	/**
	 * Find all action logs for a particular submission order by date.
	 * 
	 * @param submission
	 *            The submission
	 * @return A list of action logs, or an empty list of none or found.
	 */
	public List<ActionLog> findActionLog(Submission submission);

	/**
	 * Perform a filter search of ActionLogs returning the result.
	 * 
	 * @param filter
	 *            The filter object to search by.
	 * @param orderBy
	 *            The order of actionLogs
	 * @param direction
	 *            Weather the order is ascending or descending.
	 * @param offset
	 *            What index to off set the list into.
	 * @param limit
	 *            How many logs to returned.
	 * @return The list of action logs.
	 */
	public List<ActionLog> filterSearchActionLogs(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit);

}
