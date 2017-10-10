package org.tdl.vireo.controller.ui;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    @RequestMapping("/")
    public ModelAndView view(HttpServletRequest request) {
        return index(request);
    }

    public static ModelAndView index(HttpServletRequest request) {
		ModelAndView index = new ModelAndView("index");
		index.addObject("base", request.getServletContext().getContextPath());
        return index;
	}

}
