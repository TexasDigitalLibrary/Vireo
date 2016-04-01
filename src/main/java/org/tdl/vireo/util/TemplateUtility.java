package org.tdl.vireo.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TemplateUtility {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    public String templateParameters(String content, Map<String, String> parameters) {
        for (String name : parameters.keySet()) {
            content = content.replaceAll("\\{"+name+"\\}", parameters.get(name));
        }
        return content;
    }
    
    public String templateParameters(String content, String[][] parameters) {
        for(String[] parameter : parameters) {
            content = content.replaceAll("\\{"+parameter[0]+"\\}", parameter[1]);
        }
        return content;
    }
    
}
