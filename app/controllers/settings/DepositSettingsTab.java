package controllers.settings;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.tdl.vireo.deposit.DepositService;
import org.tdl.vireo.deposit.Depositor;
import org.tdl.vireo.deposit.Packager;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;

import play.Logger;
import play.modules.spring.Spring;
import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;
import static org.tdl.vireo.model.Configuration.*;

/**
 * Deposit settings tab
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class DepositSettingsTab extends SettingsTab {
	
	// Used for testing deposits
	public static DepositService depositService = Spring.getBeanOfType(DepositService.class);
	
	/**
	 * Display the deposit settings page.
	 */
	@Security(RoleType.MANAGER)
	public static void depositSettings() {
		
		List<DepositLocation> locations = settingRepo.findAllDepositLocations();
		
		List<Packager> packagers = new ArrayList<Packager>( (Collection) Spring.getBeansOfType(Packager.class).values() );
		List<Depositor> depositors = new ArrayList<Depositor>( (Collection) Spring.getBeansOfType(Depositor.class).values() );
		
		String nav = "settings";
		String subNav = "deposit";
		renderTemplate("SettingTabs/depositSettings.html",nav, subNav, locations, packagers, depositors);
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
		URL repositoryURL = null;
		URL collectionURL = null;
		
		
		boolean connectionOk = false;
		Map<String,URL> collectionsMap = null;
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
			repositoryURL = location.getRepositoryURL();
			collectionURL = location.getCollectionURL();
			
			try {
				collectionsMap = depositor.getCollections(location);
				connectionOk = true;
				
				if (collectionsMap == null || collectionsMap.size() == 0)
					validation.addError("collectionURL","The repository is not able to provide a list of collections.");
			} catch (RuntimeException re) {
				if (re.getMessage().contains("Code: 401")) {
					validation.addError("auth","Unauthorized credentials");
				} else if (re.getMessage().contains("Connection refused")) {
					validation.addError("repositoryURL","Connection refused");
				} else {
					validation.addError("general","Unable to communicate with this remote repository: "+re.getMessage());
				}
			}
		} 
		
		List<Packager> packagers = new ArrayList<Packager>( (Collection) Spring.getBeansOfType(Packager.class).values() );
		List<Depositor> depositors = new ArrayList<Depositor>( (Collection) Spring.getBeansOfType(Depositor.class).values() );
		
		String nav = "settings";
		String subNav = "deposit";
		renderTemplate("SettingTabs/editDepositLocation.include",nav, subNav, packagers, depositors, collectionsMap, connectionOk,
				
				// Deposit location
				depositLocationId, name, packager, depositor, username, password, onBehalfOf, repositoryURL, collectionURL
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
		String repositoryURLString = params.get("repositoryURL");
		String collectionURLString = params.get("collectionURL");
				
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
		
		URL repositoryURL = null;
		try {
			if (repositoryURLString == null || repositoryURLString.trim().length() == 0)
				validation.addError("repositoryURL", "The repository URL is required");
			else
				repositoryURL = new URL(repositoryURLString);
		} catch (MalformedURLException e) {
			validation.addError("repositoryURL", "The repositor URL is invalid");
		}
		
		URL collectionURL = null;
		try {
			if (collectionURLString != null && collectionURLString.trim().length() > 0)
				collectionURL = new URL(collectionURLString);
		} catch (MalformedURLException e) {
			validation.addError("collectionURL", "The collection URL is invalid");
		}
		
		// If no errors then try and save location
		DepositLocation location = null;
		if (!validation.hasErrors()) {
			try {
				// Save the values
				if (depositLocationId != null && depositLocationId.trim().length() > 0) {
					String[] parts = depositLocationId.split("_");
					Long id = Long.valueOf(parts[1]);
					location = settingRepo.findDepositLocation(id);
				} else {
					location = settingRepo.createDepositLocation(name);
					
					// Make sure the new location is last on the list.
					List<DepositLocation> locations = settingRepo.findAllDepositLocations();
					locations.add(location);
					saveModelOrder(locations);
				}
				
				// Check if we should update the password. We we display the
				// password we use blanks. So if it's the same number of blanks
				// leave the password alone.
				String originalPassword = location.getPassword();
				if (originalPassword != null) {					
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
				location.setRepositoryURL(repositoryURL);
				location.setCollectionURL(collectionURL);
				location.setPackager(packager);
				location.setDepositor(depositor);
				location.setName(name);
				location.save();
				
				depositLocationId = "depositLocation_"+location.getId();
			} catch (RuntimeException re) {
				Logger.error(re,"Unable to save Deposit Location");
				validation.addError("general", "Unable to save form because: "+re.getMessage());
			}
		}

		
		// If there are still no errors, then try and get a list of all collections
		Map<String,URL> collectionsMap = new HashMap<String,URL>();
		boolean connectionOk = false;
		if (!validation.hasErrors() && location != null) {

			try {
				collectionsMap = depositor.getCollections(location);
				if (collectionsMap == null || collectionsMap.size() == 0)
					validation.addError("collectionURL","The repository is not able to provide a list of collections.");
				
				connectionOk = true;
			} catch (RuntimeException re) {
				
				if (re.getMessage().contains("Code: 401")) {
					validation.addError("auth","Unauthorized credentials");
				} else if (re.getMessage().contains("Connection refused")) {
					validation.addError("repositoryURL","Connection refused");
				} else {
					validation.addError("general","Unable to communicate with this remote repository: "+re.getMessage());
				}
			}
			
			
			// Now at the end do a check to make sure the user has selected a collection url.
			if (!validation.hasErrors() && "depositLocation-save".equals(action) && collectionURL == null) {
				validation.addError("collectionURL","A collection is required.");
			}
				
		}

		// IF the user want's let's test a real submission.
		String testDepositId = null;
		if (!validation.hasErrors() && location != null && "depositLocation-test-submit".equals(action)) {
			
			try {
				testDepositId = submitTestItem(location);
			} catch (RuntimeException re) {
				validation.addError("general", "Unable to submit test item because: "+re.getMessage());
			}
			
		}		
		
		List<Packager> packagers = new ArrayList<Packager>( (Collection) Spring.getBeansOfType(Packager.class).values() );
		List<Depositor> depositors = new ArrayList<Depositor>( (Collection) Spring.getBeansOfType(Depositor.class).values() );
		
		if (depositLocationId != null && depositLocationId.trim().length() == 0)
			depositLocationId = null;
		
		renderTemplate("SettingTabs/editDepositLocation.include", packagers, depositors, collectionsMap, testDepositId, connectionOk, action,
			
			// Deposit location
			depositLocationId, name, packager, depositor, username, password, onBehalfOf, repositoryURL, collectionURL);
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
			
			sub.addCommitteeMember("Test", "Advisor", null, true).save();
			sub.addCommitteeMember("Test", "Member 1", null, false).save();
			sub.addCommitteeMember("Test", "Member 2", null, false).save();
	
			sub.save();
			
			depositService.deposit(location, sub, null, true);
						
			return sub.getDepositId();
		
		} finally {
			if (sub != null)
				sub.delete();
		}
	}
	
}
