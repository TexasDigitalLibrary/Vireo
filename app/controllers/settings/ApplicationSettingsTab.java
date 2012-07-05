package controllers.settings;

import org.tdl.vireo.model.RoleType;

import controllers.Security;
import controllers.SettingsTab;

public class ApplicationSettingsTab extends SettingsTab {

	
	@Security(RoleType.MANAGER)
	public static void applicationSettings(){
		String nav = "settings";
		String subNav = "application";
		renderTemplate("SettingTabs/applicationSettings.html",nav, subNav);
	}
}
