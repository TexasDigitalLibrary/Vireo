
package org.tdl.vireo.model.repo.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;
import org.tdl.vireo.service.DefaultSettingsService;

public class ConfigurationRepoImpl implements ConfigurationRepoCustom {

    @Autowired
    ConfigurationRepo configurationRepo;
    
    @Autowired
    DefaultSettingsService defaultSettingsService;

    /**
     * Creates or updates existing configuration
     * 
     * @param name
     * @param value
     * @return
     */
    @Override
    public Configuration create(String name, String value) {
        Configuration configuration = configurationRepo.findByName(name);
        if (configuration != null) {
            configuration.setValue(value);
            return configurationRepo.save(configuration);
        }
        return configurationRepo.save(new Configuration(name, value));
    }
    
    @Override
    public Configuration create(String name, String value, String type) {
        System.out.println("type: "+type);
        if (type == null) {
            return create(name, value);
        } else {
            Configuration configuration = configurationRepo.findByNameAndType(name,type);
            if (configuration != null) {
                configuration.setValue(value);
                return configurationRepo.save(configuration);
            }
            return configurationRepo.save(new Configuration(name, value, type));
        }
    }

    @Override
    public String getValue(String name, String fallback) {
        String ret = fallback;
        Configuration configuration = configurationRepo.findByName(name);
        if (configuration != null) {
            ret = configuration.getValue();
        }
        return ret;
    }
    
    @Override
    public Integer getValue(String name, Integer fallback) {
        Integer ret = fallback;
        Configuration configuration = configurationRepo.findByName(name);
        if (configuration != null) {
            try {
                return Integer.parseInt(configuration.getValue());
            } catch (NumberFormatException e) {
                // do nothing, ret will use fallback
            }
        }
        return ret;
    }
    
    /**
     * Gets a config value from the DB by name and type.
     * If no value is found, it checks the DefaultSettingsService, which returns the default for that name and type if it exists, null otherwise.
     * 
     * @param name
     * @param type
     * @return String
     */
    @Override
    public String getValueByNameAndType(String name, String type) {
        String overrideValue = configurationRepo.getValueByNameAndType(name,type);
        if (overrideValue != null) {
            return overrideValue;
        }
        return defaultSettingsService.getSetting(name, type);
    }
    
    public Map<String,String> getAllByType(String type) {
        List<Configuration> overrideConfigs = configurationRepo.findByType(type);
        
        Map<String,String> settings = new HashMap<String,String>(); 
        settings = defaultSettingsService.getSettingsByType(type);
        System.out.println("default settings:");
        settings.forEach((f2,v2) -> {
           System.out.println(f2+": "+v2); 
        });

        for (Configuration config:overrideConfigs) {
            settings.put(config.getName(), config.getValue());
        }
        /*
        overrideConfigs.forEach(c -> {
            settings.put(c.getName(), c.getValue());
        });
        
        System.out.println("final settings:");
        settings.forEach((f,v) -> {
           System.out.println(f+": "+v); 
        });
        */
        return settings;
    }
    
}
