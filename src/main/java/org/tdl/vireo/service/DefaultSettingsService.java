package org.tdl.vireo.service;

import java.util.HashMap;
import java.util.Map;

import org.tdl.vireo.model.DefaultPreference;

public class DefaultSettingsService {
    private Map<String,Map<String,DefaultPreference>> defaultSettings;
    
    public DefaultSettingsService() {
        Map<String,DefaultPreference> dummyPreferences = new HashMap<String,DefaultPreference>();
        dummyPreferences.put("headerColor", new DefaultPreference("headerColor","#500000","global"));
        
        
        defaultSettings.put(dummyPreferences.get(0).getType(),dummyPreferences);
    }
    
    public DefaultPreference getSetting(String field,String type) {
        return defaultSettings.get(type).get(field);
    }
}
