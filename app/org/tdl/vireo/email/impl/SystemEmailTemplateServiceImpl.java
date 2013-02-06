package org.tdl.vireo.email.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.spring.Spring;

/**
 * Manage all pre-defined system email templates. This implementation manages
 * all the templates found in /conf/emails/[name].email. On application start
 * these templates will be loaded into persistent storage if they are not
 * already present. Also at any time vireo can refresh the template with the
 * pre-defined version individually with the generateSystemEmailTemplate()
 * method.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class SystemEmailTemplateServiceImpl implements
		org.tdl.vireo.email.SystemEmailTemplateService {

	// Constants
	public static final String BASE_PATH = Play.applicationPath + File.separator + "conf"+File.separator+"emails"+File.separator;
	public static final Pattern SUBJECT_PATTERN = Pattern.compile("\\s*Subject:(.*)[\\n\\r]{1}");
	
	// Spring injected dependencies
	public SecurityContext context;
	public SettingsRepository settingRepo;
	
	/**
	 * @param settingRepo The settings repository used for generating email templates.
	 */
	public void setSettingsRepository(SettingsRepository settingRepo) {
		this.settingRepo = settingRepo;
	}
	
	/**
	 * @param context The security context.
	 */
	public void setSecurityContext(SecurityContext context) {
		this.context = context;
	}
	
	
	@Override
	public EmailTemplate generateSystemEmailTemplate(String name) {
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
			if (index < 0 )
				index = data.indexOf("\r");
			String message = data.substring(index);

			if (subject == null || subject.length() == 0)
				throw new IllegalStateException("Unable to identify the template's subject.");
			
			if (message == null || message.length() == 0)
				throw new IllegalStateException("Unable to identify the template's message.");
			
			
			try {
				context.turnOffAuthorization();

				// Check if the template already exists
				EmailTemplate template = settingRepo.findEmailTemplateByName(name);
				if (template == null) {
					// The template dosn't exist, so create a new one.
					template = settingRepo.createEmailTemplate(name,subject, message);
				} else {
					// The template already exists. Update it's contents.
					template.setSubject(subject);
					template.setMessage(message);
				}
				template.setSystemRequired(true);
				
				template.save();
				return template;
			} finally {
				context.restoreAuthorization();
			}
			
		} catch (Exception e) {
			throw new IllegalStateException(
					"Unable to generate system email template: " + name, e);
		}

	}

	@Override
	public List<EmailTemplate> generateAllSystemEmailTemplates() {
		
		List<EmailTemplate> created = new ArrayList<EmailTemplate>();
		for (String name : getAllSystemEmailTemplateNames()) {
			
			EmailTemplate template = settingRepo.findEmailTemplateByName(name);
			
			if (template == null) {
				template = generateSystemEmailTemplate(name);
				created.add(template);
			}
		}

		return created;
	}

	@Override
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
	protected String encodeTemplateName(String name) {
		
		return name.replaceAll(" ","_") + ".email";
	}
	
	/**
	 * Decode a template file name into a template name.
	 * 
	 * @param path
	 *            The file name.
	 * @return The template name.
	 */
	protected String decodeTemplateName(String path) {
		
		if (path.endsWith(".email"))
			path = path.substring(0,path.length() - ".email".length());
		
		return path.replaceAll("_", " ");
	}

	/**
	 * When the application starts generate all the system email templates if
	 * they are not already present.
	 */
	@OnApplicationStart
	public static class initializeSystemEmailTemplates extends Job {
		public void doJob() {
			try {
				SystemEmailTemplateServiceImpl templateService = Spring.getBeanOfType(SystemEmailTemplateServiceImpl.class);
				templateService.generateAllSystemEmailTemplates();
			} catch (RuntimeException re) {
				Logger.error(re,"Unable to initialize system email templates.");
			}
		}
	}

}
