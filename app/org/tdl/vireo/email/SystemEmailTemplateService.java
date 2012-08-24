package org.tdl.vireo.email;

import java.util.List;

import org.tdl.vireo.model.EmailTemplate;

/**
 * System email template service. This is a utility service to handle managing
 * of system defined templates. These are templates that are triggered by system
 * events so there is not a direct user who can select which template to use.
 * Some examples include: 1) New user registration, 2) Forgot password
 * validation, 3) Submission complete, and others.
 * 
 * When a component is unable to find this type of email in the
 * SettingsRepository this service may be used to generate a fresh template from
 * source.
 * 
 * When the application is started the createAllSystemEmailTemplates() should be
 * called to define all system defined email templates saving them in persistent
 * storage. This allows the Vireo manager to edit these templates and have the
 * updated versions be saved in persistent storage.
 * 
 * The implementors of this interface may use any method for storing the
 * pre-defined email templates. However the default location is
 * conf/emails/[name].emailTemplate.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface SystemEmailTemplateService {

	/**
	 * Generate a fresh email template from the pre-defined templates. The email
	 * template will be saved to persistent storage. If the template already
	 * exists in the persistent storage then it's definition will be replaced
	 * with the default.
	 * 
	 * @param name
	 *            The name of the email template.
	 * @return A new unsaved email template.
	 */
	public EmailTemplate generateSystemEmailTemplate(String name);

	/**
	 * Scan all system defined email templates and if they do not exist in
	 * persistent storage load it into persistent storage. If the template
	 * already exists, then it will be left alone.
	 * 
	 * This method is intended to be called on application startup so that all
	 * templates get generated for a manager to modify without waiting for the
	 * event to be triggered.
	 * 
	 * @return A list of newly generated email templates, if no templates were
	 *         generated because they all exist in persistent storage then an
	 *         empty list is returned.
	 */
	public List<EmailTemplate> generateAllSystemEmailTemplates();

	/**
	 * @return Return a list of the names of all pre-defined system email
	 *         templates.
	 */
	public List<String> getAllSystemEmailTemplateNames();
}
