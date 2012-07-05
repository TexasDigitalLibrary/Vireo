package controllers.settings;

import org.tdl.vireo.model.RoleType;

import controllers.Security;
import controllers.SettingsTab;

public class EmailSettingsTab extends SettingsTab {


	@Security(RoleType.MANAGER)
	public static void emailSettings(){
		String nav = "settings";
		String subNav = "email";
		renderTemplate("SettingTabs/emailSettings.html",nav, subNav);
	}
	
}
