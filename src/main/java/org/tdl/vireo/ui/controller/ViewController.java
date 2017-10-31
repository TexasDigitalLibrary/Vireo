package org.tdl.vireo.ui.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

// TODO: this most likely can move into the framework
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
