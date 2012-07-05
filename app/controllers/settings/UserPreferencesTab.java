package controllers.settings;

import org.tdl.vireo.model.RoleType;

import controllers.Security;
import controllers.SettingsTab;

public class UserPreferencesTab extends SettingsTab {

	@Security(RoleType.REVIEWER)
	public static void userPreferences(){
		String nav = "settings";
		String subNav = "user";
		renderTemplate("SettingTabs/userPreferences.html",nav, subNav);
	}
	
}
