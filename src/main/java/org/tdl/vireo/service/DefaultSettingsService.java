package org.tdl.vireo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.DefaultConfiguration;
import org.tdl.vireo.model.DefaultPreferences;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DefaultSettingsService {

    private final Logger logger = LoggerFactory.getLogger(DefaultSettingsService.class);

    private final List<DefaultPreferences> defaultSettings;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    DefaultSettingsService() {
        defaultSettings = new ArrayList<DefaultPreferences>();
    }

    @PostConstruct
    void init() throws IOException {
        logger.info("Loading default settings");

        Resource resource = resourcePatternResolver.getResource("classpath:/settings/SYSTEM_Defaults.json");
        JsonNode systemDefaults = objectMapper.readTree(resource.getInputStream());

        Iterator<Entry<String, JsonNode>> it = systemDefaults.fields();

        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) it.next();
            List<DefaultConfiguration> defaultConfigurations = new ArrayList<DefaultConfiguration>();
            if (entry.getValue().isArray()) {
                for (JsonNode objNode : entry.getValue()) {
                    objNode.fieldNames().forEachRemaining(n -> {
                        defaultConfigurations.add(new DefaultConfiguration(n, objNode.get(n).asText(), entry.getKey()));
                    });
                }
            }
            defaultSettings.add(new DefaultPreferences(entry.getKey(), defaultConfigurations));
        }

        getTypes().forEach(t -> {
            logger.debug("Stored preferences for type: " + t);
            getSettingsByType(t).forEach(c -> {
                logger.debug(c.getName() + ": " + c.getValue());
            });
        });
    }

    public DefaultConfiguration getSettingByName(String field) {
        for (DefaultPreferences defaultPreferences : defaultSettings) {
            for (DefaultConfiguration defaultConfiguration : defaultPreferences.getPreferences()) {
                if (defaultConfiguration.getName().equals(field)) {
                    return defaultConfiguration;
                }
            }
        }
        return null;
    }

    public DefaultConfiguration getSettingByNameAndType(String field, String type) {
        return getSettingsByType(type).stream()
            .filter(preference -> preference.getName().equals(field))
            .findFirst()
            .orElse(null);
    }

    public List<String> getTypes() {
        return defaultSettings.stream()
            .map(p -> p.getType())
            .collect(Collectors.toUnmodifiableList());
    }

    public List<DefaultConfiguration> getSettingsByType(String type) {
        return Collections.unmodifiableList(defaultSettings.stream()
            .filter(preferences -> preferences.getType().equals(type))
            .map(preferences -> preferences.getPreferences())
            .findFirst()
            .orElse(new ArrayList<DefaultConfiguration>()));
    }

}
