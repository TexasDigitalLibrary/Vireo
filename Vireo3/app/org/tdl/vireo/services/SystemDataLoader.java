/**
 * 
 */
package org.tdl.vireo.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.spring.Spring;

/**
 * @author gad
 *
 */
public class SystemDataLoader {
	// **********************************
	// On Application Startup we need to make sure the System-wide rules are defined
	// **********************************

	// Spring injected dependencies
	private SecurityContext context;
	private SettingsRepository settingRepo;

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
		AbstractWorkflowRuleCondition conditionAdvisor = settingRepo.createEmailWorkflowRuleCondition(ConditionType.Always);
		AbstractWorkflowRuleCondition conditionStudent = settingRepo.createEmailWorkflowRuleCondition(ConditionType.Always);
		// our email templates to use for the default system email rules
		EmailTemplate advisorEmailTemplate = settingRepo.findEmailTemplateByName("SYSTEM Advisor Review Request");
		EmailTemplate studentEmailTemplate = settingRepo.findEmailTemplateByName("SYSTEM Initial Submission");
		// always send to advisor on submission
		EmailWorkflowRule advisorRule = settingRepo.createEmailWorkflowRule(submitted);
		advisorRule.setCondition(conditionAdvisor); // always save a new copy of condition
		advisorRule.setEmailTemplate(advisorEmailTemplate);
		advisorRule.setRecipientType(RecipientType.Advisor);
		advisorRule.setIsSystem(true);
		// always send to student on submission
		EmailWorkflowRule studentRule = settingRepo.createEmailWorkflowRule(submitted);
		studentRule.setCondition(conditionStudent); // always save a new copy of condition
		studentRule.setEmailTemplate(studentEmailTemplate);
		studentRule.setRecipientType(RecipientType.Student);
		studentRule.setIsSystem(true);

		boolean foundAdvisor = false, foundStudent = false;

