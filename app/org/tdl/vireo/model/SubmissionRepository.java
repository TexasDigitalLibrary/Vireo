package org.tdl.vireo.model;

import java.util.Iterator;
import java.util.List;

import org.tdl.vireo.search.Semester;

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
	 * Find all the submission objects for the given list of ids. Some
	 * implementations may impose a limit on the number of ids that are
	 * permissible. If an submission object was not found for the particular id,
	 * then it will not appear in the resulting list and no exception will be
	 * thrown. The result list may be in any order.
	 * 
	 * @param submissionIds
	 *            The ids of all the submission objects.
	 * @return A list of submission objects.
	 */
	public List<Submission> findSubmissions(List<Long> submissionIds);

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
	 * Find all submissions in Vireo. Since this can be potentially huge, we
	 * return an iterator. After each object is cycled by the iterator it may
	 * potentially be removed from the current database connection.
	 * 
	 * @return An iterator over all submissions.
	 */
	public Iterator<Submission> findAllSubmissions();
	
	/**
	 * Find how many submissions are stored in the persistent repository.
	 * 
	 * @return the number total number of submissions stored in the persistent
	 *         repository.
	 */
	public long findSubmissionsTotal();
	
	// //////////////////////////////////////////////////////////////
	// Submission informational
	// //////////////////////////////////////////////////////////////
	
	/**
	 * @return A list of all graduation semesters for which there are submissions
	 *         recorded for.
	 */
	public List<Semester> findAllGraduationSemesters();
	
	/**
	 * @return A list of all years for which submissions occured during that year.
	 */
	public List<Integer> findAllSubmissionYears();
	
	/**
	 * @return A list of all colleges for which submissions have been submitted under.
	 */
	public List<String> findAllColleges();

	/**
	 * @return A list of all departments for which departments have been submitted under.
	 */
	public List<String> findAllDepartments();
	
	/**
	 * @return A list of all majors for which majors have been submitted under.
	 */
	public List<String> findAllMajors();

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
	 * Find all the action log objects for the given list of ids. Some
	 * implementations may impose a limit on the number of ids that are
	 * permissible. If a log object was not found for the particular id, then it
	 * will not appear in the resulting list and no exception will be thrown.
	 * The result list may be in any order.
	 * 
	 * @param logIds
	 *            The ids of all the action log objects.
	 * @return A list of action log objects.
	 */
	public List<ActionLog> findActionLogs(List<Long> logIds);

	/**
	 * Find all action logs for a particular submission order by date.
	 * 
	 * @param submission
	 *            The submission
	 * @return A list of action logs, or an empty list of none or found.
	 */
	public List<ActionLog> findActionLog(Submission submission);

	/**
	 * Find all Action Logs in Vireo. Since this can be potentially huge, we
	 * return an iterator. After each object is cycled by the iterator it may
	 * potentially be removed from the current database connection.
	 * 
	 * @return An iterator over all action logs.
	 */
	public Iterator<ActionLog> findAllActionLogs();
	
	/**
	 * Find how many action logs are stored in the persistent repository.
	 * 
	 * @return the number total number of action logs stored in the persistent
	 *         repository.
	 */
	public long findActionLogsTotal();
	
	// //////////////////
	// Filter Search
	// //////////////////

	/**
	 * Create a brand new search filter.
	 * 
	 * @param creator
	 *            The person who is creating and owns this search filter.
	 * @param name
	 *            A unique filter name among all those created by this user.
	 * @return The new filter.
	 */
	public NamedSearchFilter createSearchFilter(Person creator, String name);

	/**
	 * Find a search filter by unique id.
	 * 
	 * @param id
	 *            The id of the search filter.
	 * @return The search filter, or null if not found.
	 */
	public NamedSearchFilter findSearchFilter(Long id);

	/**
	 * Find all search filters that are either owned by this user, or are
	 * flagged as public.
	 * 
	 * @param creator
	 *            The potential creator, may be null. In this case all public
	 *            filters are returned.
	 * @return The unordered list of search filters.
	 */
	public List<NamedSearchFilter> findSearchFiltersByCreatorOrPublic(Person creator);
	
	/**
	 * Find the named search filter that was created by this user.
	 * 
	 * @param creator
	 *            The filter's creator
	 * @param name
	 *            The unique name of the filter
	 * @return The search filter, or null if not found.
	 */
	public NamedSearchFilter findSearchFilterByCreatorAndName(Person creator,
			String name);

	/**
	 * Find all search filters regardless of who they owned by or whether they
	 * are flagged as public.
	 * 
	 * @return
	 */
	public List<NamedSearchFilter> findAllSearchFilters();
	
}
