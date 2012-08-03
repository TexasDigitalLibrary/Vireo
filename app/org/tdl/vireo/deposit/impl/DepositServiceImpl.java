package org.tdl.vireo.deposit.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;

import org.tdl.vireo.deposit.DepositPackage;
import org.tdl.vireo.deposit.DepositService;
import org.tdl.vireo.deposit.Depositor;
import org.tdl.vireo.deposit.Packager;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.state.State;

import play.Logger;
import play.PlayPlugin;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.JobsPlugin;
import play.test.PlayJUnitRunner;
import play.utils.Java;


/**
 * Implementation of the deposit service interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DepositServiceImpl implements DepositService{

	// The searcher used to find submissions in a batch.
	public Searcher searcher;

	// How many items should be processed at the same time.
	public int submissionsPerBatch = 10;
	
	// List of currently scheduled deposit jobs
	public Set<DepositJob> jobQueue = Collections.synchronizedSet(new HashSet<DepositJob>()); 

	/**
	 * @param searcher
	 *            Set the searcher used for identify batch of submissions to be
	 *            processed.
	 */
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	/**
	 * Set how many submissions should be processed at one time when operating
	 * in batch mode. This effects the amount of memory required when depositing
	 * items.
	 * 
	 * @param submissionsPerBatch
	 *            The number of items per batch.
	 */
	public void setSubmissionsPerBatch(int submissionsPerBatch) {
		this.submissionsPerBatch = submissionsPerBatch;
	}
	
	
	public boolean isDepositRunning() {
		return !jobQueue.isEmpty();
	}
	
	
	@Override
	public void deposit(DepositLocation location, Submission submission, State successState, boolean wait) {
		verifyLocation(location);
		
		if (submission == null || submission.getId() == null)
			throw new IllegalArgumentException("A persisted submission object is required");
		
		// kick off a job to deposit this submission
		DepositJob job = new DepositJob(location,submission,successState,wait);
		
		if (wait) {
			// Do it within this thread.
			job.doJob();
		} else {
			// Schedule for background execution.
			job.now();
		}
	}

	@Override
	public void deposit(DepositLocation location, SearchFilter filter, State successState) {
		verifyLocation(location);
		
		if (filter == null)
			throw new IllegalArgumentException("A search filter is required");
		
		// Kick off a job to deposit these submissions. We don't allow waiting for batches.
		new DepositJob(location,filter,successState).now();
	}
	

	/**
	 * Internal method to share the code of checking weather a location is valid
	 * before we kick off a job. Hopefully this would surface more errors
	 * quickly while the UI is still processing so the user can recieve
	 * immediate feedback.
	 * 
	 * @param location
	 *            The location object to verify
	 */
	protected void verifyLocation(DepositLocation location) {

		if (location == null)
			throw new IllegalArgumentException("A deposit location is required.");

		
		if (location.getRepositoryURL() == null)
			throw new IllegalArgumentException("A repository URL is required.");
		
		if (location.getCollectionURL() == null)
			throw new IllegalArgumentException("A collection URL is required.");
		
		if (location.getPackager() == null)
			throw new IllegalArgumentException("A deposit packager is required.");
		
		if (location.getDepositor() == null)
			throw new IllegalArgumentException("A depositor is required.");
		
	}
	
	/**
	 * Background job to drive the submission process. It will:
	 * 
	 * 1) Generate a package for the submission(s)
	 * 
	 * 2) Deposit the submission, one at a time if multiple.
	 * 
	 * 3) Update the state of the submission weather it was successful or failed.
	 * 
	 */
	public class DepositJob extends Job {
		
		// Member fields
		public final DepositLocation location;
		public final Submission submission;
		public final SearchFilter filter;
		public final State successState;
		public final boolean throwErrors;
		
		
		/**
		 * Construct a new one-off deposit job. This will deposit one
		 * submission.
		 * 
		 * @param location
		 *            The location where to deposit the submission into.
		 * @param submission
		 *            The submission to deposit.
		 * @param successState
		 *            The state submissions should be set to if the deposit is
		 *            successful.
		 * @param throwErrors
		 *            normally errors will be logged and a submission's status
		 *            updated correctly. However sometimes you want the errors
		 *            to be thrown for evaluation in the ui. For this case turn
		 *            throwErrors on, then in addition logging and updating the
		 *            status if an error is encountered it will be thrown.
		 */
		public DepositJob(DepositLocation location, Submission submission,
				State successState, boolean throwErrors) {
			this.location = location;
			this.submission = submission;
			this.filter = null;
			this.successState = successState;
			this.throwErrors = throwErrors;
			
			jobQueue.add(this);
		}

		/**
		 * Construct a new batch deposit job. This will deposit however many
		 * items are identified by the filter.
		 * 
		 * @param location
		 *            The location where to deposit submissions into.
		 * @param filter
		 *            The filter to identify submissions to be deposited.
		 * @param successState
		 *            The state submissions should be set to if the deposit is
		 *            successful.
		 */
		public DepositJob(DepositLocation location, SearchFilter filter,
				State successState) {
			this.location = location;
			this.submission = null;
			this.filter = filter;
			this.successState = successState;
			this.throwErrors = false;
			
			jobQueue.add(this);
		}
		
		/**
		 * Run the back ground job.
		 * 
		 * There are two cases for batch mode or not. In batch mode we search
		 * for the submissions handle that batch, then get the next batch until
		 * there are no more submissions left. Alternatively in the single mode
		 * the one submission is deposited.
		 */
		public void doJob() {			
			if (submission == null) {
				// This is the complex case, we're depositing a batch of items.
				int offset = 0;
				
				SearchResult<Submission> results = null;
				do {
					// Get the next batch of submissions.
					results = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, offset, submissionsPerBatch);
					 
					// Deposit them one-by-one.
					for (Submission submission : results.getResults()) {
						// Deposit the submission
						depositSubmission(submission);
					}
					
					// Calculate the next offset.
					offset = offset + results.getResults().size();
					
					// Commit the transaction and detach all the submissions.
					JPA.em().getTransaction().commit();
					JPA.em().clear();
					JPA.em().getTransaction().begin();					
				} while ( results.getResults().size() != 0 );
				
			} else {
				// This is the simple case, just deposit this one item.
				depositSubmission(submission);				
			}
			
			jobQueue.remove(this);
		}

		/**
		 * Deposit one submission. A package is generated, the depositor is used
		 * to deposit the package, and the state is updated. This method catches
		 * all runtime exceptions and uses that to update the submission's state
		 * accordingly.
		 * 
		 * @param submission
		 *            The submission to deposit.
		 */
		public void depositSubmission(Submission submission) {

			DepositPackage depositPackage = null;
			try {
				Packager packager = location.getPackager();
				Depositor depositor = location.getDepositor();
				
				depositPackage = packager.generatePackage(submission);
				
				String depositId = depositor.deposit(location, depositPackage);
				
				ActionLog log = submission.logAction("Deposited into repository collection '"+location.getCollectionURL()+"'");
				log.save();
				submission.setDepositId(depositId);
				if (successState != null)
					submission.setState(successState);
				submission.save();
				
				Logger.info("Deposited submissions #"+submission.getId()+" into repository: '"+location.getRepositoryURL().toExternalForm()+"', collection: '"+location.getCollectionURL()+"'.");
				
			} catch (RuntimeException re) {
				Logger.error(re,"Deposit failed for submission #"+submission.getId());
				ActionLog log = submission.logAction("Deposit failed while attempting to deposit into repository collection '"+location.getCollectionURL()+"' because of the error '"+re.getMessage()+"' ");
				log.save();
				submission.save();
				
				if (throwErrors)
					throw re;
			} finally {
				if (depositPackage != null)
					depositPackage.delete();
			}
		}
		
		
		
		
		
	};
	

}
