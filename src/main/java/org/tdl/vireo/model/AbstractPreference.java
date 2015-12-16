package org.tdl.vireo.model;

import java.util.ArrayList;

public abstract class AbstractPreference {
    protected ArrayList<String> allowedKeys;
    protected String key;
    protected String value;
    protected String type;
    
    public AbstractPreference(String key, String value, String type) {
        setKey(key);
        setValue(value);
        setType(type);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }
        
    public String getValue() {
        return this.value;
    }
    
    public String getType() {
        return this.type;
    }

}
