package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.DefaultConfiguration;
import org.tdl.vireo.model.DefaultPreferences;

@Service
public class DefaultSettingsService {

    private static List<DefaultPreferences> defaultSettings;

    public DefaultSettingsService() {
        defaultSettings = new ArrayList<DefaultPreferences>();
    }

    public DefaultConfiguration getSettingByName(String field) {
        for (DefaultPreferences defaultPreferences : defaultSettings) {
            for (DefaultConfiguration defaultConfiguration : defaultPreferences.getPreferences()) {
                if (defaultConfiguration.getName().equals(field)) {
                    return defaultConfiguration;
                }
            }
        }
        return null;
    }

    public DefaultConfiguration getSettingByNameAndType(String field, String type) {
        return getSettingsByType(type).stream().filter(preference -> preference.getName().equals(field)).findFirst().orElse(null);
    }

    public List<String> getTypes() {
        List<String> types = new ArrayList<String>();
        defaultSettings.forEach(p -> {
            types.add(p.getType());
        });
        return types;
    }

    public List<DefaultConfiguration> getSettingsByType(String type) {
        Optional<DefaultPreferences> defaultConfiguration = defaultSettings.stream().filter(preferences -> preferences.getType().equals(type)).findFirst();
        return defaultConfiguration.isPresent() ? defaultConfiguration.get().getPreferences() : new ArrayList<DefaultConfiguration>();
    }

    public void addSettings(String type, List<DefaultConfiguration> preferences) {
        defaultSettings.add(new DefaultPreferences(type, preferences));
    }

}
