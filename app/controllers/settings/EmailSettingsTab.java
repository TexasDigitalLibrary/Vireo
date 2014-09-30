package controllers.settings;

import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition.ConditionType;
import org.tdl.vireo.model.AdministrativeGroup;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.WorkflowEmailRule;
import org.tdl.vireo.model.jpa.JpaAdministrativeGroupImpl;
import org.tdl.vireo.model.jpa.JpaEmailTemplateImpl;
import org.tdl.vireo.model.jpa.JpaEmailWorkflowRuleConditionImpl;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.modules.spring.Spring;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class EmailSettingsTab extends SettingsTab {

	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	
	@Security(RoleType.MANAGER)
	public static void emailSettings(){
		
		List<College> colleges = settingRepo.findAllColleges();
		List<Program> programs = settingRepo.findAllPrograms();
		List<Department> departments = settingRepo.findAllDepartments();
		List<AdministrativeGroup> adminGroups = settingRepo.findAllAdministrativeGroups();
		
		// Get all the states
		renderArgs.put("STATES", stateManager.getAllStates());
		
		// Get all the email workflow rules
		renderArgs.put("RULES", settingRepo.findAllWorkflowEmailRules());
		
		// List all templates
		List<EmailTemplate> templates = settingRepo.findAllEmailTemplates();
		
		// List all email recipient types
		List<RecipientType> recipientTypes = Arrays.asList(RecipientType.values());
		
		String nav = "settings";
		String subNav = "email";
		renderTemplate("SettingTabs/emailSettings.html",nav, subNav, templates, recipientTypes,
				
				// Sortable lists
				colleges, programs, departments, adminGroups
				
				);
	}
	
	// ////////////////////////////////////////////
	// WORKFLOW EMAIL RULES
	// ////////////////////////////////////////////
	
	@Security(RoleType.MANAGER)
	public static void addEditEmailWorkflowRuleJSON(String id, String stateString, String conditionCategory, String conditionIDString, String recipientString, String templateString) {
		try {
			Logger.info(recipientString);
			if (stateString == null || stateString.trim().length() == 0)
				throw new IllegalArgumentException("State could not be determined");
			
			State associatedState = stateManager.getState(stateString);

			List<WorkflowEmailRule> rules = settingRepo.findWorkflowEmailRulesByState(associatedState);
			WorkflowEmailRule rule;
			String conditionCategoryJSON = "";
			String conditionIdJSON = "";
			String conditionDisplayJSON = "";
			String recipientTypeJSON = "";
			String templateJSON = "";
			
			if ((id != null) && (id.trim().length() != 0) && (!id.equals("null"))) {
				// Modify an existing rule
				Long ruleID = Long.parseLong(id);
				
				rule = settingRepo.findWorkflowEmailRule(ruleID);
					
				AbstractWorkflowRuleCondition condition;
				//Check if condition exists
				if((condition = rule.getCondition()) == null) {
					condition = new JpaEmailWorkflowRuleConditionImpl();
					condition.save();
					rule.setCondition(condition);
				} 
				
				//Check if condition category exists
				if (conditionCategory != null && conditionCategory.trim().length() != 0) {
					condition.setConditionType(ConditionType.valueOf(conditionCategory));
					condition.setConditionId(null);
					conditionIdJSON = null;
					conditionCategoryJSON = rule.getCondition().getConditionType().name();
					condition.save();
				}
				
				//Check if conditionID exists
				if (conditionIDString != null && conditionIDString.trim().length() != 0) {
					condition.setConditionId(Long.parseLong(conditionIDString));
					if(rule.getCondition().getConditionType() != null)
						conditionCategoryJSON = rule.getCondition().getConditionType().name();
					conditionIdJSON = rule.getCondition().getConditionId().toString();
					//conditionDisplayJSON = settingRepo.findCollege(Long.parseLong(conditionIDString)).getName();
					condition.save();
				}
				
				EmailTemplate template;
				//Check if email template exists
				if (templateString != null && templateString.trim().length() != 0) {
					template = settingRepo.findEmailTemplate(Long.parseLong(templateString));
					rule.setEmailTemplate((JpaEmailTemplateImpl)template);
					templateJSON = rule.getEmailTemplate().name;
				} 
				
				//Check if recipient type exists
				if (recipientString != null && recipientString.trim().length() != 0) {
					rule.setRecipientType(RecipientType.valueOf(recipientString));
					// if recipient type is AdminGroup
					if(rule.getRecipientType() == RecipientType.AdminGroup) {
						String adminGroupString = params.get("AdminGroupId");
						// Check if adminGroup Id exists
						if(adminGroupString == null) {
							throw new IllegalArgumentException("Can't save an AdminGroup type of recipient without AdminGroupId to link to!");
						}
						Long adminGroupId = Long.parseLong(adminGroupString);
						AdministrativeGroup adminGroup = settingRepo.findAdministrativeGroup(adminGroupId);
						rule.setAdminGroupRecipient((JpaAdministrativeGroupImpl)adminGroup);
					} else {
						rule.setAdminGroupRecipient(null);
					}
					recipientTypeJSON = rule.getRecipientType().toString() + (rule.getAdminGroupRecipient() != null ? " (" + rule.getAdminGroupRecipient().getName() + ")" : "");
				}
				
			} else {
				rule = settingRepo.createWorkflowEmailRule(associatedState);
			}

			rules.add(rule);
						
			saveModelOrder(rules);
			
			Logger.info("%s (%d: %s) has added workflow email rule #%d.\n","user",0,"string",rule.getId());
			
			renderJSON("{ \"success\": \"true\", \"id\": "+rule.getId()+", \"state\": \""+rule.getAssociatedState().getBeanName()+"\",\"conditionCategory\": \""+conditionCategoryJSON+"\",\"condition\": \""+conditionIdJSON+"\",\"conditionDisplayJSON\": \""+conditionDisplayJSON+"\",\"recipientType\": \""+recipientTypeJSON+"\",\"templateString\": \""+templateJSON+"\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to create the workflow email rule.");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		
		}
	}
	
	@Security(RoleType.MANAGER)
	public static void removeEmailWorkflowRuleJSON(String ruleID, String stateString) { 
		try {
			
			WorkflowEmailRule rule = settingRepo.findWorkflowEmailRule(Long.parseLong(ruleID));
			
			rule.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove email template");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
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
