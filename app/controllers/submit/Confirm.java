package controllers.submit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.tdl.vireo.constant.AppConfig.*;

import org.apache.commons.codec.binary.Base64;
import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.SystemEmailTemplateService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;

import play.Logger;
import play.modules.spring.Spring;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import controllers.Security;

/**
 * This is the fifth, and last step, of the submission process. We allow
 * students to review all their information and make one final confirmation
 * before completing the submission.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author <a href="bill-ingram.com">Bill Ingram</a>
 * @author Dan Galewsky
 */
public class Confirm extends AbstractSubmitStep {

	public static final String STUDENT_INITIAL_SUBMISSION_TEMPLATE = "SYSTEM_Initial_Submission";
	public static final String ADVISOR_INITIAL_SUBMISSION_TEMPLATE = "SYSTEM_Advisor_Review_Request";
	
	public static EmailService emailService = Spring.getBeanOfType(EmailService.class);
	public static SystemEmailTemplateService templateService = Spring.getBeanOfType(SystemEmailTemplateService.class);

	
	/**
	 * Confirm the submission has passed the verification from all the previous
	 * steps. Then allow the user to review their information before confirming
	 * the submission.
	 * 
	 * @param subId
	 *            The submission's id.
	 */
	@Security(RoleType.STUDENT)
	public static void confirm(Long subId) {		

		// Locate the submission 
		Submission sub = getSubmission();
		Person submitter = context.getPerson();

		if (!PersonalInfo.verify(sub))
			validation.addError("personalInfo", "There are errors on this page to correct.");

		if (!License.verify(sub))
			validation.addError("license","There are errors on this page to correct.");

		if (!DocumentInfo.verify(sub))
			validation.addError("documentInfo", "There are errors on this page to correct.");

		if (!FileUpload.verify(sub))
			validation.addError("fileUpload", "There are errors on this page to correct.");

		flash.put("from-step","confirm");


		if (params.get("submit_confirm") != null && !validation.hasErrors()) {

			try {
				context.turnOffAuthorization();
				
				// Generate an committee hash
				generateCommitteEmailHash(sub);
				
				// Clear the approval dates
				sub.setCommitteeApprovalDate(null);
				sub.setCommitteeEmbargoApprovalDate(null);
				
				// Set the submission date
				sub.setSubmissionDate(new Date());
					
				// Transition to the next state
				State nextState = sub.getState().getTransitions(sub).get(0);
				sub.setState(nextState);
				
				// Generate the emails
				VireoEmail studentEmail = generateStudentEmail(sub);
				VireoEmail advisorEmail = generateAdvisorEmail(sub);
				
				sub.save();

				Logger.info("%s (%d: %s) has completed and confirmed submission #%d.",
						submitter.getFormattedName(NameFormat.FIRST_LAST), 
						submitter.getId(), 
						submitter.getEmail(),
						sub.getId());
				
				// After we have saved our state do we kick off the emails
				emailService.sendEmail(studentEmail, false);
				if (advisorEmail != null)
					emailService.sendEmail(advisorEmail, false);
			} finally {
				context.restoreAuthorization();

			}

			complete(subId);
		}


		List<ActionLog> logs = subRepo.findActionLog(sub);
		Attachment primaryDocument = sub.getPrimaryDocument();
		List<Attachment> additionalDocuments = sub.getAttachmentsByType(AttachmentType.SUPPLEMENTAL,AttachmentType.SOURCE,AttachmentType.ADMINISTRATIVE);
		String grantor = settingRepo.getConfigValue(GRANTOR,"Unknown Institution");

		boolean showEditLinks = true;

		renderTemplate("Submit/confirm.html",subId, sub, grantor, showEditLinks, submitter, logs, 
				primaryDocument,
				additionalDocuments);		
	}
	
	/**
	 * After completing a submission, show a set of instructions on what the student should do next.
	 * 
	 * @param subid The id of the completed submission.
	 */
	public static void complete(Long subId) {
		// Get the post submission instructions for display
		String instructions = settingRepo.getConfigValue(SUBMIT_INSTRUCTIONS);
		instructions = text2html(instructions);

		renderTemplate("Submit/complete.html", instructions);
	}

