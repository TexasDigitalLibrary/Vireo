package org.tdl.vireo.batch;

import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.state.State;

/**
 * Batch assign to service. This service handles the background tasks of
 * assigning an owner on a set of submissions.
 * 
 * @author Micah Cooper
 * 
 */
public interface AssignService {

	/**
	 * Change assignee to all submissions identified by the provided filter.
	 * 
	 * @param filter
	 *            The search filter identifying the records to be changed.
	 *            
	 * @return The metadata for the background job to change submissions.
	 *         Use this object to keep track of the task's progress.
	 */
	public JobMetadata assign(SearchFilter filter, Long assignTo);
	
}
