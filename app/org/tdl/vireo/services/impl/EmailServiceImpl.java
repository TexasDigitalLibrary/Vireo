package org.tdl.vireo.services.impl;

import java.util.Collections;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.services.EmailService;

import com.mchange.v1.util.SimpleMapEntry;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.libs.Mail;

/**
 * Implementation of Vireo's EmailService interface. This implementation use's
 * play's built in Email library (which is just a thin shell over Apache
 * Common's API). This allows for easy testing because play keeps a mock copy
 * around for inspection.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author Joe DeVries
 */
public class EmailServiceImpl implements EmailService {

	@Override
	public void sendEmail(EmailTemplate template, TemplateParameters params, List<String> recipients, String replyTo) {
		
		if (template == null)
			throw new IllegalArgumentException("Template is required.");
		
		if (params == null)
			throw new IllegalArgumentException("Template parameters are required.");
		
		if (recipients == null || recipients.size() == 0)
			throw new IllegalArgumentException("Recipients are required.");
		
		if (replyTo != null) {
			try {
				new InternetAddress(replyTo);
			} catch (AddressException e) {
				throw new IllegalArgumentException("Supplied replyTo email address is invalid: "+replyTo);
			}
		}
		
		for (String recipient : recipients) {
			try {
				new InternetAddress(recipient);
			} catch (AddressException e) {
				throw new IllegalArgumentException("Supplied recipent email address is invalid: "+recipient);
			}
		}

		
		
		String subject = template.getSubject();
		String message = template.getMessage();

		if (params.FULL_NAME != null) {
			subject = subject.replaceAll("\\{FULL_NAME\\}",params.FULL_NAME);
			message = message.replaceAll("\\{FULL_NAME\\}",params.FULL_NAME);
		}
		
		if (params.FIRST_NAME != null) {
			subject = subject.replaceAll("\\{FIRST_NAME\\}",params.FIRST_NAME);
			message = message.replaceAll("\\{FIRST_NAME\\}",params.FIRST_NAME);
		}
		
		if (params.LAST_NAME != null) {
			subject = subject.replaceAll("\\{LAST_NAME\\}",params.LAST_NAME);
			message = message.replaceAll("\\{LAST_NAME\\}",params.LAST_NAME);
		}
			
		if (params.DOCUMENT_TITLE != null) {
			subject = subject.replaceAll("\\{DOCUMENT_TITLE\\}",params.DOCUMENT_TITLE);
			message = message.replaceAll("\\{DOCUMENT_TITLE\\}",params.DOCUMENT_TITLE);
		}
			
		if (params.DOCUMENT_TYPE != null) {
			subject = subject.replaceAll("\\{DOCUMENT_TYPE\\}",params.DOCUMENT_TYPE);
			message = message.replaceAll("\\{DOCUMENT_TYPE\\}",params.DOCUMENT_TYPE);
		}
			
		if (params.GRAD_SEMESTER != null) {
			subject = subject.replaceAll("\\{GRAD_SEMESTER\\}",params.GRAD_SEMESTER);
			message = message.replaceAll("\\{GRAD_SEMESTER\\}",params.GRAD_SEMESTER);
		}
			
		if (params.STUDENT_URL != null) {
			subject = subject.replaceAll("\\{STUDENT_URL\\}",params.STUDENT_URL);
			message = message.replaceAll("\\{STUDENT_URL\\}",params.STUDENT_URL);
		}
			
		if (params.ADVISOR_URL != null) {
			subject = subject.replaceAll("\\{ADVISOR_URL\\}",params.ADVISOR_URL);
			message = message.replaceAll("\\{ADVISOR_URL\\}",params.ADVISOR_URL);
		}
			
		if (params.REGISTRATION_URL != null) {
			subject = subject.replaceAll("\\{REGISTRATION_URL\\}",params.REGISTRATION_URL);
			message = message.replaceAll("\\{REGISTRATION_URL\\}",params.REGISTRATION_URL);
		}
			
		if (params.SUBMISSION_STATUS != null) {
			subject = subject.replaceAll("\\{SUBMISSION_STATUS\\}",params.SUBMISSION_STATUS);
			message = message.replaceAll("\\{SUBMISSION_STATUS\\}",params.SUBMISSION_STATUS);
		}
			
		if (params.SUBMISSION_ASSIGNED_TO != null) {
			subject = subject.replaceAll("\\{SUBMISSION_ASSIGNED_TO\\}",params.SUBMISSION_ASSIGNED_TO);
			message = message.replaceAll("\\{SUBMISSION_ASSIGNED_TO\\}",params.SUBMISSION_ASSIGNED_TO);
		}
			
		EmailJob job = new EmailJob(recipients,replyTo,subject,message);
		job.now();

	}
	
	/**
	 * Internal class to handle the sending of an email within a thread.
	 */
	public static class EmailJob extends Job {
		
		// Email parameters
		public String subject;
		public String message;
		public List<String> recipients;
		public String replyto;
		
		/**
		 * Construct a new EmailJob
		 * 
		 * @param recipients
		 *            The recipients of this email.
		 * @param replyto
		 *            Who the email should be from (may be null)
		 * @param subject
		 *            The email's subject.
		 * @param message
		 *            The complete email message with all variables replaced.
		 */
		public EmailJob(List<String> recipients, String replyto, String subject, String message) {
			this.subject = subject;
			this.message = message;
			this.recipients = recipients;
			this.replyto = replyto;
		}
		
		/**
		 * Send the email.
		 */
		public void doJob() {
			try {
			
				SimpleEmail email = new SimpleEmail();
				
				email.setFrom(Play.configuration.getProperty("mail.from"));
				if (replyto == null) {
					email.addReplyTo(Play.configuration.getProperty("mail.replyto"));
				} else {
					email.addReplyTo(replyto);	
				}
				for (String recipient : recipients) {
					email.addTo(recipient);
				}
				email.setSubject(subject);
				email.setMsg(message);
				
				
				Mail.send(email).get();
			} catch (Exception e) {
				Logger.error(e,"Unable to send email because of error.");
				throw new RuntimeException(e);
			}
		}
	}
}
