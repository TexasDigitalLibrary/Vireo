package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPreferences {
    protected ArrayList<String> allowedKeys;
    protected List<DefaultConfiguration> preferences;
    protected String type;
    
    public AbstractPreferences(String type, List<DefaultConfiguration> preferences) {
        setType(type);
        setPreferences(preferences);
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setPreferences(List<DefaultConfiguration> preferences) {
        this.preferences = preferences;
    }
        
    public List<DefaultConfiguration> getPreferences() {
        return this.preferences;
    }
    
    public String getType() {
        return this.type;
    }

}
