package org.tdl.vireo.batch.impl;

import java.util.Iterator;

import org.tdl.vireo.batch.CommentService;
import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.error.ErrorLog;
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
public class CommentServiceImpl implements CommentService {

	// The email service
	public EmailService emailService;
	
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
	
	/**
	 * @param emailService The email service
	 */
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Override
	public JobMetadata comment(SearchFilter filter, String comment,
			String subject, Boolean visibility, Boolean sendEmail, Boolean ccAdvisor) {
		if (filter == null)
			throw new IllegalArgumentException("A search filter is required");
		
		if (comment == null)
			throw new IllegalArgumentException("A comment is required");

		if (context.isAuthorizationActive() && !context.isReviewer())
			throw new SecurityException("Unauthorized to transition submissions.");

		CommentJob job = new CommentJob(filter, comment, subject, visibility, ccAdvisor, sendEmail);

		job.now();

		return job.metadata;
	}

	/**
	 * The background job to place a comment or email on submissions.
	 */
	public class CommentJob extends Job {

		// Static state.
		public final SearchFilter filter;
		public final String comment;
		public final String subject;
		public final Boolean visibility;
		public final Boolean ccAdvisor;
		public final Boolean sendEmail;
		public final Long personId;
		public final JobMetadata metadata;


		/**
		 * Construct a new background assign job.
		 * 
		 * @param filter
		 *            The filter to use to search for submissions.
		 * @param comment
		 * 			  The text of the comment or email message.
		 * @param subject
		 * 			  The subject of the comment or email message.
		 * @param visibility
		 * 			  Whether the comment should be marked private.
		 * @param ccAdvisor
		 *            Whether the advisior should be CC'ed.
		 * @param sendEmail
		 *            Whether to send email, or just leave a comment.
		 */
		public CommentJob(SearchFilter filter, String comment, String subject, Boolean visibility,
				Boolean ccAdvisor, Boolean sendEmail) {

			this.filter = filter;
			this.comment = comment;
			this.subject = subject;
			this.visibility = visibility;
			this.ccAdvisor = ccAdvisor;
			this.sendEmail = sendEmail;

			if (context.getPerson() != null) 
				personId = context.getPerson().getId();
			else
				personId = null;
			
			
			String jobTitle = "Batch Comment"	;		
			if (this.sendEmail)
				jobTitle = "Batch Email";
			metadata = jobManager.register(jobTitle,context.getPerson());

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
						throw new IllegalStateException("Unable to complete comment/email job because person no longer exists.");
					context.login(person);
				} else {
					context.turnOffAuthorization();
				}
				
				// Figure out how many submissions total we are exporting
				long[] subIds = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING);
				metadata.getProgress().total = subIds.length;
				metadata.getProgress().completed = 0;
				
				// Comment!
				if (this.sendEmail)
					metadata.setMessage("Sending emails...");
				else
					metadata.setMessage("Adding comments...");
				
				for (long subId : subIds) {

					Submission sub = subRepo.findSubmission(subId);
					
					if(sendEmail){
						VireoEmail email = emailService.createEmail();
						
						// Run the parameters
						email.addParameters(sub);
						email.setSubject(subject);
						email.setMessage(comment);
						email.applyParameterSubstitution();
						
						// Create list of recipients
						email.addTo(sub.getSubmitter());
						
						// Create list of carbon copies
						if(ccAdvisor && sub.getCommitteeContactEmail() != null)
							email.addCc(sub.getCommitteeContactEmail());
						
						if(context.getPerson()!=null)
							email.setReplyTo(context.getPerson());
						
						email.setLogOnCompletion(context.getPerson(), sub);
						emailService.sendEmail(email, true);
						
					} else {
						String entry;
						if (subject != null && subject.trim().length() > 0)
							entry = subject+": "+comment;
						else
							entry = comment;
						
						ActionLog log = sub.logAction(entry);
						if(!visibility)
							log.setPrivate(true);
						else
							log.setPrivate(false);
						
						sub.save();
						log.save();
					}
					
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
				Logger.fatal(re,"Unexpected exception while attempting to comment/email on items. Aborted, although some items may have been completed.");
				
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
