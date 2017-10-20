
package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.DefaultConfiguration;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;
import org.tdl.vireo.service.DefaultSettingsService;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ConfigurationRepoImpl extends AbstractWeaverRepoImpl<ManagedConfiguration, ConfigurationRepo> implements ConfigurationRepoCustom {

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private DefaultSettingsService defaultSettingsService;

    @Override
    public ManagedConfiguration create(String name, String value, String type) {
        return configurationRepo.save(new ManagedConfiguration(name, value, type));
    }

    @Override
    public Configuration reset(ManagedConfiguration configuration) {
        configurationRepo.delete(configuration);
        return defaultSettingsService.getSettingByNameAndType(configuration.getName(), configuration.getType());
    }

    @Override
    public Configuration getByNameAndType(String name, String type) {
        ManagedConfiguration configuration = configurationRepo.findByName(name);
        if (configuration != null) {
            return configuration;
        }
        return defaultSettingsService.getSettingByNameAndType(name, type);
    }

    /**
     * Gets a config value from the DB by name and type. If no value is found, it checks the DefaultSettingsService, which returns the default for that name and type if it exists, null otherwise.
     * 
     * @param name
     * @param type
     * @return String
     */
    @Override
    public String getValueByNameAndType(String name, String type) {
        Configuration overrideConfig = configurationRepo.findByNameAndType(name, type);
        if (overrideConfig != null) {
            return overrideConfig.getValue();
        }

        Configuration defaultConfig = defaultSettingsService.getSettingByNameAndType(name, type);
        if (defaultConfig != null) {
            return defaultConfig.getValue();
        }
        return null;
    }

    @Override
    public String getValueByName(String name) {
        Configuration overrideConfig = configurationRepo.findByName(name);
        if (overrideConfig != null) {
            return overrideConfig.getValue();
        }

        Configuration defaultConfig = defaultSettingsService.getSettingByName(name);
        if (defaultConfig != null) {
            return defaultConfig.getValue();
        }
        return null;
    }

    public List<Configuration> getAllByType(String type) {
        return mergeConfigurations(configurationRepo.findByType(type), defaultSettingsService.getSettingsByType(type));
    }

    public Map<String, List<Configuration>> getCurrentConfigurations() {
        Map<String, List<Configuration>> currentConfigurations = new HashMap<String, List<Configuration>>();
        defaultSettingsService.getTypes().forEach(type -> {
            currentConfigurations.put(type, mergeConfigurations(configurationRepo.findByType(type), defaultSettingsService.getSettingsByType(type)));
        });
        return currentConfigurations;
    }

    private List<Configuration> mergeConfigurations(List<ManagedConfiguration> managedConfigurations, List<DefaultConfiguration> defaultConfigurations) {
        List<Configuration> settings = new ArrayList<Configuration>();
        Map<String, ManagedConfiguration> overrideConfigs = new HashMap<String, ManagedConfiguration>();
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

    @Override
    protected String getChannel() {
        return "/channel/configuration";
    }

}
