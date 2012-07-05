package controllers.settings;

import org.tdl.vireo.model.RoleType;

import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class ConfigurableSettingsTab extends SettingsTab {
	
	@Security(RoleType.MANAGER)
	public static void configurableSettings(){
		String nav = "settings";
		String subNav = "config";
		renderTemplate("SettingTabs/configurableSettings.html",nav, subNav);
	}
	
}
