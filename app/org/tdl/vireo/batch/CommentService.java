package org.tdl.vireo.batch;

import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.state.State;

/**
 * Batch comment to service. This service handles the background tasks of adding
 * a comment and the option of sending an email on a set of submissions.
 * 
 * @author Micah Cooper
 * 
 */
public interface CommentService {

	/**
	 * Add a comment to all submissions identified by the provided filter.
	 * 
	 * @param filter
	 *            The search filter identifying the records to be changed.
	 * 
	 * @param comment
	 *            The comment to be added.
	 * 
	 * @param subject
	 *            The subject of the comment or email message.
	 * 
	 * @param visibility
	 *            A boolean to flag if the comment is private or public.
	 * 
	 * @param sendEmail
	 *            A boolean flag to specify w an email should be sent.
	 * 
	 * @param ccAdvisor
	 *            A boolean flag that is only effective when sending an email to
	 *            specify if the advisor should be CC'ed.
	 * 
	 * @return The metadata for the background job to change submissions. Use
	 *         this object to keep track of the task's progress.
	 */
	public JobMetadata comment(SearchFilter filter, String comment,
			String subject, Boolean visibility, Boolean sendEmail,
			Boolean ccAdvisor);

}
