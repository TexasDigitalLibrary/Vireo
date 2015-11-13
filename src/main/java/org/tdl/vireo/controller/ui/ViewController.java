package org.tdl.vireo.controller.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    // to avoid client side 404 errors and redirect put all AngularJs views in value
    @RequestMapping(value = { "", "/", "/home" })
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("index");
        view.addObject("base", request.getServletContext().getContextPath());
        
        view.addObject("message", "Hello, World!");
        
        return view;
    }
       
}
