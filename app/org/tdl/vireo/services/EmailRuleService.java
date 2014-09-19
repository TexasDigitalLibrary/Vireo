package org.tdl.vireo.services;

import java.util.List;

import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.WorkflowEmailRule;
import org.tdl.vireo.state.State;

import play.modules.spring.Spring;

/**
 * 
 * @author gad
 *
 */
public class EmailRuleService {

	/**
	 * This static class will take a submission and go through all of the JPA email rules
	 * 
	 * to decide if an email needs to be sent (and to whom) depending on the rule's condition and recipients
	 * 
	 * @param submission - the submission
	 */
	public static void runEmailRules(Submission submission) {

		// Get all the rules
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		List<WorkflowEmailRule> rules = settingRepo.findAllWorkflowEmailRules();
		// for every rule in JPA
		for (WorkflowEmailRule workflowEmailRule : rules) {
			// if the rule's state matches the transitioned state in the submission
			if (workflowEmailRule.getAssociatedState().equals(submission.getState().getBeanName())) {
				// apply condition to send only when condition is met
				switch (workflowEmailRule.getCondition().getConditionType()) {
				case Always:
					// send the email
					sendEmail(workflowEmailRule, submission);
					break;
				case College:
					// get the collegeId out of the rule
					Long collegeId = workflowEmailRule.getCondition().getConditionId();
					// get the college out of JPA
					College ruleCollege = settingRepo.findCollege(collegeId);
					// compare the rule's college with the submission's college
					if (ruleCollege.getName().equals(submission.getCollege())) {
						// send the email
						sendEmail(workflowEmailRule, submission);
					}
					break;
				case Department:
					// get the departmentId out of the rule
					Long departmentId = workflowEmailRule.getCondition().getConditionId();
					// get the department out of JPA
					Department ruleDepartment = settingRepo.findDepartment(departmentId);
					// compare the rule's department with the submission's department
					if (ruleDepartment.getName().equals(submission.getDepartment())) {
						// send the email
						sendEmail(workflowEmailRule, submission);
					}
					break;
				case Program:
					// get the programId out of the rule
					Long programId = workflowEmailRule.getCondition().getConditionId();
					// get the program out of JPA
					Program ruleProgram = settingRepo.findProgram(programId);
					// compare the rule's program with the submission's program
					if (ruleProgram.getName().equals(submission.getProgram())) {
						// send the email
						sendEmail(workflowEmailRule, submission);
					}
					break;
				default:
					// what are we doing here?!
					throw new UnsupportedOperationException();
				}
			}
		}
	}

	/**
	 * This will generate and send the email to the right recipients from the rule
	 * 
	 * @param workflowEmailRule - the workflow email rule
	 * @param submission - the submission
	 */
	private static void sendEmail(WorkflowEmailRule workflowEmailRule, Submission submission) {
		// get the EmailService from Spring
		EmailService emailService = Spring.getBeanOfType(EmailService.class);
		// generate the email with the right template and recipients
		VireoEmail email = createEmail(emailService, workflowEmailRule, submission);
		// send the email NOW (not as a Job)
		emailService.sendEmail(email, false);
	}

	/**
	 * This will generate a VireoEmail from the right recipients from the rule
	 * 
	 * @param emailService - the emailService from Spring
	 * @param workflowEmailRule - the workflow email rule
	 * @param submission - the submission
	 * @return - a new VireoEmail with the correct email template and recipients
	 */
	private static VireoEmail createEmail(EmailService emailService, WorkflowEmailRule workflowEmailRule, Submission submission) {
		// get all the recipients from the rule
		List<String> recipients = workflowEmailRule.getRecipients(submission);
		// create the email
		VireoEmail vireoEmail = emailService.createEmail();
		// add all recipients to the TO of this email
		for (String email : recipients) {
			vireoEmail.addTo(email);
		}
		// set the correct email template for this rule
		vireoEmail.setTemplate(workflowEmailRule.getEmailTemplate());
		return vireoEmail;
	}

}
