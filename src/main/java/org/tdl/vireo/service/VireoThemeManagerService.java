package org.tdl.vireo.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.tamu.weaver.utility.HttpUtility;
import edu.tamu.weaver.wro.service.ThemeManagerService;

@Service
public class VireoThemeManagerService extends ThemeManagerService {

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${theme.reloadUrl}")
    public String reloadUrl;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getFormattedProperties() {
        StringBuilder formattedProperties = new StringBuilder();
        StringBuilder formattedComments = new StringBuilder();
        String[] themePropertyNames = { "background_main_color", "background_highlight_color", "button_main_color_on", "button_highlight_color_on", "button_main_color_off", "button_highlight_color_off" };
        @SuppressWarnings("unchecked")
        List<String> themeProperties = Arrays.asList(themePropertyNames);

        formattedComments.append("/* The Vireo ThemeManagerService added the following SASS vars:\n\n");
        List<Configuration> themeConfigurations = configurationRepo.getAllByType("lookAndFeel");
        themeConfigurations.forEach(c -> {
            if (themeProperties.contains(c.getName())) {
                formattedProperties.append("$" + c.getName() + ": " + c.getValue() + ";\n");
                formattedComments.append("* $" + c.getName() + ": " + c.getValue() + ";\n");
            }
        });

        return formattedComments.toString() + " \n*/\n" + formattedProperties.toString() + "\n";
    }

    public String getCustomCss() {
        Configuration cssConfiguration = configurationRepo.getByNameAndType("custom_css", "lookAndFeel");
        return "/* The Vireo ThemeManagerService added the following custom CSS: */\n\n" + cssConfiguration.getValue() + "\n\n /* End custom CSS */";
    }

    // tell WRO to reset its resource cache
    public void reloadCache() {
        try {
            HttpUtility.makeHttpRequest(reloadUrl, "GET");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
