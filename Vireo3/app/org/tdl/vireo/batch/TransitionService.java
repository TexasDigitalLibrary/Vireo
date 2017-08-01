package org.tdl.vireo.batch;

import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.state.State;

/**
 * Batch transition service. This service handles the background tasks of
 * transitioning submissions from their current state into a new state.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface TransitionService {

	/**
	 * Transition a batch of submissions from their current state into the
	 * specified state.
	 * 
	 * @param filter
	 *            The search filter identifying the records to be processed.
	 * @param state
	 *            The state of each submission should be transitioned in to.
	 *            
	 * @return The metadata for the background job to transition submissions.
	 *         Use this object to keep track of the task's progress.
	 */
	public JobMetadata transition(SearchFilter filter, State state);

	/**
	 * Transition a batch of submissions from their current state into the
	 * specified state. If that state is a depositable state and a deposit
	 * location is provided then each submissions will be deposited.
	 * 
	 * @param filter
	 *            The search filter identifying the records to be processed.
	 * @param state
	 *            The state each submission should be transitioned in to.
	 * @param location
	 *            If the state is depositable, the deposit location where the
	 *            submission should be deposited.
	 * 
	 * @return The metadata for the background job to transition submissions.
	 *         Use this object to keep track of the task's progress.
	 */
	public JobMetadata transition(SearchFilter filter, State state,
			DepositLocation location);

}
