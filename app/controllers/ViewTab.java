package controllers;

import play.mvc.Controller;

public class ViewTab extends Controller {

	
	public static void view() {
		String nav = "view";
		render(nav);
	}

}
