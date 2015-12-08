
package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

public class ConfigurationRepoImpl implements ConfigurationRepoCustom {

    @Autowired
    ConfigurationRepo configurationRepo;

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
}
