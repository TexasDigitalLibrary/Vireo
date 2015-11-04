package org.tdl.vireo.controller.ui;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class ViewController {
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Value("${app.ui.base}")
    private String base;
    
    @Value("${app.ui.path}")
    private String uiPath;
    
    @RequestMapping("${app.ui.base}")
    public String index() {
        return "forward:/index.html";
    }
    
    @RequestMapping("${app.ui.base}/**")
    public String ui(HttpServletRequest request) throws IOException {
        
        String path = ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).replace(base, uiPath);

        Resource resource = resourceLoader.getResource("classpath:" + path);

        if(resource.exists()) {
            return "forward:/" + path;
        } 

        return "forward:/index.html";
    }
    
}
