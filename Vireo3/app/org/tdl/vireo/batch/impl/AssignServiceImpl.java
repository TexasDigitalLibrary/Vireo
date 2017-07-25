package org.tdl.vireo.batch.impl;

import java.util.Iterator;

import org.tdl.vireo.batch.AssignService;
import org.tdl.vireo.error.ErrorLog;
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
 * Implement the assign service. This just loops through the submissions and
 * changes the assignee. Simple as that.
 * 
 * @author Micah Cooper
 */
public class AssignServiceImpl implements AssignService {

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
	public JobMetadata assign(SearchFilter filter, Long assignTo) {

		if (filter == null)
			throw new IllegalArgumentException("A search filter is required");
		
		if (context.isAuthorizationActive() && !context.isReviewer())
			throw new SecurityException("Unauthorized to assign submissions");

		AssignJob job = new AssignJob(filter, assignTo);

		job.now();

		return job.metadata;
	}


	/**
	 * The background job to change assignee on submissions.
	 */
	public class AssignJob extends Job {

		// Static state.
		public final SearchFilter filter;
		public final Long assignTo;
		public final Long personId;
		public final JobMetadata metadata;


		/**
		 * Construct a new background assign job.
		 * 
		 * @param filter
		 *            The filter to use to search for submissions.
		 *            
		 * @param assigneTo
		 * 			  The id of the person to assign submissions too.
		 */
		public AssignJob(SearchFilter filter, Long assignTo) {

			this.filter = filter;
			this.assignTo = assignTo;

			if (context.getPerson() != null) 
				personId = context.getPerson().getId();
			else
				personId = null;
			
			metadata = jobManager.register("Batch Assign",context.getPerson());
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
						throw new IllegalStateException("Unable to complete batch assign job because person no longer exists.");
					context.login(person);
				} else {
					context.turnOffAuthorization();
				}
				
				// Figure out how many submissions total we are working with
				long[] subIds = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING);
				metadata.getProgress().total = subIds.length;
				metadata.getProgress().completed = 0;
				
								
				// Assign!
				metadata.setMessage("Changing assignee on submissions...");
				for (long subId : subIds) {
				
					Submission sub = subRepo.findSubmission(subId);
					
					if(assignTo!=null) 
						sub.setAssignee(personRepo.findPerson(assignTo));
					else
						sub.setAssignee(null);
					
					sub.save();
					
					// Immediately save the transaction
					JPA.em().getTransaction().commit();
					JPA.em().clear();
					JPA.em().getTransaction().begin();
					
					// Don't let memory get out of control
					System.gc();
					metadata.getProgress().completed++;
				}
				
				metadata.setMessage(null);
				metadata.setStatus(JobStatus.SUCCESS);
				
			} catch (RuntimeException re) {
				
				Logger.fatal(re,"Unexpected exception while attempting to change assignee on items. Aborted, although some items may have been changed.");
				
				metadata.setMessage(re.toString());
				metadata.setStatus(JobStatus.FAILED);
				
				errorLog.logError(re, metadata);
				
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
