package org.tdl.vireo.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    // to avoid client side 404 errors and redirect put all AngularJs views in value
    @RequestMapping(value = { "/", "/home" })
    public ModelAndView view() {
        ModelAndView view = new ModelAndView("index");
        view.addObject("variable", "Hello, World!");
        return view;
    }
       
}
