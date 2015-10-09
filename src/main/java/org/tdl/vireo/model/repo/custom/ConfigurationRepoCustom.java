package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Configuration;

public interface ConfigurationRepoCustom {
    public Configuration create(String name, String value);
    
    /**
     * Gets a value from the configuration repo.
     * if it's not found in DB, a fallback value is used instead.
     * 
     * @param name
     * @param fallback
     * @return config value or fallback
     */
    public String getValue(String name, String fallback);
}
