package org.tdl.vireo.model.repo.custom;

import java.util.Map;

import org.tdl.vireo.model.Configuration;

public interface ConfigurationRepoCustom {
    public Configuration create(String name, String value);

    public Configuration create(String name, String value, String type);
    
    /**
     * Gets a String value from the configuration repo.
     * 
     * If it's not found in DB, a fallback value is used instead.
     * 
     * @param name
     * @param fallback
     * @return config value or fallback
     */
    public String getValue(String name, String fallback);
    
    /**
     * Gets an Integer value from the configuration repo.
     * 
     * If it's not found in DB, a fallback value is used instead.
     * 
     * @param name
     * @param fallback
     * @return config value or fallback
     */
    public Integer getValue(String name, Integer fallback);
    
    /**
     * Gets a String value from the configuration repo.
     * 
     * 
     * @param name
     * @param type
     * @return config value
     */
    public String getValueByNameAndType(String name,String type);
    
    public Map<String,String> getAllByType(String type);
}
