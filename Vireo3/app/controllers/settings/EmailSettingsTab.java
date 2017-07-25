package controllers.settings;

import static org.tdl.vireo.constant.AppConfig.EMAIL_FROM;
import static org.tdl.vireo.constant.AppConfig.EMAIL_REPLY_TO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceException;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.AdministrativeGroup;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.jpa.JpaAdministrativeGroupImpl;
import org.tdl.vireo.model.jpa.JpaAdministrativeGroupImpl.AdminGroupsComparator;
import org.tdl.vireo.model.jpa.JpaEmailTemplateImpl;
import org.tdl.vireo.services.EmailRuleService;
import org.tdl.vireo.services.Utilities;
import org.tdl.vireo.state.State;

import play.Logger;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class EmailSettingsTab extends SettingsTab {

	@Security(RoleType.MANAGER)
	public static void emailSettings() {

		// List all colleges
		List<College> colleges = settingRepo.findAllColleges();
		// List all programs
		List<Program> programs = settingRepo.findAllPrograms();
		// List all departments
		List<Department> departments = settingRepo.findAllDepartments();
		// List all administrative groups (sorted)
		List<AdministrativeGroup> adminGroups = settingRepo.findAllAdministrativeGroups();
		if (adminGroups != null)
			Collections.sort(adminGroups, AdminGroupsComparator.INSTANCE);

		// Get all the states
		renderArgs.put("STATES", stateManager.getAllStates());

		// Get all the email workflow rules
		renderArgs.put("RULES", settingRepo.findAllEmailWorkflowRules());

		renderArgs.put("EMAIL_FROM", settingRepo.getConfigValue(EMAIL_FROM));
		renderArgs.put("EMAIL_REPLY_TO", settingRepo.getConfigValue(EMAIL_REPLY_TO));

		// List all templates
		List<EmailTemplate> templates = settingRepo.findAllEmailTemplates();

		// List all email recipient types (sorted)
		List<RecipientType> recipientTypes = Arrays.asList(RecipientType.sortedValues());

		// List all email recipient types (sorted)
		List<ConditionType> conditionTypes = Arrays.asList(ConditionType.sortedValues());

		String nav = "settings";
		String subNav = "email";
		renderTemplate("SettingTabs/emailSettings.html", nav, subNav, templates, recipientTypes, conditionTypes,

		// Sortable lists
		        colleges, programs, departments, adminGroups

		);
	}

	// ////////////////////////////////////////////
	// WORKFLOW EMAIL RULES
	// ////////////////////////////////////////////

	@Security(RoleType.MANAGER)
	public static void addEditEmailWorkflowRuleJSON(String id, String stateString, String conditionCategory, String conditionIDString, String recipientString, String templateString) {
		boolean newRule = true;
		try {
			if (stateString == null || stateString.trim().length() == 0)
				throw new IllegalArgumentException("State could not be determined");

			State associatedState = stateManager.getState(stateString);

			EmailWorkflowRule rule;
			String conditionCategoryJSON = "";
			String conditionIdJSON = "";
			String conditionDisplayJSON = "";
			String recipientTypeJSON = "";
			String templateJSON = "";

			if ((id != null) && (id.trim().length() != 0) && (!id.equals("null"))) {
				newRule = false;
				// Modify an existing rule
				Long ruleID = Long.parseLong(id);

				rule = settingRepo.findEmailWorkflowRule(ruleID);

				// make sure we're not trying to edit a system workflow rule
				if (rule.isSystem()) {
					throw new UnsupportedOperationException("Cannot edit a system email workflow rule!");
				}

				AbstractWorkflowRuleCondition condition;
				// Check if condition exists
				if ((condition = rule.getCondition()) == null) {
					condition = settingRepo.createEmailWorkflowRuleCondition(null);
					rule.setCondition(condition);
				}

				// Check if condition category exists
				if (conditionCategory != null && conditionCategory.trim().length() != 0) {
					condition.setConditionType(ConditionType.valueOf(conditionCategory));
					condition.setConditionId(null);
					conditionIdJSON = null;
					conditionCategoryJSON = rule.getCondition().getConditionType().name();
				}

				// Check if conditionID exists
				if (conditionIDString != null && conditionIDString.trim().length() != 0) {
					condition.setConditionId(Long.parseLong(conditionIDString));
					if (rule.getCondition().getConditionType() != null)
						conditionCategoryJSON = rule.getCondition().getConditionType().name();
					conditionIdJSON = rule.getCondition().getConditionId().toString();
				}

				EmailTemplate template;
				// Check if email template exists
				if (templateString != null && templateString.trim().length() != 0) {
					template = settingRepo.findEmailTemplate(Long.parseLong(templateString));
					rule.setEmailTemplate((JpaEmailTemplateImpl) template);
					templateJSON = rule.getEmailTemplate().getName();
				}

				// Check if recipient type exists
				if (recipientString != null && recipientString.trim().length() != 0) {
					rule.setRecipientType(RecipientType.valueOf(recipientString));
					// if recipient type is AdminGroup
					if (rule.getRecipientType() == RecipientType.AdminGroup) {
						String adminGroupString = params.get("AdminGroupId");
						// Check if adminGroup Id exists
						if (adminGroupString == null) {
							throw new IllegalArgumentException("Can't save an AdminGroup type of recipient without AdminGroupId to link to!");
						}
						Long adminGroupId = Long.parseLong(adminGroupString);
						AdministrativeGroup adminGroup = settingRepo.findAdministrativeGroup(adminGroupId);
						rule.setAdminGroupRecipient((JpaAdministrativeGroupImpl) adminGroup);
					} else {
						rule.setAdminGroupRecipient(null);
					}
					recipientTypeJSON = rule.getRecipientType().toString() + (rule.getAdminGroupRecipient() != null ? " (" + rule.getAdminGroupRecipient().getName() + ")" : "");
				}

			} else {
				rule = settingRepo.createEmailWorkflowRule(associatedState);
			}

			// save the condition if it exists
			if (rule.getCondition() != null) {
				rule.getCondition().save();
			}

			// save the rule
			rule.save();

			Logger.info("%s (%d: %s) has " + (newRule ? "added a new" : "edited an existing") + " workflow email rule #%d.", context.getPerson().getFormattedName(NameFormat.FIRST_LAST), context.getPerson().getId(), context.getPerson().getEmail(), rule.getId());

			renderJSON("{ \"success\": true, \"id\": " + rule.getId() + ", \"state\": \"" + rule.getAssociatedState().getBeanName() + "\",\"conditionCategory\": \"" + conditionCategoryJSON + "\",\"condition\": \"" + conditionIdJSON + "\",\"conditionDisplayJSON\": \"" + conditionDisplayJSON + "\",\"recipientType\": \"" + recipientTypeJSON + "\",\"templateString\": \"" + templateJSON + "\",\"isDisabled\": " + rule.isDisabled() + ",\"isSystem\": " + rule.isSystem() + " }");

		} catch (RuntimeException re) {
			Logger.error(re, "Unable to create the workflow email rule.");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"success\": false, \"message\": \"" + message + "\" }");

		}
	}

	@Security(RoleType.MANAGER)
	public static void removeEmailWorkflowRuleJSON(String ruleID, String stateString) {
		try {

			EmailWorkflowRule rule = settingRepo.findEmailWorkflowRule(Long.parseLong(ruleID));

			// make sure we're not trying to remove a system workflow rule
			if (rule.isSystem()) {
				throw new UnsupportedOperationException("Cannot remove a system email workflow rule!");
			}

			rule.delete();

			renderJSON("{ \"success\": true }");
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to remove email workflow rule");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"success\": false, \"message\": \"" + message + "\" }");
		}
	}

	@Security(RoleType.MANAGER)
	public static void enableEmailWorkflowRuleJSON(String ruleID, String stateString) {
		try {

			EmailWorkflowRule rule = settingRepo.findEmailWorkflowRule(Long.parseLong(ruleID));

			// validate rule
			if (EmailRuleService.ruleIsValid(rule)) {
				rule.enable();
				rule.save();
			} else {
				throw new RuntimeException("The Email Workflow Rule is incomplete/invalid and cannot be enabled!");
			}

			renderJSON("{ \"success\": true }");
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to enable email workflow rule");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"success\": false, \"message\": \"" + message + "\" }");
		}
	}

	@Security(RoleType.MANAGER)
	public static void disableEmailWorkflowRuleJSON(String ruleID, String stateString) {
		try {

			EmailWorkflowRule rule = settingRepo.findEmailWorkflowRule(Long.parseLong(ruleID));

			rule.disable();
			rule.save();

			renderJSON("{ \"success\": true }");
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to enable email workflow rule");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"success\": false, \"message\": \"" + message + "\" }");
		}
	}

	// ////////////////////////////////////////////
	// EMAIL TEMPLATES
	// ////////////////////////////////////////////

	/**
	 * Create a new email template. The id of the new template will be returned.
	 * 
	 * @param name
	 *            The unique name of the template
	 * @param subject
	 *            The subject of the template
	 * @param message
	 *            The message of the template
	 */
	@Security(RoleType.MANAGER)
	public static void addEmailTemplateJSON(String name, String subject, String message) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			if (subject == null || subject.trim().length() == 0)
				throw new IllegalArgumentException("Subject is required");

			if (message == null || message.trim().length() == 0)
				throw new IllegalArgumentException("Body message is required");

			// Get the list of all templates
			List<EmailTemplate> templates = settingRepo.findAllEmailTemplates();

			EmailTemplate template = settingRepo.createEmailTemplate(name, subject, message);
			templates.add(template);

			saveModelOrder(templates);

			Logger.info("%s (%d: %s) has added email template #%d.\nTemplate Name = '%s'\nTemplate Subject = '%s'\nTemplate Body = '%s'", context.getPerson().getFormattedName(NameFormat.FIRST_LAST), context.getPerson().getId(), context.getPerson().getEmail(), template.getId(), template.getName(), template.getSubject(), template.getMessage());

			name = escapeJavaScript(template.getName());
			subject = escapeJavaScript(template.getSubject());
			message = escapeJavaScript(template.getMessage());
			String custom = "<strong class=\\\"custom_system_email_template\\\">" + (template.isSystemRequired() ? "" : " (*)" ) + "</strong>";

			renderJSON("{ \"success\": \"true\", \"id\": " + template.getId() + ", \"name\": \"" + name + "\", \"subject\": \"" + subject + "\", \"message\": \"" + message + "\", \"system\": " + template.isSystemRequired() + ", \"custom\": \"" + custom + "\" }");
		} catch (IllegalArgumentException iae) {
			String errorMessage = escapeJavaScript(iae.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + errorMessage + "\" }");

		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another email template already exists with the name: '" + name + "'\" }");

		} catch (RuntimeException re) {
			Logger.error(re, "Unable to add email template");
			String errorMessage = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + errorMessage + "\" }");
		}
	}

	/**
	 * Retrieve the attributes of an email template.
	 * 
	 * @param emailTemplateId
	 *            The id of the email template in the form "emailTemplate_id".
	 */
	public static void retrieveEmailTemplateJSON(String emailTemplateId) {
		try {
			// Retrieve the old template
			String[] parts = emailTemplateId.split("_");
			Long id = Long.valueOf(parts[1]);
			EmailTemplate template = settingRepo.findEmailTemplate(id);

			String name = escapeJavaScript(template.getName());
			String subject = escapeJavaScript(template.getSubject());
			String message = escapeJavaScript(template.getMessage());

			renderJSON("{ \"success\": true, \"id\": " + template.getId() + ", \"name\": \"" + name + "\", \"subject\": \"" + subject + "\", \"message\": \"" + message + "\", \"system\": " + template.isSystemRequired() + " }");
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to retrieve email template");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": true, \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing template's name, subject, and message.
	 * 
	 * @param emailTemplateId
	 *            The id of the template, in the form "emailTemplate_id"
	 * @param name
	 *            The new name of the template
	 * @param subject
	 *            The new subject of the template
	 * @param message
	 *            The new message of the template.
	 */
	@Security(RoleType.MANAGER)
	public static void editEmailTemplateJSON(String emailTemplateId, String name, String subject, String message) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			if (subject == null || subject.trim().length() == 0)
				throw new IllegalArgumentException("Subject is required");

			if (message == null || message.trim().length() == 0)
				throw new IllegalArgumentException("Body message is required");

			// Save the new label
			String[] parts = emailTemplateId.split("_");
			Long id = Long.valueOf(parts[1]);
			EmailTemplate template = settingRepo.findEmailTemplate(id);
			template.setName(name);
			template.setSubject(subject);
			template.setMessage(message);
			template.save();

			Logger.info("%s (%d: %s) has edited email template #%d.\nTemplate Name = '%s'\nTemplate Subject = '%s'\nTemplate Body = '%s'", context.getPerson().getFormattedName(NameFormat.FIRST_LAST), context.getPerson().getId(), context.getPerson().getEmail(), template.getId(), template.getName(), template.getSubject(), template.getMessage());

			name = escapeJavaScript(template.getName());
			subject = escapeJavaScript(template.getSubject());
			message = escapeJavaScript(template.getMessage());
			String custom = "<strong class=\\\"custom_system_email_template\\\">" + (template.isSystemRequired() ? "" : " (*)" ) + "</strong>";

			renderJSON("{ \"success\": \"true\", \"id\": " + template.getId() + ", \"name\": \"" + name + "\", \"subject\": \"" + subject + "\", \"message\": \"" + message + "\", \"system\": " + template.isSystemRequired() + ", \"custom\": \"" + custom + "\" }");
		} catch (IllegalArgumentException iae) {
			String errorMessage = escapeJavaScript(iae.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + errorMessage + "\" }");

		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another email template already exists with the name: '" + name + "'\" }");

		} catch (RuntimeException re) {
			Logger.error(re, "Unable to edit email template");
			String errorMessage = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + errorMessage + "\" }");
		}
	}

	/**
	 * Remove an existing email template
	 * 
	 * @param emailTemplateId
	 *            The id of the template to be removed of the form "emailTemplate_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeEmailTemplateJSON(String emailTemplateId) {
		try {
			// Delete the old template
			String[] parts = emailTemplateId.split("_");
			Long id = Long.valueOf(parts[1]);
			EmailTemplate template = settingRepo.findEmailTemplate(id);
			template.delete();

			Logger.info("%s (%d: %s) has deleted email template #%d.\nTemplate Name = '%s'\nTemplate Subject = '%s'\nTemplate Body = '%s'", context.getPerson().getFormattedName(NameFormat.FIRST_LAST), context.getPerson().getId(), context.getPerson().getEmail(), template.getId(), template.getName(), template.getSubject(), template.getMessage());

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to remove email template");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Reorder a list of email templates.
	 * 
	 * @param emailTemplateIds
	 *            An ordered list of ids in the form: "emailTemplate_1,emailTemplate_3,emailTemplate_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderEmailTemplatesJSON(String emailTemplateIds) {

		try {

			if (emailTemplateIds != null && emailTemplateIds.trim().length() > 0) {
				// Save the new order
				List<EmailTemplate> templates = resolveIds(emailTemplateIds, EmailTemplate.class);
				saveModelOrder(templates);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to reorder email template");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Handle updating the individual values under the application settings tab.
	 * 
	 * If the field is defined as a boolean, then value should either be an null/empty string to be turned off, otherwise any other value will be interpreted as on.
	 * 
	 * @param field
	 *            The field being updated.
	 * @param value
	 *            The value of the new field.
	 */
	@Security(RoleType.MANAGER)
	public static void updateEmailSettingsJSON(String field, String value) {

		try {
			List<String> textFields = new ArrayList<String>();
			textFields.add(EMAIL_FROM);
			textFields.add(EMAIL_REPLY_TO);

			if (textFields.contains(field)) {
				// This is a free-form text field
				if (EMAIL_FROM.toString().equals(field) && !Utilities.validateEmailAddress(value)) {
					throw new IllegalArgumentException("The current email is invalid");
				}

				if (EMAIL_REPLY_TO.toString().equals(field) && !Utilities.validateEmailAddress(value)) {
					throw new IllegalArgumentException("The current email is invalid");
				}

				Configuration config = settingRepo.findConfigurationByName(field);

				if (config == null)
					config = settingRepo.createConfiguration(field, value);
				else {
					config.setValue(value);
				}
				config.save();

			} else {
				throw new IllegalArgumentException("Unknown field '" + field + "'");
			}

			field = escapeJavaScript(field);
			value = escapeJavaScript(value);

			renderJSON("{ \"success\": \"true\", \"field\": \"" + field + "\", \"value\": \"" + value + "\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		} catch (RuntimeException re) {
			Logger.error(re, "Unable to update application settings");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

}
