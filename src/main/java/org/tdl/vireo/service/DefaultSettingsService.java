package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.DefaultPreferences;

@Service
public class DefaultSettingsService {
    private List<DefaultPreferences> defaultSettings;
    
    public DefaultSettingsService() {
        defaultSettings = new ArrayList<DefaultPreferences>();
    }
    
    public String getSetting(String field,String type) {
        Map<String,String> preferencesOfType = getSettingsByType(type);
        return preferencesOfType.get(field);
    }
    
    public List<String> getTypes() {
        List<String> types = new ArrayList<String>();
        defaultSettings.forEach(p -> {
            types.add(p.getType());
        });
        return types;
    }
    
    public Map<String,String> getSettingsByType(String type) {
        DefaultPreferences preferencesOfType = defaultSettings.stream().filter(preferences -> preferences.getType() == type).findFirst().orElse(null);
        return new HashMap<String,String>(preferencesOfType.getPreferences());
    }
    
    public List<String> getAllowedKeysByType(String type) {
        DefaultPreferences allowedKeysOfType = defaultSettings.stream().filter(preferences -> preferences.getType() == type).findFirst().orElse(null);
        return allowedKeysOfType.getAllowedKeys();
    }
    
    public void addSettings(String type, Map<String,String> preferences) {
        defaultSettings.add(new DefaultPreferences(type,preferences));
    }
}
