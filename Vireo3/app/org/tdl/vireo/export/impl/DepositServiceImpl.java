package org.tdl.vireo.export.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.export.DepositService;
import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.export.Packager;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.modules.spring.Spring;


/**
 * Implementation of the deposit service interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DepositServiceImpl implements DepositService{

	// The repository of people
	public PersonRepository personRepo;
	public ErrorLog errorLog;
	
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
	 * @param errorLog
	 *            The error log
	 */
	public void setErrorLog(ErrorLog errorLog) {
		this.errorLog = errorLog;
	}
	
	/**
	 * @param context
	 *            The security context managing who is currently logged in.
	 */
	public void setSecurityContext(SecurityContext context) {
		this.context = context;
	}
	
	/**
	 * @param jobManager The manager for background jobs.
	 */
	public void setJobManager(JobManager jobManager){
		this.jobManager = jobManager;
	}
	
	
	@Override
	public void deposit(DepositLocation location, Submission submission, State successState, boolean wait) {
		// Check our input
		if (location == null)
			throw new IllegalArgumentException("A deposit location is required.");
		
		if (location.getRepository() == null)
			throw new IllegalArgumentException("A repository URL is required.");
		
		if (location.getCollection() == null)
			throw new IllegalArgumentException("A collection URL is required.");
		
		if (location.getPackager() == null)
			throw new IllegalArgumentException("A deposit packager is required.");
		
		if (location.getDepositor() == null)
			throw new IllegalArgumentException("A depositor is required.");
		
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
		public final State successState;
		public final boolean runInThread;
		
		public final Long personId;
		public final boolean ignoreAuthorizations;
		
		public final JobMetadata metadata;
		
		
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
		 * @param runInThread
		 *            Normally this job is expected to be run as a background
		 *            job. However for some circumstances it is better to run
		 *            the deposit in the same thread. When this is done, errors
		 *            are re-thrown. This allows the UI to immediatly handle
		 *            those errors instead of inspecting the submission's action
		 *            log.
		 */
		public DepositJob(DepositLocation location, Submission submission,
				State successState, boolean runInThread) {
			this.location = location;
			this.submission = submission;
			this.successState = successState;
			this.runInThread = runInThread;
			
			if (context.getPerson() != null) {
				
				if (!context.isReviewer())
					throw new SecurityException("Not authorized to preform deposit operation.");
				
				if ( runInThread ) {
					this.personId = null;
					this.ignoreAuthorizations = false;
				} else {
					this.personId = context.getPerson().getId();
					this.ignoreAuthorizations = false;
				}
				
			} else {
				
				if (context.isAuthorizationActive())
					throw new SecurityException("Not authorized to preform deposit operation.");

				this.personId = null;
				this.ignoreAuthorizations = true;
			}
			
			if (!runInThread) {
				metadata = jobManager.register("Item "+submission.getId()+" Deposit", context.getPerson());
				metadata.setJob(this);
				metadata.getProgress().total = 0;
				metadata.getProgress().completed = 1;
				metadata.setStatus(JobStatus.READY);
			} else {
				metadata = null;
			}
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
			try {
				if (metadata != null)
					metadata.setStatus(JobStatus.RUNNING);
				
				if (ignoreAuthorizations) {
					context.turnOffAuthorization();
				}
				
				if (!runInThread && personId != null) {
					Person person = personRepo.findPerson(personId);
					if (person == null)
						throw new IllegalStateException("Unable to complete deposit job because person no longer exists.");
					
					// Log the person in for this job.
					context.login(person);
				}
	
				Submission submission = this.submission.merge();
				depositSubmission(submission);
				
				if (metadata != null) {
					metadata.getProgress().completed = 1;
					metadata.setStatus(JobStatus.SUCCESS);
				}
				
			} catch (RuntimeException re) {				
				Logger.fatal(re,"Unexepcted exception while attempting to deposit items. Aborted.");
				
				if (metadata != null) {
					metadata.setMessage(re.toString());
					metadata.setStatus(JobStatus.FAILED);
				}
				
				errorLog.logError(re, metadata);
				
				throw re;
				
			} finally {
				// Clean up the security context
				if (!runInThread && personId != null) {
					context.logout();
				} 
				if (ignoreAuthorizations) {
					context.restoreAuthorization();
				}
				
				if (metadata != null)
					metadata.setJob(null);
			}
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

			ExportPackage exportPackage = null;
			try {
				Packager packager = location.getPackager();
				Depositor depositor = location.getDepositor();
				
				exportPackage = packager.generatePackage(submission);
				
				String depositId = depositor.deposit(location, exportPackage);
				
				ActionLog log = submission.logAction("Deposited into repository collection '"+location.getCollection()+"'");
				log.save();
				
				if (depositId != null)
					submission.setDepositId(depositId);
					submission.setDepositDate(new Date());
				if (successState != null)
					submission.setState(successState);
				
				submission.save();
				
				String message = "Deposited submission #"+submission.getId()+" into repository: '"+location.getRepository()+"', collection: '"+location.getCollection()+"', and assigned depositId: '"+depositId+"'.";
				Logger.info(message);
				if (metadata != null)
					metadata.setMessage(message);
				
			} catch (RuntimeException re) {
				Logger.error(re,"Deposit failed for submission #"+submission.getId());
				ActionLog log = submission.logAction("Deposit failed while attempting to deposit into repository collection '"+location.getCollection()+"' because of the error '"+re.getMessage()+"' ");
				log.save();
				submission.save();
				
				errorLog.logError(re, metadata);
				
				if (runInThread)
					throw re;
			} finally {
				if (exportPackage != null)
					exportPackage.delete();
			}
		}
		
		
		
		
		
	};
	

}
