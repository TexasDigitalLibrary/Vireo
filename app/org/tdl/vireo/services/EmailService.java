package org.tdl.vireo.services;

import java.text.DateFormatSymbols;
import java.util.List;

import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Submission;

/**
 * The email service managing the sending of emails reducing the complexity of
 * other components. It expects that there is an email template which contains
 * some variable substitutions. Those variables are replaced and the email is
 * sent. Note that most implementation will probably implement sending of an
 * email using a background thread. So if there were to be an exception
 * generated during the sending of an email it would not be received from this
 * call. Instead the current thread will return happily.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author Joe DeVries
 * @author Micah Cooper
 */
public interface EmailService {

	/**
	 * Send an email template to a set of recipients.
	 * 
	 * @param template
	 *            The template to send.
	 * @param params
	 *            Parameters for the template's variable substitution.
	 * @param recipients
	 *            A list of recipients to attach to the email.
	 * @param replyTo
	 *            The preferred reply to address (may be null, in this case the
	 *            default mail.replyto will be used).
	 */
	public void sendEmail(EmailTemplate template, TemplateParameters params, List<String> recipients, String replyTo, List<String> carbonCopies);
	
	/**
	 * Send an email template to a set of recipients.
	 * 
	 * @param subject
	 *            The subject of the email.
	 * @param message
	 * 			  The message of the email.
	 * @param params
	 *            Parameters for the template's variable substitution.
	 * @param recipients
	 *            A list of recipients to attach to the email.
	 * @param replyTo
	 *            The preferred reply to address (may be null, in this case the
	 *            default mail.replyto will be used).
	 */
	public void sendEmail(String subject, String message, TemplateParameters params, List<String> recipients, String replyTo, List<String> carbonCopies);
	
	/**
	 * Manage a list of variable substitutions that may be used.
	 */
	public static class TemplateParameters {

		// The student's full, official name.
		public String FULL_NAME;
		
		// The student's last name.
		public String FIRST_NAME;
		
		// The student's first name.
		public String LAST_NAME;
		
		// The title of the thesis or dissertation as supplied by the student.
		public String DOCUMENT_TITLE;
		
		// The document type, typically 'Thesis' or 'Dissertation'. This is defined by your office.
		public String DOCUMENT_TYPE;
		
		// The semester in which the student indicated they will graduate.
		public String GRAD_SEMESTER;
		
		// A URL that will allow the student to view or take action on their submission.
		public String STUDENT_URL;
		
		// The URL sent to the student's advisor to request manuscript approval.
		public String ADVISOR_URL;
		
		// The URL sent to complete registration/forgot password.
		public String REGISTRATION_URL;
		
		// The current status of the submission; e.g. 'Approved', 'Needs Corrections', etc.
		public String SUBMISSION_STATUS;
		
		// The name of the staff member to which this submission is currently assigned.
		public String SUBMISSION_ASSIGNED_TO;
		
		/**
		 * Create a blank template parameters. The caller will need to set the
		 * member parameters manually.
		 */
		public TemplateParameters() {
			
		}
		
		/**
		 * Create a template parameter set filling out most of the variables
		 * using information obtained from the provided submission object.
		 * 
		 * @param submission
		 *            The submission object.
		 */
		public TemplateParameters(Submission submission) {
			FULL_NAME = submission.getStudentFirstName() + " " + submission.getStudentLastName();
			FIRST_NAME = submission.getStudentFirstName();
			LAST_NAME = submission.getStudentLastName();
			
			DOCUMENT_TYPE = submission.getDocumentType();
			SUBMISSION_STATUS = submission.getState().getDisplayName();
			
			if (submission.getPrimaryDocument() != null)
				DOCUMENT_TITLE = submission.getPrimaryDocument().getName();
			
			if (submission.getGraduationMonth() != null) {
				Integer monthInt = submission.getGraduationMonth();
				String monthName = new DateFormatSymbols().getMonths()[monthInt];
				GRAD_SEMESTER = monthName + ", "+ submission.getGraduationYear();
			}

			if (submission.getAssignee() != null)
				SUBMISSION_ASSIGNED_TO = submission.getAssignee().getFormattedName(NameFormat.FIRST_LAST);
		}
		
	}
	
}
