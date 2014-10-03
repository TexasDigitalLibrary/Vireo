package org.tdl.vireo.services;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.email.impl.SystemEmailTemplateServiceImpl;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition.ConditionType;
import org.tdl.vireo.model.jpa.JpaEmailTemplateImpl;
import org.tdl.vireo.model.jpa.JpaEmailWorkflowRuleConditionImpl;
import org.tdl.vireo.model.jpa.JpaEmailWorkflowRuleImpl;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
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
	 * @param submission
	 *            - the submission
	 */
	public static void runEmailRules(Submission submission) {

		// Get all the rules
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		List<EmailWorkflowRule> rules = settingRepo.findAllEmailWorkflowRules();
		// for every rule in JPA
		for (EmailWorkflowRule ewflRule : rules) {
			// verify the rule is valid/complete before running it
			if (ruleIsValid(ewflRule, submission)) {
				// if the rule's state matches the transitioned state in the submission
				if (ewflRule.getAssociatedState().getBeanName().equals(submission.getState().getBeanName())) {
					// apply condition to send only when condition is met
					switch (ewflRule.getCondition().getConditionType()) {
					case Always:
						// send the email
						sendEmail(ewflRule, submission);
						break;
					case College:
						// get the collegeId out of the rule
						Long collegeId = ewflRule.getCondition().getConditionId();
						// get the college out of JPA
						College ruleCollege = settingRepo.findCollege(collegeId);
						// compare the rule's college with the submission's college
						if (ruleCollege != null && ruleCollege.getName().equals(submission.getCollege())) {
							// send the email
							sendEmail(ewflRule, submission);
						} else {
							Logger.error("This rule's college with id #%d doesn't exist! ", collegeId);
						}
						break;
					case Department:
						// get the departmentId out of the rule
						Long departmentId = ewflRule.getCondition().getConditionId();
						// get the department out of JPA
						Department ruleDepartment = settingRepo.findDepartment(departmentId);
						// compare the rule's department with the submission's department
						if (ruleDepartment != null && ruleDepartment.getName().equals(submission.getDepartment())) {
							// send the email
							sendEmail(ewflRule, submission);
						} else {
							Logger.error("This rule's department with id #%d doesn't exist! ", departmentId);
						}
						break;
					case Program:
						// get the programId out of the rule
						Long programId = ewflRule.getCondition().getConditionId();
						// get the program out of JPA
						Program ruleProgram = settingRepo.findProgram(programId);
						// compare the rule's program with the submission's program
						if (ruleProgram != null && ruleProgram.getName().equals(submission.getProgram())) {
							// send the email
							sendEmail(ewflRule, submission);
						} else {
							Logger.error("This rule's program with id #%d doesn't exist! ", programId);
						}
						break;
					default:
						// what are we doing here?!
						throw new UnsupportedOperationException();
					}
				}
			}
		}
	}

	/**
	 * Checks the validity of a rule to make sure it should run or not
	 * 
	 * @param EmailWorkflowRule
	 *            - the workflow email rule to check
	 * @param submission
	 *            - the submission to check against
	 * @return - returns true if rule is valid, false if condition is not set or recipients is emtpy
	 */
	private static boolean ruleIsValid(EmailWorkflowRule ewflRule, Submission submission) {
		boolean ret = true;
		// check condition != null
		if (ewflRule.getCondition() != null) {
			// check condition type and id != null
			if (ewflRule.getCondition().getConditionType() == null) {
				ret = false;
			}
			switch (ewflRule.getCondition().getConditionType()) {
			case College:
			case Department:
			case Program:
				if (ewflRule.getCondition().getConditionId() == null) {
					ret = false;
				}
				break;
			case Always:
				break;
			default:
				throw new UnsupportedOperationException();
			}
		} else {
			ret = false;
		}
		// check recipients
		if (ewflRule.getRecipients(submission).size() == 0) {
			ret = false;
		}
		// check to see if rule is disabled
		ret = (!ewflRule.isDisabled()); // if rule isDisabled(true) return false(rule is not valid)
		return ret;
	}

	/**
	 * This will generate and send the email to the right recipients from the rule
	 * 
	 * @param EmailWorkflowRule
	 *            - the workflow email rule
	 * @param submission
	 *            - the submission
	 */
	private static void sendEmail(EmailWorkflowRule EmailWorkflowRule, Submission submission) {
		// get the EmailService from Spring
		EmailService emailService = Spring.getBeanOfType(EmailService.class);
		// generate the email with the right template and recipients
		VireoEmail email = createEmail(emailService, EmailWorkflowRule, submission);
		// send the email NOW (not as a Job)
		emailService.sendEmail(email, true);
	}

	/**
	 * This will generate a VireoEmail from the right recipients from the rule
	 * 
	 * @param emailService
	 *            - the emailService from Spring
	 * @param EmailWorkflowRule
	 *            - the workflow email rule
	 * @param submission
	 *            - the submission
	 * @return - a new VireoEmail with the correct email template and recipients
	 */
	private static VireoEmail createEmail(EmailService emailService, EmailWorkflowRule EmailWorkflowRule, Submission submission) {
		// get all the recipients from the rule
		List<String> recipients = EmailWorkflowRule.getRecipients(submission);
		// create the email
		VireoEmail vireoEmail = emailService.createEmail();
		// add all recipients to the TO of this email
		for (String email : recipients) {
			vireoEmail.addTo(email);
		}
		// set the correct email template for this rule
		vireoEmail.setTemplate(EmailWorkflowRule.getEmailTemplate());
		// add the parameters into the email body
		vireoEmail.addParameters(submission);
		return vireoEmail;
	}

	// **********************************
	// On Application Startup we need to make sure the System-wide rules are defined
	// **********************************

	// Spring injected dependencies
	public SecurityContext context;
	public SettingsRepository settingRepo;

	/**
	 * @param settingRepo
	 *            The settings repository used for generating email templates.
	 */
	public void setSettingsRepository(SettingsRepository settingRepo) {
		this.settingRepo = settingRepo;
	}

	/**
	 * @param context
	 *            The security context.
	 */
	public void setSecurityContext(SecurityContext context) {
		this.context = context;
	}

	public void generateAllSystemEmailRules() {
		// turn off authorization if we're saving
		context.turnOffAuthorization();

		// get the Submitted state
		State submitted = Spring.getBeanOfType(StateManager.class).getState("Submitted");
		// get all of the current Submitted rules
		List<EmailWorkflowRule> submittedRules = settingRepo.findEmailWorkflowRulesByState(submitted);
		// our Always conditions
		JpaEmailWorkflowRuleConditionImpl conditionAdvisor = (JpaEmailWorkflowRuleConditionImpl) settingRepo.createEmailWorkflowRuleCondition(ConditionType.Always);
		JpaEmailWorkflowRuleConditionImpl conditionStudent = (JpaEmailWorkflowRuleConditionImpl) settingRepo.createEmailWorkflowRuleCondition(ConditionType.Always);
		// our email template to use for the default system email rules
		JpaEmailTemplateImpl emailTemplate = (JpaEmailTemplateImpl) settingRepo.findEmailTemplateByName("SYSTEM Initial Submission");
		// always send to advisor on submission
		JpaEmailWorkflowRuleImpl advisorRule = (JpaEmailWorkflowRuleImpl) settingRepo.createEmailWorkflowRule(submitted);
		advisorRule.setCondition(conditionAdvisor); // always save a new copy of condition
		advisorRule.setEmailTemplate(emailTemplate);
		advisorRule.setRecipientType(RecipientType.Advisor);
		advisorRule.setIsSystem(true);
		// always send to student on submission
		JpaEmailWorkflowRuleImpl studentRule = (JpaEmailWorkflowRuleImpl) settingRepo.createEmailWorkflowRule(submitted);
		studentRule.setCondition(conditionStudent); // always save a new copy of condition
		studentRule.setEmailTemplate(emailTemplate);
		studentRule.setRecipientType(RecipientType.Student);
		studentRule.setIsSystem(true);

		boolean foundAdvisor = false, foundStudent = false;

		for (EmailWorkflowRule rule : submittedRules) {
			// if this is a system-installed rule
			if(rule.isSystem()) {
				// compare conditions for advisor rule
				if (rule.getCondition().getConditionType() == advisorRule.getCondition().getConditionType()) {
					// compare recipients for advisor rule
					if (rule.getRecipientType() == advisorRule.getRecipientType()) {
						foundAdvisor = true;
					}
				}
				// compare conditions for student rule
				if (rule.getCondition().getConditionType() == studentRule.getCondition().getConditionType()) {
					// compare recipients for student rule
					if (rule.getRecipientType() == studentRule.getRecipientType()) {
						foundStudent = true;
					}
				}
			}
		}
		// if the rules weren't found, add them
		if (!foundAdvisor) {
			if(advisorRule.getCondition() != null) {
				advisorRule.getCondition().save();
			}
			advisorRule.save();
		}
		if (!foundStudent) {
			if(studentRule.getCondition() != null) {
				studentRule.getCondition().save();
			}
			studentRule.save();
		}

		// turn on authorization after we're done
		context.restoreAuthorization();
	}

	/**
	 * When the application starts generate all the system email workflow rules if they are not already present.
	 */
	@OnApplicationStart
	public static class initializeSystemEmailWorkflowRules extends Job {
		public void doJob() {
			try {
				EmailRuleService emailRuleService = Spring.getBeanOfType(EmailRuleService.class);
				emailRuleService.generateAllSystemEmailRules();
			} catch (RuntimeException re) {
				Logger.error(re, "Unable to initialize system email workflow rules.");
			}
		}
	}
}
