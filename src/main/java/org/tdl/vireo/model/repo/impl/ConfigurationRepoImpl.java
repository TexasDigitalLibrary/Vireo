
package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

public class ConfigurationRepoImpl implements ConfigurationRepoCustom {

    @Autowired
    ConfigurationRepo configurationRepo;
    
    @Override
    public Configuration createOrUpdate(String name, String value, String type) {
        Configuration configuration = configurationRepo.getByName(name);
        // if we only found the system required one, create a custom non-system one instead of updating an already existing one.
        if(configuration != null && configuration.isSystemRequired()) {
            configuration = null;
        }
        if (configuration != null) {
            configuration.setValue(value);
            configuration.setType(type);
            return configurationRepo.save(configuration);
        }
        return configurationRepo.save(new Configuration(name, value, type));
    }

    @Override
    public String getValue(String name, String fallback) {
        String ret = fallback;
        Configuration configuration = configurationRepo.getByName(name);
        if (configuration != null) {
            ret = configuration.getValue();
        }
        return ret;
    }
    
    @Override
    public Integer getValue(String name, Integer fallback) {
        Integer ret = fallback;
        Configuration configuration = configurationRepo.getByName(name);
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
    public Configuration getByName(String name) {
        List<Configuration> configurations = configurationRepo.findByName(name);
        Configuration ret = null;
        for(Configuration configuration: configurations) {
            if(configuration.isSystemRequired() && ret == null) {
                ret = configuration;
            } else if (!configuration.isSystemRequired()) {
                ret = configuration;
            }
        }
        return ret;
    }
}
