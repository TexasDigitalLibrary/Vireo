package controllers.settings;

import org.tdl.vireo.model.RoleType;

import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class UserPreferencesTab extends SettingsTab {

	@Security(RoleType.REVIEWER)
	public static void userPreferences(){
		String nav = "settings";
		String subNav = "user";
		renderTemplate("SettingTabs/userPreferences.html",nav, subNav);
	}
	
}