		for (EmailWorkflowRule rule : submittedRules) {
			// if this is a system-installed rule
			if (rule.isSystem()) {
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
			if (advisorRule.getCondition() != null) {
				advisorRule.getCondition().save();
			}
			advisorRule.enable();
			advisorRule.save();
		}
		if (!foundStudent) {
			if (studentRule.getCondition() != null) {
				studentRule.getCondition().save();
			}
			studentRule.enable();
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
				SystemDataLoader systemDataLoader = Spring.getBeanOfType(SystemDataLoader.class);
				Logger.info("initializeSystemEmailWorkflowRules running...");
				systemDataLoader.generateAllSystemEmailRules();
				Logger.info("initializeSystemEmailWorkflowRules done!");
			} catch (RuntimeException re) {
				Logger.error(re, "Unable to initialize system email workflow rules.");
			}
		}
	}

	// Constants
	public static final String BASE_PATH = Play.applicationPath + File.separator + "conf" + File.separator + "emails" + File.separator;
	public static final Pattern SUBJECT_PATTERN = Pattern.compile("\\s*Subject:(.*)[\\n\\r]{1}");

	public EmailTemplate loadSystemEmailTemplate(String name) {
		try {
			String templatePath = BASE_PATH + encodeTemplateName(name);
			File templateFile = new File(templatePath);

			String data = FileUtils.readFileToString(templateFile);

			// Remove any comment lines
			data = data.replaceAll("\\s*#.*[\\n\\r]{1}", "");

			// Extract the subject
			Matcher subjectMatcher = SUBJECT_PATTERN.matcher(data);
			if (!subjectMatcher.find())
				throw new IllegalStateException("Unable to identify the template's subject.");
			String subject = subjectMatcher.group(1).trim();

			// Trim the subject leaving just the body.
			int index = data.indexOf("\n");
			if (index < 0)
				index = data.indexOf("\r");
			String message = data.substring(index);

			if (subject == null || subject.length() == 0)
				throw new IllegalStateException("Unable to identify the template's subject.");

			if (message == null || message.length() == 0)
				throw new IllegalStateException("Unable to identify the template's message.");

			EmailTemplate template = settingRepo.createEmailTemplate(name, subject, message);
			template.setSystemRequired(true);

			return template;

		} catch (Exception e) {
			throw new IllegalStateException("Unable to generate system email template: " + name, e);
		}

	}

	public void generateAllSystemEmailTemplates() {
		// turn off authorization if we're saving
		context.turnOffAuthorization();
		for (String name : getAllSystemEmailTemplateNames()) {

			// try to see if it already exists in the DB
			EmailTemplate dbTemplate = settingRepo.findSystemEmailTemplateByName(name);

			// create template or upgrade the old one
			if (dbTemplate == null) {
				dbTemplate = loadSystemEmailTemplate(name);
				Logger.info("New System Email template being installed [%s]", dbTemplate.getName());
				dbTemplate.save();
			} else {
				EmailTemplate loadedTemplate = loadSystemEmailTemplate(name);
				// if the template in the DB doesn't match in content with the one loaded from .email file
				if (!(dbTemplate.getMessage().equals(loadedTemplate.getMessage())) || !(dbTemplate.getSubject().equals(loadedTemplate.getSubject()))) {
					EmailTemplate possibleCustomTemplate = settingRepo.findNonSystemEmailTemplateByName(name);
					// if this System template already has a custom template (meaning one named the same but that is !isSystemRequired)
					if (possibleCustomTemplate != null) {
						// a custom version of this System email template already exists, it's safe to override dbTemplate's data and save
						dbTemplate.setSystemRequired(false);
						dbTemplate.setMessage(loadedTemplate.getMessage());
						dbTemplate.setSubject(loadedTemplate.getSubject());
						dbTemplate.setSystemRequired(true);
						Logger.info("Upgrading Old System Email Template for [%s]", dbTemplate.getName());
						dbTemplate.save();
					}
					// there is no custom one yet, we need to make the dbTemplate !isSystemRequired and the save loadedTemplate
					else {
						Logger.info("Upgrading Old System Email Template and creating custom version for [%s]", dbTemplate.getName());
						dbTemplate.setSystemRequired(false);
						dbTemplate.save();
						loadedTemplate.save();
					}
				}
			}
		}
		// turn on authorization after we're done
		context.restoreAuthorization();
	}

	public List<String> getAllSystemEmailTemplateNames() {

		File directory = new File(BASE_PATH);

		List<String> names = new ArrayList<String>();
		for (File file : directory.listFiles()) {

			if (file.isFile()) {
				String fileName = file.getName();
				String templateName = decodeTemplateName(fileName);
				names.add(templateName);
			}
		}

		return names;
	}

	/**
	 * Encode a template name to a file name on disk.
	 * 
	 * @param name
	 *            The template name.
	 * @return The file path.
	 */
	private static String encodeTemplateName(String name) {

		return name.replaceAll(" ", "_") + ".email";
	}

	/**
	 * Decode a template file name into a template name.
	 * 
	 * @param path
	 *            The file name.
	 * @return The template name.
	 */
	private String decodeTemplateName(String path) {

		if (path.endsWith(".email"))
			path = path.substring(0, path.length() - ".email".length());

		return path.replaceAll("_", " ");
	}

	/**
	 * When the application starts generate all the system email templates if they are not already present.
	 */
	@OnApplicationStart
	public static class initializeSystemEmailTemplates extends Job {
		public void doJob() {
			try {
				SystemDataLoader systemDataLoader = Spring.getBeanOfType(SystemDataLoader.class);
				Logger.info("initializeSystemEmailTemplates running...");
				systemDataLoader.generateAllSystemEmailTemplates();
				Logger.info("initializeSystemEmailTemplates done!");
			} catch (RuntimeException re) {
				Logger.error(re, "Unable to initialize system email templates.");
			}
		}
	}

	/**
	 * Initial Embargo Types to create
	 */

	private static final EmbargoArray[] EMBARGO_DEFINTITIONS = { 
		new EmbargoArray("None", "The work will be published after approval.", 0, true, true, EmbargoGuarantor.DEFAULT),
		new EmbargoArray("Journal Hold", "The work will be delayed for publication by one year because of a restriction from publication in an academic journal.", 12, true, true, EmbargoGuarantor.DEFAULT),
		new EmbargoArray("Patent Hold", "The work will be delayed for publication by two years because of patent related activities.", 24, true, true, EmbargoGuarantor.DEFAULT),
		new EmbargoArray("Other Embargo Period", "The work will be delayed for publication by an indefinite amount of time.", null, false, true, EmbargoGuarantor.DEFAULT),
		new EmbargoArray("None", "The work will be published after approval.", 0, true, true, EmbargoGuarantor.PROQUEST),
		new EmbargoArray("6-month Journal Hold", "The full text of this work will be held/restricted from worldwide access on the internet for six months from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication.", 6, true, true, EmbargoGuarantor.PROQUEST),
		new EmbargoArray("1-year Journal Hold", "The full text of this work will be held/restricted from worldwide access on the internet for one year from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication.", 12, true, true, EmbargoGuarantor.PROQUEST),
		new EmbargoArray("2-year Journal Hold", "The full text of this work will be held/restricted from worldwide access on the internet for two years from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication.", 24, true, true, EmbargoGuarantor.PROQUEST),
		new EmbargoArray("Flexible/Delayed Release Embargo Period", "The work will be delayed for publication by an indefinite amount of time.", null, false, true, EmbargoGuarantor.PROQUEST)
		};

	private static class EmbargoArray {

		String name;
		String description;
		Integer duration;
		boolean active;
		boolean isSystem;
		EmbargoGuarantor guarantor;

		EmbargoArray(String name, String description, Integer duration, boolean active, boolean isSystem, EmbargoGuarantor guarantor) {
			this.name = name;
			this.description = description;
			this.duration = duration;
			this.active = active;
			this.isSystem = isSystem;
			this.guarantor = guarantor;
		}
	}

	public void generateAllSystemEmbargos() {
		try {
			// turn off authorization if we're saving
			context.turnOffAuthorization();

			// Setup Embargos
			// for every System Defined Embargo
			for (EmbargoArray embargoDefinition : EMBARGO_DEFINTITIONS) {
				EmbargoType dbEmbargo = settingRepo.findSystemEmbargoTypeByNameAndGuarantor(embargoDefinition.name, embargoDefinition.guarantor);

				// create template or upgrade the old one, new system embargos are enabled by default unless they have a custom one that already exists
				if (dbEmbargo == null) {
					EmbargoType possibleCustomEmbargo = settingRepo.findNonSystemEmbargoTypeByNameAndGuarantor(embargoDefinition.name, embargoDefinition.guarantor);
					
					dbEmbargo = settingRepo.createEmbargoType(embargoDefinition.name, embargoDefinition.description, embargoDefinition.duration, embargoDefinition.active);
					dbEmbargo.setGuarantor(embargoDefinition.guarantor);
					dbEmbargo.setSystemRequired(true);
					// if we have a custom one that's named the same, make sure this new one is not active by default
					if(possibleCustomEmbargo != null) {
						dbEmbargo.setActive(false);
					}
					Logger.info("New System Embargo Type being installed [%s]@[%s]", dbEmbargo.getName(), dbEmbargo.getGuarantor().name());
					dbEmbargo.save();
				} else {
					EmbargoType loadedEmbargo = settingRepo.createEmbargoType(embargoDefinition.name, embargoDefinition.description, embargoDefinition.duration, embargoDefinition.active);
					loadedEmbargo.setGuarantor(embargoDefinition.guarantor);
					loadedEmbargo.setSystemRequired(true);

					// if the embargo in the DB doesn't match in content with the one loaded from array
					if (!(dbEmbargo.getDescription().equals(loadedEmbargo.getDescription())) || !( dbEmbargo.getDuration() == loadedEmbargo.getDuration()) || !(dbEmbargo.getGuarantor().ordinal() == loadedEmbargo.getGuarantor().ordinal())) {
						EmbargoType possibleCustomEmbargo = settingRepo.findNonSystemEmbargoTypeByNameAndGuarantor(embargoDefinition.name, embargoDefinition.guarantor);
						// if this System template already has a custom template (meaning one named the same but that is !isSystemRequired)
						if (possibleCustomEmbargo != null) {
							// a custom version of this System email template already exists, it's safe to override dbTemplate's data and save
							dbEmbargo.setSystemRequired(false);
							// upgraded system embargos that have a custom version are disabled by default
							dbEmbargo.setActive(false);
							dbEmbargo.setDescription(loadedEmbargo.getDescription());
							dbEmbargo.setDuration(loadedEmbargo.getDuration());
							dbEmbargo.setGuarantor(loadedEmbargo.getGuarantor());
							dbEmbargo.setSystemRequired(true);
							Logger.info("Upgrading Old System Embargo Type for [%s]@[%s]", dbEmbargo.getName(), dbEmbargo.getGuarantor().name());
							dbEmbargo.save();
						}
						// there is no custom one yet, we need to make the dbEmbargo !isSystemRequired and the save loadedEmbargo
						else {
							Logger.info("Upgrading Old System Embargo Type and creating custom version for [%s]@[%s]", dbEmbargo.getName(), dbEmbargo.getGuarantor().name());
							dbEmbargo.setSystemRequired(false);
							dbEmbargo.save();
							// upgraded system embargos are disabled by default
							loadedEmbargo.setActive(false);
							loadedEmbargo.save();
						}
					}
				}
			}
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to initialize default embargos.");
		} finally {
			// turn on authorization after we're done
			context.restoreAuthorization();
		}
	}

	@OnApplicationStart
	public static class initializeSystemEmbargos extends Job {
		public void doJob() {
			try {
				Logger.info("initializeSystemEmbargos running...");
				SystemDataLoader systemDataLoader = Spring.getBeanOfType(SystemDataLoader.class);
				systemDataLoader.generateAllSystemEmbargos();
				Logger.info("initializeSystemEmbargos done!");
			} catch (RuntimeException re) {
				Logger.error(re, "Unable to initialize system email templates.");
			}
		}
	}
}
