package org.tdl.vireo.batch;

import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.state.State;

/**
 * Batch delete service. This service handles the background tasks of
 * deleteing a set of submissions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface DeleteService {

	/**
	 * Delete all submissions identified by the provided filter.
	 * 
	 * @param filter
	 *            The search filter identifying the records to be deleted.
	 *            
	 * @return The metadata for the background job to delete submissions.
	 *         Use this object to keep track of the task's progress.
	 */
	public JobMetadata delete(SearchFilter filter);
	
}
