package org.tdl.vireo.batch.impl;

import java.util.Iterator;

import org.tdl.vireo.batch.TransitionService;
import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.export.DepositService;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.modules.spring.Spring;

/**
 * Implement the transition service. This follows a basic implementation that
 * supports both changing the status of a submission, and in addition if
 * instructed will also use the deposit service to deposit a transition.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class TransitionServiceImpl implements TransitionService {

	// The repositories
	public PersonRepository personRepo;
	public SubmissionRepository subRepo;
	public ErrorLog errorLog;

	// The searcher used to find submissions in a batch.
	public Searcher searcher;
	
	// The security context, who's logged in.
	public SecurityContext context;

	// Manager of all background jobs
	public JobManager jobManager;
	
	// The service handling deposits.
	public DepositService depositService;

	/**
	 * @param repo
	 *            The person repository
	 */
	public void setPersonRepository(PersonRepository repo) {
		this.personRepo = repo;
	}
	
	/**
	 * @param repo
	 *            The submission repository
	 */
	public void setSubmissionRepository(SubmissionRepository repo) {
		this.subRepo = repo;
	}
	
	/**
	 * @param errorLog
	 *            The error log
	 */
	public void setErrorLog(ErrorLog errorLog) {
		this.errorLog = errorLog;
	}
	
	/**
	 * @param searcher
	 *            Set the searcher used for identify batch of submissions to be
	 *            processed.
	 */
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	/**
	 * @param context
	 *            The security context managing who is currently logged in.
	 */
	public void setSecurityContext(SecurityContext context) {
		this.context = context;
	}

	/**
	 * @param jobManager The manager of background jobs
	 */
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}


	@Override
	public JobMetadata transition(SearchFilter filter, State state) {
		return transition(filter,state,null);
	}

	@Override
	public JobMetadata transition(SearchFilter filter, State state, DepositLocation location) {

		if (filter == null)
			throw new IllegalArgumentException("A search filter is required");

		if (state == null)
			throw new IllegalArgumentException("A state is required");

		if (location != null) {
			// If the location is provided check that it is good.

			if (location.getRepository() == null)
				throw new IllegalArgumentException("A repository URL is required.");

			if (location.getCollection() == null)
				throw new IllegalArgumentException("A collection URL is required.");

			if (location.getPackager() == null)
				throw new IllegalArgumentException("A deposit packager is required.");

			if (location.getDepositor() == null)
				throw new IllegalArgumentException("A depositor is required.");
		}

		if (context.isAuthorizationActive() && !context.isReviewer())
			throw new SecurityException("Unauthorized to transition submissions.");

		TransitionJob job = new TransitionJob(filter,state,location);

		job.now();

		return job.metadata;
	}


	/**
	 * The background job to update submissions.
	 */
	public class TransitionJob extends Job {

		// Static state.
		public final SearchFilter filter;
		public final State state;
		public final DepositLocation location;
		public final Long personId;
		public final JobMetadata metadata;


		/**
		 * Construct a new background transition job.
		 * 
		 * @param filter
		 *            The filter to use to search for submissions.
		 * @param state
		 *            The new state submissions should be set too.
		 * @param location
		 *            The location where submissions should be deposited to (may
		 *            be null)
		 */
		public TransitionJob(SearchFilter filter, State state, DepositLocation location) {

			this.filter = filter;
			this.state = state;
			this.location = location;

			if (context.getPerson() != null) 
				personId = context.getPerson().getId();
			else
				personId = null;

			String jobName = "Batch Update Status";
			if (state.isDepositable() && location != null) 
				jobName = "Batch Deposit";
			
			metadata = jobManager.register(jobName,context.getPerson());
			metadata.setJob(this);
			metadata.setStatus(JobStatus.READY);
			metadata.setMessage("Waiting to start...");
		}


		/**
		 * Do the work.
		 */
		public void doJob() {
			try {
				metadata.setStatus(JobStatus.RUNNING);
				metadata.setMessage("Preparing to run...");
				
				// Handle authorization
				if (personId != null) {
					Person person = personRepo.findPerson(personId);
					if (person == null)
						throw new IllegalStateException("Unable to complete transition job because person no longer exists.");
					context.login(person);
				} else {
					context.turnOffAuthorization();
				}
				
				// Figure out how many submissions total we are exporting
				long[] subIds = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING);
				metadata.getProgress().total = subIds.length;
				metadata.getProgress().completed = 0;
				
				// Transition!
				if (state.isDepositable() && location != null) 
					metadata.setMessage("Depositing submissions...");
				else
					metadata.setMessage("Updating submissions...");
				for (long subId : subIds) {

					Submission sub = subRepo.findSubmission(subId);
					
					if (state.isDepositable() && location != null) {
						// We're doing a deposit transition
						depositService.deposit(location, sub, state, true);
					} else {
						// Do a regular transition
						sub.setState(state);
						sub.save();
					}
					
					// Immediately save the transaction
					JPA.em().getTransaction().commit();
					JPA.em().clear();
					JPA.em().getTransaction().begin();
					
					// Don't let memory get out of controll
					System.gc();
					metadata.getProgress().completed++;
				}
				
				metadata.setMessage(null);
				metadata.setStatus(JobStatus.SUCCESS);
				
			} catch (RuntimeException re) {
				Logger.fatal(re,"Unexpected exception while attempting to transition items. Aborted.");
				
				errorLog.logError(re, metadata);
				
				metadata.setMessage(re.toString());
				metadata.setStatus(JobStatus.FAILED);
				
			} finally {

				if (personId != null) {
					context.logout();
				} else {
					context.restoreAuthorization();
				}


				metadata.setJob(null);
			}

		}

	}

}
