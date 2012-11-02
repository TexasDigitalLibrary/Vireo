package controllers.settings;

import static org.tdl.vireo.constant.AppPref.*;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;

import play.Logger;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

/**
 * User preference tab.
 * 
 * The user can select their personal administrative options. At the present
 * time that includes just email options indicating what the default values are
 * on the view tab.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
@With(Authentication.class)
public class UserPreferencesTab extends SettingsTab {
	
	/**
	 * Display the userPreferences page
	 */
	@Security(RoleType.REVIEWER)
	public static void userPreferences(){
		
		
		Person person = context.getPerson();
		renderArgs.put("NOTES_EMAIL_STUDENT", person.getPreference(NOTES_EMAIL_STUDENT));
		renderArgs.put("NOTES_CC_ADVISOR", person.getPreference(NOTES_CC_ADVISOR));
		renderArgs.put("NOTES_FLAG_NEEDS_CORRECTIONS", person.getPreference(NOTES_FLAG_NEEDS_CORRECTIONS));
		renderArgs.put("NOTES_MARK_PRIVATE", person.getPreference(NOTES_MARK_PRIVATE));

		renderArgs.put("ATTACHMENT_EMAIL_STUDENT", person.getPreference(ATTACHMENT_EMAIL_STUDENT));
		renderArgs.put("ATTACHMENT_CC_ADVISOR", person.getPreference(ATTACHMENT_CC_ADVISOR));
		renderArgs.put("ATTACHMENT_FLAG_NEEDS_CORRECTIONS", person.getPreference(ATTACHMENT_FLAG_NEEDS_CORRECTIONS));
		
		String nav = "settings";
		String subNav = "user";
		renderTemplate("SettingTabs/userPreferences.html",nav, subNav);
	}
	
	/**
	 * Receive updates for preferences via AJAX.
	 * 
	 * @param field
	 *            The field being updated.
	 * @param value
	 *            The value (either something or null)
	 */
	@Security(RoleType.REVIEWER)
	public static void updateUserPreferencesJSON(String field, String value) {
		
		try {
			boolean booleanValue = true;
			if (value == null || value.trim().length() == 0)
				booleanValue = false;
			
			
			List<String> editableFields = new ArrayList<String>();
			editableFields.add(NOTES_EMAIL_STUDENT);
			editableFields.add(NOTES_CC_ADVISOR);
			editableFields.add(NOTES_FLAG_NEEDS_CORRECTIONS);
			editableFields.add(NOTES_MARK_PRIVATE);
				
			editableFields.add(ATTACHMENT_EMAIL_STUDENT);
			editableFields.add(ATTACHMENT_CC_ADVISOR);
			editableFields.add(ATTACHMENT_FLAG_NEEDS_CORRECTIONS);
			
			if (!editableFields.contains(field))
				throw new IllegalArgumentException("Unknown field '"+field+"'");
			
			Person person = context.getPerson();
			Preference preference = person.getPreference(field);
			
			if (!booleanValue && preference != null)
				preference.delete();
			else if (booleanValue && preference == null)
				person.addPreference(field, "true").save();
			
			person.save();
			renderJSON("{ \"success\": \"true\" }");

		} catch (RuntimeException re) {
			Logger.error(re,"Unable to update user preferences");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}
}