	/**
	 * Generate security hash for this submission. This method will make sure
	 * that it is not being used by another submission before assigning a hash.
	 * 
	 * @param sub
	 *            The submission to assign the hash too.
	 * @return The new hash.
	 */
	protected static String generateCommitteEmailHash(Submission sub) {

		String hash = null;

		do {		
			byte[] randomBytes = new byte[8];
			new Random().nextBytes(randomBytes);
			String proposed = Base64.encodeBase64URLSafeString(randomBytes);
			proposed = proposed.replaceAll("[^A-Za-z0-9]","");
						
			// Check if the hash already exists
			if (subRepo.findSubmissionByEmailHash(proposed) == null) {
				// We're done, otherwise keep looping.
				hash = proposed;
			}
		} while (hash == null);

		sub.setCommitteeEmailHash(hash);
		return hash;
	}
	
	/**
	 * Generate an email to the student using the default template.
	 * 
	 * @param sub The submission to generate an email about.
	 * @return A vireo email, or null if no email address exists.
	 */
	protected static VireoEmail generateStudentEmail(Submission sub) {
		templateService.generateAllSystemEmailTemplates();

		VireoEmail email = null;
		if (sub.getSubmitter().getEmail() != null) {
			EmailTemplate template = templateService.generateSystemEmailTemplate(STUDENT_INITIAL_SUBMISSION_TEMPLATE);

			email = emailService.createEmail();
			email.setTemplate(template);
			email.addParameters(sub);
			email.addParameter("STUDENT_URL",getStudentURL(sub));
			
			email.addTo(sub.getSubmitter());
			
			email.setLogOnCompletion(null, sub);
			email.setSuccessLogMessage("Student confirmation sent to "+sub.getSubmitter().getEmail());
			email.setFailureLogMessage("Failed to send student confirmation, "+sub.getSubmitter().getEmail());
		}
		
		return email;
	}
	
	/**
	 * Generate an email to the advisor using the default template.
	 * 
	 * @param sub The submission to generate an email about.
	 * @return A viero email, or null if no advisor email address exists.
	 */
	protected static VireoEmail generateAdvisorEmail(Submission sub) {
		VireoEmail email = null;
		if (sub.getCommitteeContactEmail() != null) {
			EmailTemplate template = templateService.generateSystemEmailTemplate(ADVISOR_INITIAL_SUBMISSION_TEMPLATE);

			email = emailService.createEmail();
			email.getTo().clear();
			email.getCc().clear();
			email.getBcc().clear();
			
			
			email.setTemplate(template);
			email.addParameters(sub);
			email.addParameter("ADVISOR_URL",getAdvisorURL(sub));
			
			email.addTo(sub.getCommitteeContactEmail());
			
			email.setLogOnCompletion(null, sub);
			email.setSuccessLogMessage("Advisor review request sent to "+sub.getCommitteeContactEmail());
			email.setFailureLogMessage("Failed to send advisor review request, "+sub.getCommitteeContactEmail());

		}
		
		return email;
	}
	
	/**
	 * Retrieve the url where students may review their submission.
	 * 
	 * @param sub
	 *            The submission
	 * @return The url
	 */
	protected static String getStudentURL(Submission sub) {
		
		ActionDefinition studentAction = Router.reverse("Student.submissionList");
		studentAction.absolute();
		
		return studentAction.url;
	}
	
	/**
	 * Retrieve the url where advisors may approve the submission.
	 * 
	 * @param sub
	 *            The submission.
	 * @return the url
	 */
	protected static String getAdvisorURL(Submission sub) {
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("token", sub.getCommitteeEmailHash());
		
		ActionDefinition advisorAction = Router.reverse("Advisor.review",routeArgs);
		advisorAction.absolute();
		
		
		return advisorAction.url;
	}
	
}
