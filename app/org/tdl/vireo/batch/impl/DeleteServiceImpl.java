package org.tdl.vireo.batch.impl;

import java.util.Iterator;

import org.tdl.vireo.batch.DeleteService;
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
 * Implement the delete service. This just loops through the submissions and
 * deletes them. Simple as that.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DeleteServiceImpl implements DeleteService {

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
	public JobMetadata delete(SearchFilter filter) {

		if (filter == null)
			throw new IllegalArgumentException("A search filter is required");

		if (context.isAuthorizationActive() && !context.isReviewer())
			throw new SecurityException("Unauthorized to transition submissions.");

		DeleteJob job = new DeleteJob(filter);

		job.now();

		return job.metadata;
	}


	/**
	 * The background job to delete submissions.
	 */
	public class DeleteJob extends Job {

		// Static state.
		public final SearchFilter filter;
		public final Long personId;
		public final JobMetadata metadata;


		/**
		 * Construct a new background delete job.
		 * 
		 * @param filter
		 *            The filter to use to search for submissions.
		 */
		public DeleteJob(SearchFilter filter) {

			this.filter = filter;

			if (context.getPerson() != null) 
				personId = context.getPerson().getId();
			else
				personId = null;
			
			metadata = jobManager.register("Batch Delete",context.getPerson());
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
				
				// Delete!
				metadata.setMessage("Deleting submissions...");				
				for (long subId : subIds) {

					Submission sub = subRepo.findSubmission(subId);
					sub.delete();
					
					
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
				Logger.fatal(re,"Unexpected exception while attempting to deleting items. Aborted, although some items may have been deleted.");
				
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
