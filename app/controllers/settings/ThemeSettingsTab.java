package controllers.settings;

import static org.tdl.vireo.constant.AppConfig.*;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.RoleType;

import play.Logger;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

/**
 * Theme settings
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class ThemeSettingsTab extends SettingsTab {


	@Security(RoleType.MANAGER)
	public static void themeSettings() {
		
		renderArgs.put("FRONT_PAGE_INSTRUCTIONS", settingRepo.getConfigValue(FRONT_PAGE_INSTRUCTIONS));
		renderArgs.put("SUBMIT_INSTRUCTIONS", settingRepo.getConfigValue(SUBMIT_INSTRUCTIONS));
		renderArgs.put("CORRECTION_INSTRUCTIONS", settingRepo.getConfigValue(CORRECTION_INSTRUCTIONS));

		String nav = "settings";
		String subNav = "theme";
		renderTemplate("SettingTabs/themeSettings.html",nav, subNav);
	}
	
	
	@Security(RoleType.MANAGER)
	public static void updateThemeSettingsJSON(String field, String value) {

		try {
			List<String> booleanFields = new ArrayList<String>();
			// None at the moment but we expect some in the future.
			List<String> textFields = new ArrayList<String>();
			textFields.add(FRONT_PAGE_INSTRUCTIONS);
			textFields.add(SUBMIT_INSTRUCTIONS);
			textFields.add(CORRECTION_INSTRUCTIONS);

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
				Configuration config = settingRepo.findConfigurationByName(field);
				
				if (config == null)
					config = settingRepo.createConfiguration(field, value);
				else {
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

}
