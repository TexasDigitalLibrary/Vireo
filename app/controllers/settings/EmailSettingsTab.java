package controllers.settings;

import static org.tdl.vireo.constant.AppConfig.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.RoleType;

import play.Logger;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class EmailSettingsTab extends SettingsTab {


	@Security(RoleType.MANAGER)
	public static void emailSettings(){
		
		// Get the email checkboxes
		renderArgs.put("EMAIL_DELAY_SENDING_ADVISOR_REQUEST", settingRepo.findConfigurationByName(EMAIL_DELAY_SENDING_ADVISOR_REQUEST));


		
		// List all templates
		List<EmailTemplate> templates = settingRepo.findAllEmailTemplates();
		
		String nav = "settings";
		String subNav = "email";
		renderTemplate("SettingTabs/emailSettings.html",nav, subNav, templates);
	}
	
	
	/**
	 * Receive updates for email configuration (the checkboxs at the top of the page, not the templates)
	 * 
	 * @param field
	 *            The field being updated.
	 * @param value
	 *            The value (either something or null)
	 */
	@Security(RoleType.REVIEWER)
	public static void updateEmailSettingsJSON(String field, String value) {
		
		try {
			boolean booleanValue = true;
			if (value == null || value.trim().length() == 0)
				booleanValue = false;
			
			
			List<String> editableFields = new ArrayList<String>();
			editableFields.add(EMAIL_DELAY_SENDING_ADVISOR_REQUEST);
			
			if (!editableFields.contains(field))
				throw new IllegalArgumentException("Unknown field '"+field+"'");
			
			Configuration configuration = settingRepo.findConfigurationByName(field);

			if (!booleanValue && configuration != null)
				configuration.delete();
			else if (booleanValue && configuration == null)
				settingRepo.createConfiguration(field, "true").save();

			
			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to update email settings");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
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

			Logger.info("%s (%d: %s) has added email template #%d.\nTemplate Name = '%s'\nTemplate Subject = '%s'\nTemplate Body = '%s'",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
					context.getPerson().getId(), 
					context.getPerson().getEmail(),
					template.getId(),
					template.getName(),
					template.getSubject(),
					template.getMessage());
			
			name = escapeJavaScript(template.getName());
			subject = escapeJavaScript(template.getSubject());
			message = escapeJavaScript(template.getMessage());
			
			String system = " ";
			if (template.isSystemRequired())
				system = ", \"system\": \"true\" ";

			renderJSON("{ \"success\": \"true\", \"id\": " + template.getId() + ", \"name\": \"" + name + "\", \"subject\": \"" + subject + "\", \"message\": \"" + message + "\""+system+"}");
		} catch (IllegalArgumentException iae) {
			String errorMessage = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+errorMessage+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another email template already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add email template");
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
			
			String system = " ";
			if (template.isSystemRequired())
				system = ", \"system\": \"true\" ";

			renderJSON("{ \"success\": \"true\", \"id\": " + template.getId() + ", \"name\": \"" + name + "\", \"subject\": \"" + subject + "\", \"message\": \"" + message + "\""+system+"}");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to retrieve email template");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
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

			Logger.info("%s (%d: %s) has edited email template #%d.\nTemplate Name = '%s'\nTemplate Subject = '%s'\nTemplate Body = '%s'",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
					context.getPerson().getId(), 
					context.getPerson().getEmail(),
					template.getId(),
					template.getName(),
					template.getSubject(),
					template.getMessage());
			
			name = escapeJavaScript(template.getName());
			subject = escapeJavaScript(template.getSubject());
			message = escapeJavaScript(template.getMessage());

			String system = " ";
			if (template.isSystemRequired())
				system = ", \"system\": \"true\" ";
			
			renderJSON("{ \"success\": \"true\", \"id\": " + template.getId() + ", \"name\": \"" + name + "\", \"subject\": \"" + subject + "\", \"message\": \"" + message + "\""+system+"}");
		} catch (IllegalArgumentException iae) {
			String errorMessage = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+errorMessage+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another email template already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to edit email template");
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

			Logger.info("%s (%d: %s) has deleted email template #%d.\nTemplate Name = '%s'\nTemplate Subject = '%s'\nTemplate Body = '%s'",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
					context.getPerson().getId(), 
					context.getPerson().getEmail(),
					template.getId(),
					template.getName(),
					template.getSubject(),
					template.getMessage());
			
			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove email template");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Reorder a list of email templates.
	 * 
	 * @param emailTemplateIds
	 *            An ordered list of ids in the form:
	 *            "emailTemplate_1,emailTemplate_3,emailTemplate_2"
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
			Logger.error(re,"Unable to reorder email template");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}
}
