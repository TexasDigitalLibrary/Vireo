package org.tdl.vireo.controller.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    @RequestMapping(value = { "", "/", "/home", "/register" })
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("WEB-INF/app/index.jsp");
        view.addObject("base", request.getServletContext().getContextPath());
        return view;
    }
       
}
