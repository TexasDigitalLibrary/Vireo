package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.DefaultConfiguration;
import org.tdl.vireo.model.DefaultPreferences;

@Service
public class DefaultSettingsService {
    private List<DefaultPreferences> defaultSettings;
    
    public DefaultSettingsService() {
        defaultSettings = new ArrayList<DefaultPreferences>();
    }
    
    public String getSetting(String field,String type) {
        return getSettingsByType(type).stream().filter(preference -> preference.getName() == field).findFirst().orElse(null).getValue();
    }
    
    public List<String> getTypes() {
        List<String> types = new ArrayList<String>();
        defaultSettings.forEach(p -> {
            types.add(p.getType());
        });
        return types;
    }
    
    public List<DefaultConfiguration> getSettingsByType(String type) {
        return defaultSettings.stream().filter(preferences -> preferences.getType() == type).findFirst().orElse(null).getPreferences();
    }
    
    public void addSettings(String type, List<DefaultConfiguration> preferences) {
        defaultSettings.add(new DefaultPreferences(type,preferences));
    }
}
