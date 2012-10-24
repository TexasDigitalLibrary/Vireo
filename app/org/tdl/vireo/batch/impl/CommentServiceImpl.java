package org.tdl.vireo.batch.impl;

import java.util.Iterator;

import org.tdl.vireo.batch.CommentService;
import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Submission;
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

	public static EmailService emailService = Spring.getBeanOfType(EmailService.class);
	
	// The repository of people
	public PersonRepository personRepo;

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
	public JobMetadata comment(SearchFilter filter, String comment,
			String subject, Boolean visibility, Boolean sendEmail, Boolean ccAdvisor) {
		if (filter == null)
			throw new IllegalArgumentException("A search filter is required");

		if (context.isAuthorizationActive() && !context.isReviewer())
			throw new SecurityException("Unauthorized to transition submissions.");

		CommentJob job = new CommentJob(filter, comment, subject, visibility, ccAdvisor, sendEmail);

		job.now();

		return job.metadata;
	}

	/**
	 * The background job to change assignee on submissions.
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
			
			metadata = jobManager.register("Batch Comment/Email",context.getPerson());
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
				SearchResult<Submission> results = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 1);
				metadata.getProgress().total = results.getTotal();
				metadata.getProgress().completed = 0;
				if (results.getResults().size() > 0)
					results.getResults().get(0).detach();
				
				// Delete!
				metadata.setMessage("Adding comment/emailing on submissions...");
				Iterator<Submission> itr = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING);
				
				while(itr.hasNext()) {

					Submission sub = itr.next();
					
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
					JPA.em().getTransaction().begin();
					
					// Don't let memory get out of control
					System.gc();
					metadata.getProgress().completed++;
				}
				
				metadata.setMessage(null);
				metadata.setStatus(JobStatus.SUCCESS);
				
			} catch (RuntimeException re) {
				Logger.fatal(re,"Unexpected exception while attempting to comment/email on items. Aborted, although some items may have been completed.");
				
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
