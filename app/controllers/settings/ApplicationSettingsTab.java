package controllers.settings;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.RoleType;

import play.Logger;
import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;
import static org.tdl.vireo.model.Configuration.*;

@With(Authentication.class)
public class ApplicationSettingsTab extends SettingsTab {

	/**
	 * Display the application settings page.
	 */
	@Security(RoleType.MANAGER)
	public static void applicationSettings() {
		
		renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(SUBMISSIONS_OPEN));
		renderArgs.put("ALLOW_MULTIPLE_SUBMISSIONS", settingRepo.findConfigurationByName(ALLOW_MULTIPLE_SUBMISSIONS));
		renderArgs.put("REQUEST_COLLEGE", settingRepo.findConfigurationByName(REQUEST_COLLEGE));
		renderArgs.put("REQUEST_UMI", settingRepo.findConfigurationByName(REQUEST_UMI));
		
		Configuration currentSemester = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
		if (currentSemester != null)
			renderArgs.put("CURRENT_SEMESTER", currentSemester.getValue());

		Configuration submissionInstructions = settingRepo.findConfigurationByName(SUBMISSION_INSTRUCTIONS);
		if (submissionInstructions != null)
			renderArgs.put("SUBMISSION_INSTRUCTIONS", submissionInstructions.getValue());

		
		List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
		
		
		String nav = "settings";
		String subNav = "application";
		renderTemplate("SettingTabs/applicationSettings.html",nav, subNav, actions);
	}
	
	/**
	 * Handle updating the individual values under the application settings tab.
	 * 
	 * If the field is defined as a boolean, then value should either be an
	 * null/empty string to be turned off, otherwise any other value will be
	 * interpreted as on.
	 * 
	 * @param field
	 *            The field being updated.
	 * @param value
	 *            The value of the new field.
	 */
	@Security(RoleType.MANAGER)
	public static void updateApplicationSettingsJSON(String field, String value) {

		try {
			List<String> booleanFields = new ArrayList<String>();
			booleanFields.add(SUBMISSIONS_OPEN);
			booleanFields.add(ALLOW_MULTIPLE_SUBMISSIONS);
			booleanFields.add(REQUEST_COLLEGE);
			booleanFields.add(REQUEST_UMI);
			List<String> textFields = new ArrayList<String>();
			textFields.add(CURRENT_SEMESTER);
			textFields.add(SUBMISSION_INSTRUCTIONS);
			
			
			if (booleanFields.contains(field)) {
				// This is a boolean field
				boolean booleanValue = true;
				if (value == null || value.trim().length() == 0)
					booleanValue = false;
				
				Configuration config = settingRepo.findConfigurationByName(field);
				if (!booleanValue && config != null)
					config.delete();
				else if (booleanValue && config == null)
					settingRepo.createConfiguration(field, "true").save();
				
				
			} else if (textFields.contains(field)) {
				// This is a free-form text field
				
				
				if (CURRENT_SEMESTER.toString().equals(field) && !isValidCurrentSemester(value)) {
					throw new IllegalArgumentException("The current semester is invalid, it must be of the form: month year. I.g. 'May 2012'");
				}
				
				Configuration config = settingRepo.findConfigurationByName(field);
				if (config == null)
					config = settingRepo.createConfiguration(field, value);
				else
					config.setValue(value);
				config.save();
			} else {
				throw new IllegalArgumentException("Unknown field '"+field+"'");
			}
			
		
			field = escapeJavaScript(field);
			value = escapeJavaScript(value);
			
			
			renderJSON("{ \"success\": \"true\", \"field\": \""+field+"\", \"value\": \""+value+"\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to update application settings");
			String message = escapeJavaScript(re.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}
	
	/**
	 * Add a new custom action value. The id and label of the new action will be
	 * returned.
	 * 
	 * @param name
	 *            The label of the new action
	 */
	@Security(RoleType.MANAGER)
	public static void addCustomActionJSON(String name) {
		
		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Label is required");
			
			// Add the new action to the end of the list.
			List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
			
			CustomActionDefinition action = settingRepo.createCustomActionDefinition(name);
			actions.add(action);
			
			saveModelOrder(actions);
			
			name = escapeJavaScript(name);
			
			renderJSON("{ \"success\": \"true\", \"id\": "+action.getId()+", \"name\": \""+name+"\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another custom action allready exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add custom action");
			String message = escapeJavaScript(re.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}

	/**
	 * Edit an existing custom action's label. The id and the label will be
	 * returned.
	 * 
	 * @param actionId
	 *            The id of the action to be edited, in the fom "action_id"
	 * @param name
	 *            The new label of the action.
	 */
	@Security(RoleType.MANAGER)
	public static void editCustomActionJSON(String actionId, String name) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Label is required");
			
			// Save the new label
			String[] parts = actionId.split("_");
			Long id = Long.valueOf(parts[1]);
			CustomActionDefinition action = settingRepo.findCustomActionDefinition(id);
			action.setLabel(name);
			action.save();
			
			name = escapeJavaScript(name);
			
			renderJSON("{ \"success\": \"true\", \"id\": "+action.getId()+", \"name\": \""+name+"\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another custom action allready exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to edit custom action");
			String message = escapeJavaScript(re.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}
	
	/**
	 * Remove an existing custom action value.
	 * 
	 * @param actionId
	 *            The id of the action to be removed of the form "action_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeCustomActionJSON(String actionId) {
		try {
			// Delete the old action
			String[] parts = actionId.split("_");
			Long id = Long.valueOf(parts[1]);
			CustomActionDefinition action = settingRepo.findCustomActionDefinition(id);
			action.delete();
			
			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove custom action");
			String message = escapeJavaScript(re.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}
	
	
	/**
	 * Reorder a list of custom actions.
	 * 
	 * @param actionIds
	 *            An ordered list of ids in the form:
	 *            "action_1,action3,action_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderCustomActionsJSON(String actionIds) {

		try {
			
			if (actionIds != null && actionIds.trim().length() > 0) {
				// Save the new order
				List<CustomActionDefinition> actions = resolveIds(actionIds,CustomActionDefinition.class);
				saveModelOrder(actions);
			}
			
			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder custom action");
			String message = escapeJavaScript(re.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}
	

	
	/**
	 * Validate the current semester string which must be of the format: month
	 * year.
	 * 
	 * @param value
	 *            The value to be validated.
	 * @return Either true if valid, otherwise false.
	 */
	protected static boolean isValidCurrentSemester(String value) {

		try {
			if (value == null || value.trim().length() == 0)
				// The blank value is also acceptable.
				return true;
			
			String[] parts = value.split(" ");

			if (parts.length != 2)
				return false;

			Integer month = monthNameToInt(parts[0]);
			Integer year = Integer.valueOf(parts[1]);

			return true;
		} catch (RuntimeException re) {
			return false;
		}
	}
	
	
	
	
	
}
