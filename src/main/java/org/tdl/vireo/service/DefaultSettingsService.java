package org.tdl.vireo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tdl.vireo.model.DefaultPreferences;

public class DefaultSettingsService {
    private List<DefaultPreferences> defaultSettings;
    
    public DefaultSettingsService() {
        Map<String,String> temp = new HashMap<String,String>();
        temp.put("headerColor","#500000");
        DefaultPreferences dummyPreferences = new DefaultPreferences("global",temp);
          
        defaultSettings.add(dummyPreferences);
    }
    
    public String getSetting(String field,String type) {
        Map<String,String> preferencesOfType = getSettingsByType(type);
        return preferencesOfType.get(field);
    }
    
    public Map<String,String> getSettingsByType(String type) {
        DefaultPreferences preferencesOfType = defaultSettings.stream().filter(preferences -> preferences.getType() == type).findFirst().orElse(null);
        return preferencesOfType.getPreferences();
    }

}
