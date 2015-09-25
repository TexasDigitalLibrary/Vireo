package org.tdl.vireo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
	
	@RequestMapping("/")
	public ModelAndView index(@RequestParam(value = "name", defaultValue = "World") String name) {
	    ModelAndView view = new ModelAndView();
	    view.setViewName("index");
		view.addObject("name", name);
		return view;
	}
	
}
