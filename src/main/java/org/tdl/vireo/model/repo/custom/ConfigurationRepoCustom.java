package org.tdl.vireo.model.repo.custom;

import java.util.List;

import javax.servlet.ServletInputStream;

import org.tdl.vireo.controller.model.LookAndFeelControllerModel;
import org.tdl.vireo.model.Configuration;

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
    public Configuration create(String name, String value, String type);
    
    /**
     * Resets existing configuration to its system value
     * 
     * 
     * @param configuration
     * @return
     */
    public Configuration reset(Configuration configuration);
    
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
    
    /**
     * Validates all the business logic for an incoming configuration
     * 
     * @param unvalidated {@link Configuration} for updating
     * @return validated {@link Configuration}
     */
    public Configuration validateUpdate(Configuration configuration);
    
    /**
     * Validates all the business logic for an incoming configuration
     * 
     * @param unvalidated {@link Configuration} for resetting
     * @return validated {@link Configuration}
     */
    public Configuration validateReset(Configuration configuration);
    
    /**
     * Validates all the business logic for an incoming logo
     * 
     * @param unvalidated {@link LookAndFeelControllerModel} for updating
     * @param inputStream
     * @return validated {@link LookAndFeelControllerModel}
     */
    public LookAndFeelControllerModel validateUploadLogo(LookAndFeelControllerModel lfModel, ServletInputStream inputStream, String path);
    
    /**
     * Validates all the business logic for an incoming reset logo
     * 
     * @param unvalidated {@link LookAndFeelControllerModel} for updating
     * @param inputStream
     * @return validated {@link LookAndFeelControllerModel} to reset the logo with
     */
    public LookAndFeelControllerModel validateResetLogo(LookAndFeelControllerModel lfModel);
}
