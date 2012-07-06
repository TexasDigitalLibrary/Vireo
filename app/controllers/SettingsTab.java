package controllers;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.mozilla.javascript.Context;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;

import controllers.settings.ApplicationSettingsTab;
import controllers.settings.UserPreferencesTab;

import play.mvc.Controller;
import play.mvc.With;

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
			} else if ("currentEmailAddress".equals(field)) {
				if (value == null || value.trim().length() == 0)
					throw new IllegalArgumentException("An email address is required.");
				
				new InternetAddress(value).validate();
				person.setCurrentEmailAddress(value);
			} else if ("ccEmail".equals(field)) {
				
				Preference ccEmail = person.getPreference("ccEmail");
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
			renderJSON("{ \"success\": \"true\", \"displayName\": \""+displayName+"\", \"currentEmailAddress\": \""+currentEmailAddress+"\" }");
			
		} catch (AddressException ae) {
			renderJSON("{ \"failure\": \"true\", \"message\": \"The email address is not valid. It should be formatted like 'your-username@your-domain' without any spaces and includes one @ sign.\" }");
			
		} catch (RuntimeException re) {
			renderJSON("{ \"failure\": \"true\", \"message\": \""+re.getMessage()+"\" }");
			
		}
	}
	
}
