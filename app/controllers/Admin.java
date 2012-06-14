package controllers;

import play.mvc.Controller;

public class Admin extends Controller {

	public static void list(){
		String nav = "list";
		render(nav);
	}
	
	public static void view(){
		String nav = "view";
		render(nav);
	}
	
	public static void log(){
		String nav = "log";
		render(nav);
	}
	
	public static void settings(){
		String nav = "settings";
		render(nav);
	}
	
}
