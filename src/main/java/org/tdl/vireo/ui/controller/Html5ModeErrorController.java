package org.tdl.vireo.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

// TODO: this most likely can move into the framework
@RestController
public class Html5ModeErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Override
    public String getErrorPath() {
        return PATH;
    }

    @RequestMapping(value = PATH)
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("X-Requested-With") == null) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        return ViewController.index(request);
    }

}
