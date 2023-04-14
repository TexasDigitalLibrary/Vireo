package org.tdl.vireo.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import edu.tamu.weaver.wro.service.SimpleThemeManagerService;

@Service
public class VireoThemeManagerService extends SimpleThemeManagerService implements VireoThemeManager {

    @Autowired
    private ConfigurationRepo configurationRepo;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Map<String, String> getThemeProperties() {
        String[] themePropertyNames = {"text_main_color", "background_main_color", "background_highlight_color", "background_text_color", "button_main_color_on", "button_highlight_color_on", "button_text_color_on", "button_main_color_off", "button_highlight_color_off", "button_text_color_off", "admin_tab_main_color", "admin_tab_selected_color", "admin_tab_text_color"};
        List<String> themePropertyNamesList = Arrays.asList(themePropertyNames);
        List<Configuration> themeConfigurations = configurationRepo.getAllByType("lookAndFeel");
        HashMap<String, String> themeProperties = new HashMap<String, String>();
        themeConfigurations.forEach(c -> {
            if (themePropertyNamesList.contains(c.getName())) {
                themeProperties.put(c.getName(), c.getValue());
            }
        });
        return themeProperties;
    }

    public String getCustomCss() {
        Configuration cssConfiguration = configurationRepo.getByNameAndType("custom_css", "lookAndFeel");
        return cssConfiguration.getValue();
    }
}
