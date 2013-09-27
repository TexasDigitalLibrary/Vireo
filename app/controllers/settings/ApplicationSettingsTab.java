package controllers.settings;

import static org.tdl.vireo.constant.AppConfig.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.export.DepositException;
import org.tdl.vireo.export.DepositService;
import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.Packager;
import org.tdl.vireo.export.DepositException.FIELD;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.proquest.ProquestDegree;

import play.Logger;
import play.modules.spring.Spring;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

/**
 * Application settings
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class ApplicationSettingsTab extends SettingsTab {

	// How many members to list per page when searching for new members to add as reviewers or above.
	public final static int SEARCH_MEMBERS_RESULTS_PER_PAGE = 5;
	
	// Used for testing deposits
	public static DepositService depositService = Spring.getBeanOfType(DepositService.class);
	
	/**
	 * Display the application settings page.
	 */
	@Security(RoleType.MANAGER)
	public static void applicationSettings() {
		
		renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(SUBMISSIONS_OPEN));
		renderArgs.put("ALLOW_MULTIPLE_SUBMISSIONS", settingRepo.findConfigurationByName(ALLOW_MULTIPLE_SUBMISSIONS));
		
		renderArgs.put("CURRENT_SEMESTER", settingRepo.getConfigValue(CURRENT_SEMESTER, ""));
		renderArgs.put("GRANTOR", settingRepo.getConfigValue(GRANTOR, ""));
		renderArgs.put("SUBMIT_LICENSE_TEXT", settingRepo.getConfigValue(SUBMIT_LICENSE_TEXT));
		
		renderArgs.put("PROQUEST_INSTITUTION_CODE", settingRepo.getConfigValue(PROQUEST_INSTITUTION_CODE));
		renderArgs.put("PROQUEST_INDEXING", settingRepo.getConfigValue(PROQUEST_INDEXING));
		renderArgs.put("PROQUEST_LICENSE_TEXT", settingRepo.getConfigValue(PROQUEST_LICENSE_TEXT));

		List<String> degrees = new ArrayList<String>();
		for(Degree degree : settingRepo.findAllDegrees()) {
			if (!degrees.contains(degree.getName()))
				degrees.add(degree.getName());
		}
		for (String degree : subRepo.findAllDegrees()) {
			if (!degrees.contains(degree))
				degrees.add(degree);
		}
		List<String> proquestDegrees = new ArrayList<String>();
		for (ProquestDegree degree : proquestRepo.findAllDegrees()) {
			String sanatizedCode = degree.getCode().replaceAll("'","&#39;");
			if (!proquestDegrees.contains(sanatizedCode)) {
				proquestDegrees.add(sanatizedCode);
			}
		}
		
		List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
		
		List<DepositLocation> locations = settingRepo.findAllDepositLocations();
		
		List<Packager> packagers = new ArrayList<Packager>( (Collection) Spring.getBeansOfType(Packager.class).values() );
		List<Depositor> depositors = new ArrayList<Depositor>( (Collection) Spring.getBeansOfType(Depositor.class).values() );
		
		List<Person> reviewers = personRepo.findPersonsByRole(RoleType.REVIEWER);
		
		int offset=0;
		int limit=SEARCH_MEMBERS_RESULTS_PER_PAGE;		
		List<Person> searchResults = personRepo.searchPersons(null, offset, limit);

		
		String nav = "settings";
		String subNav = "application";
		renderTemplate("SettingTabs/applicationSettings.html",nav, subNav, degrees, proquestDegrees, actions, locations, packagers, depositors, reviewers, searchResults, offset, limit);
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
			List<String> textFields = new ArrayList<String>();
			textFields.add(CURRENT_SEMESTER);
			textFields.add(GRANTOR);
			textFields.add(SUBMIT_LICENSE_TEXT);
			textFields.add(PROQUEST_INSTITUTION_CODE);
			textFields.add(PROQUEST_INDEXING);
			textFields.add(PROQUEST_LICENSE_TEXT);

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
				
				if(SUBMIT_LICENSE_TEXT.equals(field)){
					Logger.info("%s (%d: %s) has updated license aggreement from '%s' to '%s'.",
							context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
							context.getPerson().getId(), 
							context.getPerson().getEmail(),
							oldValue,
							value);			
				}
				
				if(PROQUEST_LICENSE_TEXT.equals(field)){
					Logger.info("%s (%d: %s) has updated proquest license aggreement from '%s' to '%s'.",
							context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
							context.getPerson().getId(), 
							context.getPerson().getEmail(),
							oldValue,
							value);	
				}
				
			} else if (field.startsWith(DEGREE_CODE_PREFIX)) { 
				// This is a free form degree code.
				
				String oldValue = null;
				Configuration config = settingRepo.findConfigurationByName(field);
				
				if (config == null)
					config = settingRepo.createConfiguration(field, value);
				else {
					oldValue = config.getValue();
					config.setValue(value);
				}
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
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another custom action already exists with the name: '"+name+"'\" }");
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
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another custom action already exists with the name: '"+name+"'\" }");
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
	 * Return the list of all deposit settings. This is called often after state
	 * changes so that we can be sure this list is up-to-date always.
	 */
	@Security(RoleType.MANAGER)
	public static void updateDepositLocationList() {
		List<DepositLocation> locations = settingRepo.findAllDepositLocations();
		renderTemplate("SettingTabs/listDepositLocations.include",locations);
	}
	
	/**
	 * Load a deposit loation into the modal dialog box. We will attempt to test
	 * the connection and return proper results just as the save() method would.
	 * 
	 * @param depositLocationId
	 *            The id of the depositlocation, or null if creating a new
	 *            location.
	 */
	@Security(RoleType.MANAGER)
	public static void loadDepositLocation(String depositLocationId) {
		
		String name = null;
		Packager packager = null;
		Depositor depositor = null;
		String username = null;
		String password = null;
		String onBehalfOf = null;
		String repository = null;
		String collection = null;
		
		
		boolean connectionOk = false;
		Map<String,String> collectionsMap = null;
		if (depositLocationId != null) {
			String[] parts = depositLocationId.split("_");
			Long id = Long.valueOf(parts[1]);
			DepositLocation location = settingRepo.findDepositLocation(id);
			
			name = location.getName();
			packager = location.getPackager();
			depositor = location.getDepositor();
			username = location.getUsername();
			password = location.getPassword();
			onBehalfOf = location.getOnBehalfOf();
			repository = location.getRepository();
			collection = location.getCollection();
						
// Don't try to connect on inital load because it could take too long.
//			try {
//				collectionsMap = depositor.getCollections(location);
//				connectionOk = true;
//				
//				if (collectionsMap == null || collectionsMap.size() == 0)
//					validation.addError("collection","The repository is not able to provide a list of collections.");
//			} catch (DepositException de) {
//				
//				if (de.isField(FIELD.REPOSITORY)) {
//					validation.addError("repository",de.getMessage());
//				} else if (de.isField(FIELD.AUTHENTICATION)) {
//					validation.addError("auth",de.getMessage());
//				} else {
//					validation.addError("general","Unable to communicate with deposit location: "+de.getMessage());
//				}
//				
//			} catch (RuntimeException re) {
//				validation.addError("general","Unable to communicate with deposit location: "+re.getMessage());
//			}
		} 
		
		List<Packager> packagers = new ArrayList<Packager>( (Collection) Spring.getBeansOfType(Packager.class).values() );
		List<Depositor> depositors = new ArrayList<Depositor>( (Collection) Spring.getBeansOfType(Depositor.class).values() );
		
		String nav = "settings";
		String subNav = "deposit";
		renderTemplate("SettingTabs/editDepositLocation.include",nav, subNav, packagers, depositors, collectionsMap, connectionOk,
				
				// Deposit location
				depositLocationId, name, packager, depositor, username, password, onBehalfOf, repository, collection
				);
	}
	
	/**
	 * Save the deposit location.
	 * 
	 * If the basic sanity checks pass we will save the new state. Then we will
	 * test the connection against the actual deposit server reporting any
	 * errors found.
	 * 
	 * 
	 * @param depositLocationId
	 *            The id of the deposit location being edited, or null if this
	 *            is a new one being created.
	 */
	@Security(RoleType.MANAGER)
	public static void saveDepositLocation(String depositLocationId) {
		
		// Get our paramaters
		String action = params.get("action");
		String name = params.get("name");
		String packagerBeanName = params.get("packager");
		String depositorBeanName = params.get("depositor");
		String username = params.get("username");
		String password = params.get("password");
		String onBehalfOf = params.get("onBehalfOf");
		String repository = params.get("repository");
		String collection = params.get("collection");
				
		// Nullify things
		if (username != null && username.trim().length() == 0)
			username = null;
		if (password != null && password.length() == 0)
			password = null;
		if (onBehalfOf != null && onBehalfOf.trim().length() == 0)
			onBehalfOf = null;
		
		
		// Validation and format conversions
		if (name == null || name.trim().length() == 0)
			validation.addError("name", "A name is required");
		
		Packager packager = null;
		try {
			packager = (Packager) Spring.getBean(packagerBeanName);
		} catch (RuntimeException re) {
			validation.addError("packager", "The format is invalid.");
		}
		
		Depositor depositor = null;
		try {
			depositor = (Depositor) Spring.getBean(depositorBeanName);
		} catch (RuntimeException re) {
			validation.addError("depositor", "The protocol is invalid.");
		}
		
		if (repository == null || repository.trim().length() == 0)
			validation.addError("repository", "The repository location is required");
		
		// If no errors then try and save location
		DepositLocation location = null;
		if (!validation.hasErrors()) {
			try {
				// Save the values
				Boolean created = false;
				if (depositLocationId != null && depositLocationId.trim().length() > 0) {
					String[] parts = depositLocationId.split("_");
					Long id = Long.valueOf(parts[1]);
					location = settingRepo.findDepositLocation(id);
				} else {
					location = settingRepo.createDepositLocation(name);
					created = true;
					
					// Make sure the new location is last on the list.
					List<DepositLocation> locations = settingRepo.findAllDepositLocations();
					locations.add(location);
					saveModelOrder(locations);
				}
				
				// Check if we should update the password. We we display the
				// password we use blanks. So if it's the same number of blanks
				// leave the password alone.
				String originalPassword = location.getPassword();
				if (originalPassword != null && password != null) {					
					// Convert the original password to all spaces.
					originalPassword = originalPassword.replaceAll(".", " ");
					
					// Sometimes the browser gives us non breaking spaces, switch those out with normal ones.
					password = password.replaceAll("\u00A0"," ");
					
					if (!(originalPassword.length() == password.length() && password.trim().length() == 0)) {
						location.setPassword(password);
					}
				} else {
					location.setPassword(password);
				}
				
				location.setUsername(username);
				location.setOnBehalfOf(onBehalfOf);
				location.setRepository(repository);
				location.setCollection(collection);
				location.setPackager(packager);
				location.setDepositor(depositor);
				location.setName(name);
				location.save();
				
				Logger.info("%s (%d: %s) has %s deposit location #%d.\nDeposit Name = '%s'\nDeposit Packager = '%s'\nDeposit Depositor = '%s'\nDeposit Repository = '%s'\nDeposit Username = '%s'\nDeposit On Behalf Of = '%s'\nDeposit Collection = '%s'",
						context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
						context.getPerson().getId(), 
						context.getPerson().getEmail(),
						created ? "added" : "edited",
						location.getId(),
						location.getName(),
						location.getPackager()==null ? "null" : location.getPackager().getBeanName(),
						location.getDepositor()==null ? "null" : location.getDepositor().getBeanName(),
						location.getRepository(),
						location.getUsername(),
						location.getOnBehalfOf(),
						location.getCollection());
				
				depositLocationId = "depositLocation_"+location.getId();
			} catch (PersistenceException pe) {
				validation.addError("name", "Another deposit location already exists with the name.");
				
			} catch (RuntimeException re) {
				Logger.error(re,"Unable to save Deposit Location");
				validation.addError("general", "Unable to save form because: "+re.getMessage());
			}
		}

		
		// If there are still no errors, then try and get a list of all collections
		Map<String,String> collectionsMap = new HashMap<String,String>();
		boolean connectionOk = false;
		if (!validation.hasErrors() && location != null) {

			try {
				collectionsMap = depositor.getCollections(location);
				if (collectionsMap == null || collectionsMap.size() == 0)
					validation.addError("collection","The repository is not able to provide a list of collections.");
				
				connectionOk = true;
			} catch (DepositException de) {
				
				if (de.isField(FIELD.REPOSITORY)) {
					validation.addError("repository",de.getMessage());
				} else if (de.isField(FIELD.AUTHENTICATION)) {
					validation.addError("auth",de.getMessage());
				} else {
					validation.addError("general","Unable to communicate with deposit location: "+de.getMessage());
				}
				
			} catch (RuntimeException re) {
				validation.addError("general","Unable to communicate with deposit location: "+re.getMessage());
			}
			
			
			// Now at the end do a check to make sure the user has selected a collection url.
			if (!validation.hasErrors() && "depositLocation-save".equals(action) && collection == null) {
				validation.addError("collection","A collection is required.");
			}
				
		}

		// IF the user want's let's test a real submission.
		String testDepositId = null;
		if (!validation.hasErrors() && location != null && "depositLocation-test-submit".equals(action)) {
			
			try {
				testDepositId = submitTestItem(location);
			} catch (RuntimeException re) {
				Logger.warn(re,"Unable to submit test item.");
				validation.addError("general", "Unable to submit test item because: "+re.getMessage());
			}
			
		}		
		
		List<Packager> packagers = new ArrayList<Packager>( (Collection) Spring.getBeansOfType(Packager.class).values() );
		List<Depositor> depositors = new ArrayList<Depositor>( (Collection) Spring.getBeansOfType(Depositor.class).values() );
		
		if (depositLocationId != null && depositLocationId.trim().length() == 0)
			depositLocationId = null;
		
		renderTemplate("SettingTabs/editDepositLocation.include", packagers, depositors, collectionsMap, testDepositId, connectionOk, action,
			
			// Deposit location
			depositLocationId, name, packager, depositor, username, password, onBehalfOf, repository, collection);
	}

	/**
	 * Remove the specified deposit location.
	 * 
	 * @param depositLocationId
	 *            The id of the location to be removed.
	 */
	@Security(RoleType.MANAGER)
	public static void removeDepositLocationJSON(String depositLocationId) {
		
		try {
			// Delete the old template
			String[] parts = depositLocationId.split("_");
			Long id = Long.valueOf(parts[1]);
			DepositLocation location = settingRepo.findDepositLocation(id);
			location.delete();

			Logger.info("%s (%d: %s) has deleted deposit location #%d.\nDeposit Name = '%s'\nDeposit Packager = '%s'\nDeposit Depositor = '%s'\nDeposit Repository = '%s'\nDeposit Username = '%s'\nDeposit On Behalf Of = '%s'\nDeposit Collection = '%s'",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
					context.getPerson().getId(), 
					context.getPerson().getEmail(),
					location.getId(),
					location.getName(),
					location.getPackager()==null ? "null" : location.getPackager().getBeanName(),
					location.getDepositor()==null ? "null" : location.getDepositor().getBeanName(),
					location.getRepository(),
					location.getUsername(),
					location.getOnBehalfOf(),
					location.getCollection());
			
			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove deposit location");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Reorder the list of deposit locations.
	 * 
	 * @param depositLocationIds
	 *            A comma seperated list of deposit location ids.
	 */
	@Security(RoleType.MANAGER)
	public static void reorderDepositLocationsJSON(String depositLocationIds) {
		try {

			if (depositLocationIds != null && depositLocationIds.trim().length() > 0) {
				// Save the new order
				List<DepositLocation> locations = resolveIds(depositLocationIds, DepositLocation.class);
				saveModelOrder(locations);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder deposit locations");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
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
	
	/**
	 * An internal method to submit a test item to the repository location and
	 * check if it works.
	 * 
	 * @param location
	 *            The location to deposit too.
	 * @return The deposit id of the item submitted.
	 */
	protected static String submitTestItem(DepositLocation location) {
		
		Submission sub = subRepo.createSubmission(context.getPerson());
		try {
			sub.save();
			sub.setStudentFirstName("Test");
			sub.setStudentLastName("Submitter");
			sub.setDocumentTitle("Test Submission");
			sub.setDocumentAbstract("This is a sample submission generated by Vireo to test the repository deposit features.");
			sub.setDocumentKeywords("Test; Vireo; Repository Deposit");
			sub.setDegree("Test Degree");
			sub.setDegreeLevel(DegreeLevel.MASTERS);
			sub.setDepartment("Test Department");
			sub.setCollege("Test College");
			sub.setMajor("Test Major");
			sub.setDocumentType("Test Document Type");
			sub.setGraduationMonth(new Date().getMonth());
			sub.setGraduationYear(new Date().getYear()+1900);
			
			CommitteeMember member = sub.addCommitteeMember("Test", "Advisor", null).save();
			member.addRole("Chair");
			member.save();
			sub.addCommitteeMember("Test", "Member 1", null).save();
			sub.addCommitteeMember("Test", "Member 2", null).save();
	
			sub.save();
			
			depositService.deposit(location, sub, null, true);
						
			return sub.getDepositId();
		
		} finally {
			if (sub != null)
				sub.delete();
		}
	}
	
	
	
}
