
package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DefaultConfiguration;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.interfaces.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;
import org.tdl.vireo.service.DefaultSettingsService;

public class ConfigurationRepoImpl implements ConfigurationRepoCustom {

    @Autowired
    private ConfigurationRepo configurationRepo;
    
    @Autowired
    private DefaultSettingsService defaultSettingsService;

    @Override
    public ManagedConfiguration create(String name, String value, String type) {
        return configurationRepo.save(new ManagedConfiguration(name, value, type));
    }

    @Override
    public ManagedConfiguration reset(ManagedConfiguration configuration) {

        configurationRepo.delete(configuration);

        return configurationRepo.findByNameAndIsSystemRequired(configuration.getName(), true);

    }

    @Override
    public String getValue(String name, String fallback) {
        String ret = fallback;
        ManagedConfiguration configuration = configurationRepo.getByName(name);
        if (configuration != null) {
            ret = configuration.getValue();
        }
        return ret;
    }

    @Override
    public Integer getValue(String name, Integer fallback) {
        Integer ret = fallback;
        ManagedConfiguration configuration = configurationRepo.getByName(name);
        if (configuration != null) {
            try {
                return Integer.parseInt(configuration.getValue());
            } catch (NumberFormatException e) {
                // do nothing, ret will use fallback
            }
        }
        return ret;
    }

    @Override
    public ManagedConfiguration getByName(String name) {
        List<ManagedConfiguration> configurations = configurationRepo.findByName(name);
        ManagedConfiguration ret = null;
        for (ManagedConfiguration configuration : configurations) {
            if (configuration.isSystemRequired() && ret == null) {
                ret = configuration;
            } else if (!configuration.isSystemRequired()) {
                ret = configuration;
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
        return (overrideValue != null) ? overrideValue:defaultSettingsService.getSetting(name, type);
    }
    
    public List<Configuration> getAllByType(String type) {
        return mergeConfigurations(configurationRepo.findByType(type),defaultSettingsService.getSettingsByType(type));
    }
    
    public Map<String,List<Configuration>> getCurrentConfigurations() {
    	Map<String,List<Configuration>> currentConfigurations = new HashMap<String,List<Configuration>>();
    	defaultSettingsService.getTypes().forEach(type -> {
    		currentConfigurations.put(type, mergeConfigurations(configurationRepo.findByType(type),defaultSettingsService.getSettingsByType(type)));
    	});
    	return currentConfigurations;
    }
    
    private List<Configuration> mergeConfigurations(List<ManagedConfiguration> managedConfigurations, List<DefaultConfiguration> defaultConfigurations) {
        List<Configuration> settings = new ArrayList<Configuration>();
        Map<String,ManagedConfiguration> overrideConfigs = new HashMap<String,ManagedConfiguration>();
        managedConfigurations.forEach(c -> {
        	overrideConfigs.put(c.getName(), c);
        });
        defaultConfigurations.forEach(dc -> {
        	if (overrideConfigs.containsKey(dc.getName())) {
        		settings.add(overrideConfigs.get(dc.getName()));
        	} else {
        		settings.add(dc);
        	}
        });
        return settings;
    }
    
}
