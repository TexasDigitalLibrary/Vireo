package controllers;

import play.mvc.Controller;

public class Settings extends Controller {

	
	public static void settings(){
		String nav = "settings";
		render(nav);
	}
	
}
