package controllers.settings;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;

import play.Logger;
import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;
import static org.tdl.vireo.model.Configuration.*;

/**
 * Application settings
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class ApplicationSettingsTab extends SettingsTab {

	// How many members to list per page when searching for new members to add as reviewers or above.
	public final static int SEARCH_MEMBERS_RESULTS_PER_PAGE = 5;
	
	/**
	 * Display the application settings page.
	 */
	@Security(RoleType.MANAGER)
	public static void applicationSettings() {
		
		renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(SUBMISSIONS_OPEN));
		renderArgs.put("ALLOW_MULTIPLE_SUBMISSIONS", settingRepo.findConfigurationByName(ALLOW_MULTIPLE_SUBMISSIONS));
		
		renderArgs.put("SUBMIT_REQUEST_BIRTH", settingRepo.findConfigurationByName(SUBMIT_REQUEST_BIRTH));
		renderArgs.put("SUBMIT_REQUEST_COLLEGE", settingRepo.findConfigurationByName(SUBMIT_REQUEST_COLLEGE));
		renderArgs.put("SUBMIT_REQUEST_UMI", settingRepo.findConfigurationByName(SUBMIT_REQUEST_UMI));
		
		Configuration currentSemester = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
		if (currentSemester != null)
			renderArgs.put("CURRENT_SEMESTER", currentSemester.getValue());

		Configuration grantor = settingRepo.findConfigurationByName(GRANTOR);
		if (grantor != null)
			renderArgs.put("GRANTOR", grantor.getValue());
		
		Configuration submitInstructions = settingRepo.findConfigurationByName(SUBMIT_INSTRUCTIONS);
		if (submitInstructions != null)
			renderArgs.put("SUBMIT_INSTRUCTIONS", submitInstructions.getValue());
		
		Configuration submitLicense = settingRepo.findConfigurationByName(SUBMIT_LICENSE);
		if (submitLicense != null)
			renderArgs.put("SUBMIT_LICENSE", submitLicense.getValue());

		
		List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
		
		
		List<Person> reviewers = personRepo.findPersonsByRole(RoleType.REVIEWER);
		
		int offset=0;
		int limit=SEARCH_MEMBERS_RESULTS_PER_PAGE;		
		List<Person> searchResults = personRepo.searchPersons(null, offset, limit);

		
		String nav = "settings";
		String subNav = "application";
		renderTemplate("SettingTabs/applicationSettings.html",nav, subNav, actions, reviewers, searchResults, offset, limit);
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
			booleanFields.add(SUBMIT_REQUEST_BIRTH);
			booleanFields.add(SUBMIT_REQUEST_COLLEGE);
			booleanFields.add(SUBMIT_REQUEST_UMI);
			List<String> textFields = new ArrayList<String>();
			textFields.add(CURRENT_SEMESTER);
			textFields.add(GRANTOR);
			textFields.add(SUBMIT_INSTRUCTIONS);
			textFields.add(SUBMIT_LICENSE);

			
			
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
				
				String oldValue = null;
				Configuration config = settingRepo.findConfigurationByName(field);
				
				if (config == null)
					config = settingRepo.createConfiguration(field, value);
				else {
					oldValue = config.getValue();
					config.setValue(value);
				}
				config.save();
				
				if(SUBMIT_LICENSE.equals(field)){
					Logger.info("%s (%d: %s) has updated license aggreement from '%s' to '%s'.",
							context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
							context.getPerson().getId(), 
							context.getPerson().getEmail(),
							oldValue,
							value);			
				}
				
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
	 * Search the current list of members and generate. This action is designed
	 * to be invoked from an AJAX call but does not produce JSON. Instead it
	 * returns an HTML snipit that can be used to replace the contents of the
	 * Search Members modal dialog box.
	 * 
	 * @param query
	 *            The search query to look for new members, or null.
	 * @param offset
	 *            The offset into the list of people
	 */
	@Security(RoleType.MANAGER)
	public static void searchMembers(String query, Integer offset) {
		
		if (offset == null || offset < 0)
			offset = 0;
		
		int limit = SEARCH_MEMBERS_RESULTS_PER_PAGE;
		List<Person> searchResults = personRepo.searchPersons(query, offset, limit);
		List<Person> reviewers = personRepo.findPersonsByRole(RoleType.REVIEWER);

		
		renderTemplate("SettingTabs/searchMembers.include",query, offset, limit, searchResults, reviewers);
	}
	
	/**
	 * Modify the person's role. This action is designed to be invoked from an
	 * AJAK call but does not produce JSON. Instead it returns a snipit of HTML
	 * that can be used to replace the current list of reviewers.
	 * 
	 * @param personId
	 *            The id of the person, in the form "person_id"
	 * @param role
	 *            The id of the new role
	 */
	@Security(RoleType.MANAGER)
	public static void updatePersonRole(String personId, int role) {
		
		try {

			RoleType newRole = RoleType.find(role);
			
			if (newRole.ordinal() > context.getPerson().getRole().ordinal())
				throw new IllegalArgumentException("Unable to set a user's role to a higher level that the current user.");
			
			
			String[] parts = personId.split("_");
			Long id = Long.valueOf(parts[1]);
			Person updated = personRepo.findPerson(id);
			RoleType oldRole = updated.getRole();
			updated.setRole(newRole);
			updated.save();
			
			Logger.info("%s (%d: %s) has changed %s (%d: %s) role from %s to %s.",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
					context.getPerson().getId(), 
					context.getPerson().getEmail(),
					updated.getFormattedName(NameFormat.FIRST_LAST), 
					updated.getId(), 
					updated.getEmail(),
					oldRole.name(),
					newRole.name());
			
			List<Person> reviewers = personRepo.findPersonsByRole(RoleType.REVIEWER);

			renderTemplate("SettingTabs/listMembers.include",reviewers,updated);
			
			
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to update person's role");
			String error = re.getMessage();
			renderTemplate("SettingTabs/listMembers.include",error);
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
