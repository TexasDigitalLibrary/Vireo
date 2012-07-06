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

@With(Authentication.class)
public class SettingsTab extends AbstractVireoController {

	@Security(RoleType.REVIEWER)
	public static void settingsRedirect() {
		UserPreferencesTab.userPreferences();
	}
	
	
	@Security(RoleType.REVIEWER)
	public static void updateProfileJSON(String field, String value) {
		java.lang.System.out.println("updateProfileJSON('"+field+"','"+value+"')");
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
			
			String displayName = person.getDisplayName();
			String currentEmailAddress = person.getCurrentEmailAddress();
			
			person.save();
			java.lang.System.out.println("person.save()");

			renderJSON("{ \"success\": \"true\", \"displayName\": \""+displayName+"\", \"currentEmailAddress\": \""+currentEmailAddress+"\" }");
			
		} catch (AddressException ae) {
			renderJSON("{ \"failure\": \"true\", \"message\": \"The email address is not valid.\" }");
		} catch (RuntimeException re) {
			renderJSON("{ \"failure\": \"true\", \"message\": \""+re.getMessage()+"\" }");
		}
	}
	
}
