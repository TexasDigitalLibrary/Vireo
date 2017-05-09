package org.tdl.vireo.controller.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    @RequestMapping("/")
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("index");
        view.addObject("base", request.getServletContext().getContextPath());
        if (request.getHeader("X-Requested-With") == null) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        return view;
    }

}
