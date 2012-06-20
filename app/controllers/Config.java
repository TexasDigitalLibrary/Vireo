package controllers;

import play.mvc.Controller;

public class Config extends Controller {

	
	public static void userPreferences(){
		String nav = "settings";
		String subNav = "user";
		render(nav, subNav);
	}
	
	public static void applicationSettings(){
		String nav = "settings";
		String subNav = "application";
		render(nav, subNav);
	}
	
	public static void emailSettings(){
		String nav = "settings";
		String subNav = "email";
		render(nav, subNav);
	}
	
	public static void configurableSettings(){
		String nav = "settings";
		String subNav = "config";
		render(nav, subNav);
	}
	
}
