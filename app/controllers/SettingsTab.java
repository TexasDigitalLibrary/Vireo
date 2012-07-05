package controllers;

import org.tdl.vireo.model.RoleType;

import controllers.settings.ApplicationSettingsTab;
import controllers.settings.UserPreferencesTab;

import play.mvc.Controller;
import play.mvc.With;

@With(Authentication.class)
public class SettingsTab extends Controller {

	@Security(RoleType.REVIEWER)
	public static void settingsRedirect() {
		UserPreferencesTab.userPreferences();
	}
	
	
}
