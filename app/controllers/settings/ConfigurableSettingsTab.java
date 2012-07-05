package controllers.settings;

import org.tdl.vireo.model.RoleType;

import controllers.Security;
import controllers.SettingsTab;

public class ConfigurableSettingsTab extends SettingsTab {
	
	@Security(RoleType.MANAGER)
	public static void configurableSettings(){
		String nav = "settings";
		String subNav = "config";
		renderTemplate("SettingTabs/configurableSettings.html",nav, subNav);
	}
	
}
