package org.tdl.vireo.model;

import java.util.Map;

public class DefaultPreferences extends AbstractPreferences {

    public DefaultPreferences(String type, Map<String, String> preferences) {
        super(type, preferences);
        preferences.forEach((k,v) -> {
            this.addAllowedKey(k);
        });
    }
}
