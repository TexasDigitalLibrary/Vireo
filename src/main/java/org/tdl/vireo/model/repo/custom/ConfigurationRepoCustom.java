package org.tdl.vireo.model.repo.custom;

import java.util.List;
import java.util.Map;

import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.interfaces.Configuration;

public interface ConfigurationRepoCustom {

    /**
     * Creates a configuration
     * 
     * Will create a non-isSystemRequired
     * 
     * @param name
     * @param value
     * @param type
     * @return
     */
    public ManagedConfiguration create(String name, String value, String type);

    /**
     * Resets existing configuration to its system value
     * 
     * 
     * @param configuration
     * @return
     */
    public ManagedConfiguration reset(ManagedConfiguration configuration);

    /**
     * Gets a Configuration from the repo
     * 
     * Will always pick a non-isSystemRequired if it exists.
     * 
     * @param name
     * @return
     */
    public ManagedConfiguration getByName(String name);

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
    
    public List<Configuration> getAllByType(String type);
    
    public Map<String,List<Configuration>> getCurrentConfigurations();
}
