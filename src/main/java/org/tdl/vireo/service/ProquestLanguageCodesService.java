package org.tdl.vireo.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProquestLanguageCodesService {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    private Map<String, String> languageCodes;
    
    public ProquestLanguageCodesService() {
        languageCodes = new HashMap<String, String>();
    }
    
    public void setLanguageCodes(Map<String, String> languageCodes) {
        this.languageCodes = languageCodes;
    }
    
    public Map<String, String> getLanguageCodes() {
        return languageCodes;
    }
    
    public void addLanguageCode(String langauge, String code) {
        languageCodes.put(langauge, code);
    }
    
    public void removeLanguageCode(String langauge) {
        languageCodes.remove(langauge);
    }
    
    public void clearLanguageCodes() {
        languageCodes.clear();
    }
    
}
