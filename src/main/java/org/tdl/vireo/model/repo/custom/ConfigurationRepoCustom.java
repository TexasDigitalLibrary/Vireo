package org.tdl.vireo.model.repo.custom;

import java.util.List;
import java.util.Map;

import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.ManagedConfiguration;

public interface ConfigurationRepoCustom {

    /**
     * Creates a ManagedConfiguration
     * 
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
    public Configuration reset(ManagedConfiguration configuration);

    /**
     * Gets a Configuration by its name and type
     * 
     * 
     * @param String name
     * @param String type
     * @return
     */
    public Configuration getByNameAndType(String name, String type);
    
    /**
     * Gets a Configuration from the repo or falls back to the default, returns null if neither exist
     * 
     * @param name
     * @return
     */
    public String getValueByName(String name);

    /**
     * Gets a Configuration's string value by its name and type.
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
