package org.tdl.vireo.state.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.services.EmailService;
import org.tdl.vireo.services.SystemEmailTemplateService;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateTransitionListener;

import play.mvc.Router;
import play.mvc.Router.ActionDefinition;

/**
 * Default implementation of the state transition listener. This implementation
 * handles sending emails during state transitions, updating file names, etc..
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DefaultTransitionListenerImpl implements StateTransitionListener {

	public static final String STUDENT_INITIAL_SUBMISSION_TEMPLATE = "SYSTEM_Initial_Submission";
	public static final String ADVISOR_INITIAL_SUBMISSION_TEMPLATE = "SYSTEM_Advisor_Review_Request";

	
	// Spring dependencies
	public EmailService emailService;
	public SystemEmailTemplateService templateService;
	public SubmissionRepository subRepo;
	
	/**
	 * Inject the email service dependency
	 * 
	 * @param emailService
	 *            Email Service
	 */
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 * Inject the system template service dependency
	 * 
	 * @param templateService
	 *            system template service
	 */
	public void setSystemEmailTemplateService(SystemEmailTemplateService templateService) {
		this.templateService = templateService;
	}

	/**
	 * Inject the submission repository dependency
	 * 
	 * @param subRepo
	 *            The submission repository
	 */
	public void setSubmissionRepository(SubmissionRepository subRepo) {
		this.subRepo = subRepo;
	}
	
	@Override
	public void transition(Submission sub, State previousState) {

		State currentState = sub.getState();

		if (previousState.isInProgress() && !currentState.isInProgress()) {
			// If the submission is no longer "inProgress" and has ready to be
			// reviewed then send emails and generate advisor hashes.
			
			// 1) Generate advisor hash			
			generateCommitteEmailHash(sub);
			sub.save();
			
			// 2) Generate template parameters
			EmailService.TemplateParameters params = new EmailService.TemplateParameters(sub);
			params.STUDENT_URL = getStudentURL(sub);
			params.ADVISOR_URL = getAdvisorURL(sub);
			if (params.SUBMISSION_ASSIGNED_TO == null)
				params.SUBMISSION_ASSIGNED_TO = "n/a";
			
			
			// 3) Send the student email
			if (sub.getSubmitter().getEmail() != null) {
				EmailTemplate template = templateService.generateSystemEmailTemplate(STUDENT_INITIAL_SUBMISSION_TEMPLATE);
				List<String> recipients = new ArrayList<String>();
				recipients.add(sub.getSubmitter().getEmail());
				emailService.sendEmail(template, params, recipients, null, null);
			}
			
			// 4) Send the advisor email
			if (sub.getCommitteeContactEmail() != null) {
				EmailTemplate template = templateService.generateSystemEmailTemplate(ADVISOR_INITIAL_SUBMISSION_TEMPLATE);
				List<String> recipients = new ArrayList<String>();
				recipients.add(sub.getCommitteeContactEmail());
				emailService.sendEmail(template, params, recipients, null, null);
			}
			
		}
	}
	
	/**
	 * Generate security hash for this submission. This method will make sure
	 * that it is not being used by another submission before assigning a hash.
	 * 
	 * @param sub
	 *            The submission to assign the hash too.
	 * @return The new hash.
	 */
	protected String generateCommitteEmailHash(Submission sub) {

		String hash = null;

		do {		
			byte[] randomBytes = new byte[8];
			new Random().nextBytes(randomBytes);
			String proposed = Base64.encodeBase64URLSafeString(randomBytes);
			proposed = proposed.replaceAll("[^A-Za-z0-9]","");
						
			// Check if the hash allready exists
			if (subRepo.findSubmissionByEmailHash(proposed) == null) {
				// We're done, otherwise keep looping.
				hash = proposed;
			}
		} while (hash == null);

		sub.setCommitteeEmailHash(hash);
		return hash;
	}
	
	/**
	 * Retrieve the url where students may review their submission.
	 * 
	 * @param sub
	 *            The submission
	 * @return The url
	 */
	protected String getStudentURL(Submission sub) {
		
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
	protected String getAdvisorURL(Submission sub) {
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("token", sub.getCommitteeEmailHash());
		
		ActionDefinition advisorAction = Router.reverse("Advisor.review",routeArgs);
		advisorAction.absolute();
		
		
		return advisorAction.url;
	}
}
