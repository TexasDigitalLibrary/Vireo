
package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

import edu.tamu.framework.validation.ModelBindingResult;

public class ConfigurationRepoImpl implements ConfigurationRepoCustom {

    @Autowired
    ConfigurationRepo configurationRepo;
    
    @Override
    public Configuration create(String name, String value, String type) {
        return configurationRepo.save(new Configuration(name, value, type));
    }
    
    @Override
    public Configuration reset(String name) {
        
        Configuration deletableOverride = configurationRepo.findByNameAndIsSystemRequired(name, false);
        if (deletableOverride != null) {
            configurationRepo.delete(deletableOverride);
        }
        
        return configurationRepo.findByNameAndIsSystemRequired(name, true);
        
    } 
    
    @Override
    public List<Configuration> getAll() {
        List<Configuration> ret = new ArrayList<Configuration>();
        List<Configuration> system = configurationRepo.findAllByIsSystemRequired(true);
        List<Configuration> user = configurationRepo.findAllByIsSystemRequired(false);
        for (Configuration sysConfig : system) {
            Boolean found = false;
            for(Configuration userConfig: user) {
                if(sysConfig.getName().equals(userConfig.getName())) {
                    ret.add(userConfig);
                    found = true;
                    break;
                }
            }
            if(!found) {
                ret.add(sysConfig);
            }
        }
        return ret;
    }
    
    @Override
    public List<Configuration> getAllByType(String type) {
        List<Configuration> ret = new ArrayList<Configuration>();
        List<Configuration> system = configurationRepo.findAllByTypeAndIsSystemRequired(type, true);
        List<Configuration> user = configurationRepo.findAllByTypeAndIsSystemRequired(type, false);
        for (Configuration sysConfig : system) {
            Boolean found = false;
            for(Configuration userConfig: user) {
                if(sysConfig.getName().equals(userConfig.getName())) {
                    ret.add(userConfig);
                    found = true;
                    break;
                }
            }
            if(!found) {
                ret.add(sysConfig);
            }
        }
        return ret;
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
    
    @Override
    public void validateUpdate(Configuration configuration) {
        Configuration existing = configurationRepo.getByName(configuration.getName());
        // if we only found the system required one, create a custom non-system
        if(existing != null && existing.isSystemRequired()) {
            // make sure we copy the binding result to the new configuration... for the controller to use if it needs it
            ModelBindingResult bindingResult = configuration.getBindingResult();
            configuration = configurationRepo.create(configuration.getName(), configuration.getValue(), configuration.getType());
            configuration.setBindingResult(bindingResult);
        }
        // otherwise if we found a non-system required one, update it
        else if (existing != null && !existing.isSystemRequired()) {
            existing.setValue(configuration.getValue());
            existing.setBindingResult(configuration.getBindingResult());
            configuration = existing;
        }
        // otherwise we didn't even find a system required one!
        else {
            configuration.getBindingResult().addError(new ObjectError("configuration", "Cannot create or update configuration that doesn't have a system-required copy in the database!"));
        }
    }
    
    @Override
    public void validateReset(Configuration configuration) {
        // TODO Auto-generated method stub
        
    }
}
