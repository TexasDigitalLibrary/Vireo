
package org.tdl.vireo.model.repo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.controller.model.LookAndFeelControllerModel;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;
import org.tdl.vireo.util.FileIOUtility;

import edu.tamu.framework.validation.ModelBindingResult;

public class ConfigurationRepoImpl implements ConfigurationRepoCustom {

    @Autowired
    private ConfigurationRepo configurationRepo;
    
    @Autowired
    private FileIOUtility fileIOUtility;

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
            for (Configuration userConfig : user) {
                if (sysConfig.getName().equals(userConfig.getName())) {
                    ret.add(userConfig);
                    found = true;
                    break;
                }
            }
            if (!found) {
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
            for (Configuration userConfig : user) {
                if (sysConfig.getName().equals(userConfig.getName())) {
                    ret.add(userConfig);
                    found = true;
                    break;
                }
            }
            if (!found) {
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
        for (Configuration configuration : configurations) {
            if (configuration.isSystemRequired() && ret == null) {
                ret = configuration;
            } else if (!configuration.isSystemRequired()) {
                ret = configuration;
            }
        }
        return ret;
    }

    @Override
    public Configuration validateUpdate(Configuration configuration) {
        if(!configuration.getBindingResult().hasErrors()) {
            Configuration configurationToUpdate = configurationRepo.getByName(configuration.getName());
            // special case for #RGB values for CSS colors
            if(configuration.getType().equals("lookAndFeel") && configuration.getName().contains("_color")){
                if(!configuration.getValue().matches("(^#[0-9A-Fa-f]{6}$)|(^#[0-9A-Fa-f]{3}$)")) {
                    configuration.getBindingResult().addError(new ObjectError("configuration", "Invalid Hex color value!"));
                }
            }
            // if we only found the system required one, create a custom non-system
            if (configurationToUpdate != null && configurationToUpdate.isSystemRequired()) {
                // make sure we copy the binding result to the new configuration... for the controller to use if it needs it
                ModelBindingResult bindingResult = configuration.getBindingResult();
                // we copy values over to new instance of Configuration in case the incoming configuration had isSystemRequired() set to true
                configuration = configurationRepo.create(configuration.getName(), configuration.getValue(), configuration.getType());
                configuration.setBindingResult(bindingResult);
            }
            // otherwise if we found a non-system required one, update it
            else if (configurationToUpdate != null && !configurationToUpdate.isSystemRequired()) {
                configurationToUpdate.setValue(configuration.getValue());
                configurationToUpdate.setBindingResult(configuration.getBindingResult());
                configuration = configurationToUpdate;
            }
            // otherwise we didn't even find a system required one!
            else {
                configuration.getBindingResult().addError(new ObjectError("configuration", "Cannot update configuration that doesn't have a system-required copy in the database!"));
            }
        }
        return configuration;
    }

    @Override
    public Configuration validateReset(Configuration configuration) {
        Configuration configurationToUpdate = configurationRepo.getByName(configuration.getName());
        // if it doesn't exist
        if (configurationToUpdate == null) {
            configuration.getBindingResult().addError(new ObjectError("configuration", "Cannot reset configuration that doesn't have a system-required copy in the database!"));
        }
        // it exists, but we don't have a custom override for it: nothing to reset!
        else if (configurationToUpdate.isSystemRequired()) {
            configuration.getBindingResult().addWarning(new ObjectError("configuration", "No custom value was set for " + configuration.getName() + ". Nothing to reset!"));
        }
        return configuration;
    }
    
    @Override
    public LookAndFeelControllerModel validateUploadLogo(LookAndFeelControllerModel lfModel, ServletInputStream inputStream, String path) {
        Configuration logoToUpdate = configurationRepo.getByName(lfModel.getSetting());
        // if it doesn't exist
        if (logoToUpdate == null) {
            lfModel.getBindingResult().addError(new ObjectError("lookAndFeelControllerModel", "Cannot upload logo that doesn't exist!"));
        } else {
            try {
                fileIOUtility.writeImage(inputStream, path);
            } catch (IOException e) {
                lfModel.getBindingResult().addError(new ObjectError("lookAndFeelControllerModel", e.getLocalizedMessage()));
            }
        }
        return lfModel;
    }
    
    @Override
    public LookAndFeelControllerModel validateResetLogo(LookAndFeelControllerModel lfModel) {
        Configuration logoToReset = configurationRepo.getByName(lfModel.getSetting());
        // if it doesn't exist
        if (logoToReset == null) {
            lfModel.getBindingResult().addError(new ObjectError("configuration", "Cannot reset logo that doesn't have a system-required copy in the database!"));
        }
        // it exists, but we don't have a custom override for it: nothing to reset!
        else if (logoToReset.isSystemRequired()) {
            lfModel.getBindingResult().addWarning(new ObjectError("configuration", "No custom value was set for " + lfModel.getSetting() + ". Nothing to reset!"));
        }
        return lfModel;
    }
}
