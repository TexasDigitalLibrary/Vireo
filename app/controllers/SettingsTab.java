package controllers;

import org.tdl.vireo.model.RoleType;

import controllers.settings.ApplicationSettingsTab;
import controllers.settings.UserPreferencesTab;

import play.mvc.Controller;

public class SettingsTab extends Controller {

	@Security(RoleType.REVIEWER)
	public static void settingsRedirect() {
		UserPreferencesTab.userPreferences();
	}
	
	
}
