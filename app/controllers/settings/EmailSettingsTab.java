package controllers.settings;

import org.tdl.vireo.model.RoleType;

import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class EmailSettingsTab extends SettingsTab {


	@Security(RoleType.MANAGER)
	public static void emailSettings(){
		String nav = "settings";
		String subNav = "email";
		renderTemplate("SettingTabs/emailSettings.html",nav, subNav);
	}
	
}
