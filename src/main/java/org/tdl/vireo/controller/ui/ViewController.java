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
    HttpServletRequest request;
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @RequestMapping("${app.ui.base}")
    public String index() {
        return "forward:/index.html";
    }
    
    @Value("${app.ui.base}")
    private String base;
    
    @Value("${app.ui.path}")
    private String uiPath;
     
    @RequestMapping("${app.ui.base}**")
    public String ui() throws IOException {
        
        String reqPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String path = reqPath.substring(base.length(), reqPath.length());
                        
        Resource resource = resourceLoader.getResource("classpath:"+uiPath+"/"+path);
        if(resource.exists()) {
            return "forward:/"+path;
        } 
        
        return "forward:/index.html";
    }
    
}
