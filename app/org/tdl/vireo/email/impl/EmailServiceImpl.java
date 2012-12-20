package org.tdl.vireo.email.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import play.jobs.Job;
import play.libs.Mail;
import play.modules.spring.Spring;

/**
 * Implementation of the email service. This implementation uses the built in play
 * framework facilities for sending emails in a background thread.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class EmailServiceImpl implements EmailService {

	// Spring dependency
	public SecurityContext context;

	// List of jobs in the queue.
	public static Set<EmailJob> jobQueue = Collections.synchronizedSet(new HashSet<EmailJob>()); 

	
	/**
	 * Inject spring security context dependency.
	 * 
	 * @param context
	 *            The security context
	 */
	public void setSecurityContext(SecurityContext context) {
		this.context = context;
	}

	@Override
	public VireoEmail createEmail() {
		return Spring.getBeanOfType(VireoEmail.class);
	}
	

	@Override
	public void sendEmail(VireoEmail email, boolean wait) {

		EmailJob job = new EmailJob(email,context,wait);

		if (wait) {
			// Send the email while we wait.
			job.doJob();
		} else {
			// Otherwise, schedule it for immediate execution.
			job.now();
		}
	}
	
	@Override
	public boolean isJobRunning() {
		return !jobQueue.isEmpty();
	}




	/**
	 * Internal class to handle the sending of an email within a thread.
	 */
	public static class EmailJob extends Job {

		public VireoEmail email;
		public SecurityContext context;
		public boolean wait;

		/**
		 * Construct a new EmailJob
		 * 
		 * @param email
		 *            The email to send in a background thread.
		 * @param context
		 *            the security context
		 * @param wait
		 *            weather this job is being executed in a background thread
		 *            or not. If in the current thread then errors should be
		 *            re-thrown.
		 */
		public EmailJob(VireoEmail email, SecurityContext context, boolean wait) {
			this.email = email;
			this.context = context;
			this.wait = wait;
			
			jobQueue.add(this);
		}

		/**
		 * Send the email.
		 */
		public void doJob() {
			try {

				// Make sure any templates have been applied.
				this.email.applyParameterSubstitution();

				Email email = new SimpleEmail();

				// Add all Primary Recipients
				if (this.email.getTo().size() > 0)
					email.setTo(this.email.getTo());

				// Add all Carbon Copies
				if (this.email.getCc().size() > 0)
					email.setCc(this.email.getCc());

				// Add all Blind Carbon Copies
				if (this.email.getBcc().size() > 0)
					email.setBcc(this.email.getBcc());

				// Who's sending this
				if (this.email.getFrom() != null)
					email.setFrom(
							this.email.getFrom().getAddress(),
							this.email.getFrom().getPersonal());

				// Where to send replys
				if (this.email.getReplyTo() != null)
					email.addReplyTo(
							this.email.getReplyTo().getAddress(),
							this.email.getReplyTo().getPersonal()
							);

				// Subject and Message
				email.setSubject(this.email.getSubject());
				email.setMsg(this.email.getMessage());


				// Send the email
				email = Mail.buildMessage(email);
				if (Play.configuration.getProperty("mail.smtp", "").equals("mock") && Play.mode == Play.Mode.DEV) {
					// Send using the mock server.
					Mail.send(email).get();
				} else {

					// Otherwise send it for real
					email.setMailSession(Mail.getSession());
					email.setSentDate(new Date());
					email.send();
				}

				// Check if we should log a message
				logMessage(this.email.getSuccessLogMessage());

			} catch (Throwable t) {
				Logger.error(t,"Unable to send email because of error.");
				
				String logError = this.email.getFailureLogMessage(t.getMessage());
				logMessage(logError);
				
				ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);
				errorLog.logError(t, "Sending email");
				
				if (wait)
					throw new RuntimeException(t);
			} finally {
				jobQueue.remove(this);
			}
		}


		/**
		 * If we're set up to log successes or failures back to a submission,
		 * then log the provided message. This handles the authorizations of
		 * who's doing what.
		 * 
		 * @param logMessage
		 *            The message to log.
		 */
		public void logMessage(String logMessage) {

			synchronized (EmailService.class) {
				
				// If we are in a background thread isolate the transaction as much
				// as possible to prevent deadlocks.
				if (!wait) {
					if (JPA.isInsideTransaction())
						JPAPlugin.closeTx(false);
					JPAPlugin.startTx(false);
				}
				
				Submission sub = this.email.getLogSubmission();
				if (sub != null) {
					Person person = this.email.getLogPerson();
					boolean loggedSomeoneIn = false;
					if (context.getPerson() == null && person != null) {
						context.login(person);
						loggedSomeoneIn = true;
					}
					context.turnOffAuthorization();
	
					try {
						ActionLog log = sub.logAction(logMessage);
						log.save();
						sub.save();
					} finally {
	
						context.restoreAuthorization();
						if (loggedSomeoneIn)
							context.logout();
					} // finally
				} // if sub
				
				if (!wait) {
					JPAPlugin.closeTx(false);
				}
				
			} // synchronized EmailService
		} // logMessage
	} // emailJob

}
