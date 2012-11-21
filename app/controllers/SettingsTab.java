package controllers;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.constant.AppPref;
import org.tdl.vireo.model.AbstractOrderedModel;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.RoleType;

import play.Logger;
import play.mvc.With;
import controllers.settings.UserPreferencesTab;

/**
 * Parent class for all setting tab controllers.
 * 
 * This class shares code between the several children controllers, as well as
 * handles the ajax updates for the profile information since they are on every
 * page.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class SettingsTab extends AbstractVireoController {

	/**
	 * Redirect to the user's preference tab.
	 */
	@Security(RoleType.REVIEWER)
	public static void settingsRedirect() {
		UserPreferencesTab.userPreferences();
	}

	/**
	 * Update a user's admin profile information. This includes three things.
	 * The user's display name, their preferred email address, and a flag to be
	 * cc'ed anytime something is emailed.
	 * 
	 * @param field
	 *            The name of the field either: displayName,
	 *            currentEmailAddress, or ccEmail.
	 * @param value
	 *            The new value of the field.
	 */
	@Security(RoleType.REVIEWER)
	public static void updateProfileJSON(String field, String value) {
		try {
			if (value != null && value.trim().length() == 0)
				value = null;
			
			Person person = context.getPerson();
			
			if ("displayName".equals(field)) {
				person.setDisplayName(value);
				
				Logger.info("%s (%d: %s) has changed their display name to '%s'.",
						context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
						context.getPerson().getId(), 
						context.getPerson().getEmail(),
						value);
				
			} else if ("currentEmailAddress".equals(field)) {
				if (value == null || value.trim().length() == 0)
					throw new IllegalArgumentException("An email address is required.");
				
				new InternetAddress(value).validate();
				person.setCurrentEmailAddress(value);
			} else if ("ccEmail".equals(field)) {
				
				Preference ccEmail = person.getPreference(AppPref.CC_EMAILS);
				if (value == null && ccEmail != null) {
					// remove the setting
					ccEmail.delete();
				} else if (value != null && ccEmail == null) {
					person.addPreference("ccEmail", "true").save();
				}
			} else {
				throw new IllegalArgumentException("Unknown field type.");
			}
			
			person.save();
			
			String displayName = person.getDisplayName();
			String currentEmailAddress = person.getCurrentEmailAddress();
			
			displayName = escapeJavaScript(displayName);
			currentEmailAddress = escapeJavaScript(currentEmailAddress);
						
			renderJSON("{ \"success\": \"true\", \"displayName\": \""+displayName+"\", \"currentEmailAddress\": \""+currentEmailAddress+"\" }");
			
		} catch (AddressException ae) {
			renderJSON("{ \"failure\": \"true\", \"message\": \"The email address is not valid. It should be formatted like 'your-username@your-domain' without any spaces and includes one @ sign.\" }");
			
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		}
	}
	
	/**
	 * Internal method to persist the current ordering of the model.
	 * 
	 * Each item in the list will have it's display order updated according to
	 * it's position in the provided list. Then the item will be saved() to
	 * persistant storage.
	 * 
	 * @param models
	 *            A List of ordered models.
	 */
	protected static void saveModelOrder(
			List<? extends AbstractOrderedModel> models) {
		int i = 0;
		for (AbstractOrderedModel model : models) {
			model.setDisplayOrder(i);
			model.save();
			i = i + 10;
		}
	}
	
	/**
	 * Internal method to resolve the ids from a sortable element.
	 * 
	 * The jQuery sortable library expects all ids to be of the format: type_id.
	 * This method will process a comma sepeareted list of these jquery-based
	 * ids, resolving them into their actual model objects according to the ids
	 * position within the idString.
	 * 
	 * 
	 * @param idString
	 *            Comma seperated list of ids and labels: i.e. action_1,
	 *            action_2, etc...
	 * @param type
	 *            The type of model.
	 * @return A list of models
	 */
	protected static <T extends AbstractOrderedModel> List<T> resolveIds(String idString, Class<T> type) {
		
		List<T> models = new ArrayList<T>();
		
		String[] idArray = idString.split(",");
		for (String idElement : idArray) {
			
			String[] parts = idElement.split("_");
			
			Long id = Long.valueOf(parts[1]);
			
			if (type.equals(CustomActionDefinition.class)){
				CustomActionDefinition action = settingRepo.findCustomActionDefinition(id);
				models.add((T) action);	
				
			} else if (type.equals(College.class)) {
				College college = settingRepo.findCollege(id);
				models.add((T) college);
			
			} else if (type.equals(Program.class)) {
				Program program = settingRepo.findProgram(id);
				models.add((T) program);
				
			} else if (type.equals(Department.class)) {
				Department department = settingRepo.findDepartment(id);
				models.add((T) department);
				
			} else if (type.equals(Major.class)) {
				Major major = settingRepo.findMajor(id);
				models.add((T) major);
				
			} else if (type.equals(Degree.class)) {
				Degree degree = settingRepo.findDegree(id);
				models.add((T) degree);
				
			} else if (type.equals(DocumentType.class)) {
				DocumentType docType = settingRepo.findDocumentType(id);
				models.add((T) docType);
				
			} else if (type.equals(GraduationMonth.class)) {
				GraduationMonth month = settingRepo.findGraduationMonth(id);
				models.add((T) month);
				
			} else if (type.equals(EmailTemplate.class)) {
				EmailTemplate template = settingRepo.findEmailTemplate(id);
				models.add((T) template);
				
			} else if (type.equals(EmbargoType.class)) {
				EmbargoType embargo = settingRepo.findEmbargoType(id);
				models.add((T) embargo);
				
			} else if (type.equals(DepositLocation.class)) {
				DepositLocation location = settingRepo.findDepositLocation(id);
				models.add((T) location);
			
			} else if (type.equals(Language.class)) {
				Language language = settingRepo.findLanguage(id);
				models.add((T) language);
			} else {
				throw new IllegalArgumentException("Unknown model type: "+type.getName());
			}
		}
		
		return models;
	}
	
	
	/**
	 * Internal method to translate the name of a month into it's integer value.
	 * 
	 * If the month name is invalid then a user displayable message is thrown as
	 * an IllegalArgumentException.
	 * 
	 * @param monthName
	 *            The name of a month in the default lanugage.
	 * @return The integer value of the month, january=0, december=11.
	 */
	protected static int monthNameToInt(String monthName) {
		
		if (monthName == null || monthName.trim().length() == 0)
			throw new IllegalArgumentException("monthName is required.");
		
		monthName = monthName.toLowerCase();
		
		String[] months = new DateFormatSymbols().getMonths();
		for (int i =0; i <months.length; i++) {
			
			if (monthName.equalsIgnoreCase(months[i]))
				return i;
		}
		
		throw new IllegalArgumentException("The month '"+monthName+"' is invalid, month names should be spelled out completely such as \"January\", \"Feburary\", etc...");
	}
	
}
