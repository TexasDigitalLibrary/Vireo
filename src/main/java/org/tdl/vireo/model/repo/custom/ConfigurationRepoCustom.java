package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.model.Configuration;

public interface ConfigurationRepoCustom {
    
    /**
     * Creates or updates existing configuration
     * 
     * Will create a non-isSystemRequired if it didn't already exist.
     * 
     * @param name
     * @param value
     * @param type
     * @return
     */
    public Configuration createOrUpdate(String name, String value, String type);
    
    /**
     * Resets existing configuration to its system value
     * 
     * 
     * @param name
     * @param value
     * @param type
     * @return
     */
    public Configuration reset(String name);
    
    /**
     * Get a list of all configurations
     * 
     * Will always pick a non-isSystemRequired if it exists
     * 
     * @return
     */
    public List<Configuration> getAll();
    
    /**
     * Gets a list of all the configurations by type
     * 
     * Will always pick a non-isSystemRequired if it exists
     * 
     * @param type
     * @return
     */
    public List<Configuration> getAllByType(String type);
    
    /**
     * Gets a Configuration from the repo
     * 
     * Will always pick a non-isSystemRequired if it exists.
     * @param name
     * @return
     */
    public Configuration getByName(String name);
    
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
}
