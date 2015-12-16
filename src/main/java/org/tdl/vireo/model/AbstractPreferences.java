package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.Map;

public abstract class AbstractPreferences {
    protected ArrayList<String> allowedKeys;
    protected Map<String,String> preferences;
    protected String type;
    
    public AbstractPreferences(String type, Map<String, String> preferences) {
        setType(type);
        setPreferences(preferences);
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }
        
    public Map<String, String> getPreferences() {
        return this.preferences;
    }
    
    public String getType() {
        return this.type;
    }

}
