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
	ConfigurationRepo configurationRepo;
	
	@Value("${theme.reloadUrl}")
	String reloadUrl;
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
    public String getFormattedProperties() {
        StringBuilder formattedProperties = new StringBuilder();
        StringBuilder formattedComments = new StringBuilder();
        StringBuilder customCss = new StringBuilder();
        String[] themePropertyNames = {"background_main_color","background_highlight_color","button_main_color_on","button_highlight_color_on","button_main_color_off","button_highlight_color_off"};
        List<String> themeProperties = Arrays.asList(themePropertyNames);

        formattedComments.append("/* The Vireo ThemeManagerService added the following SASS vars:\n\n");
        List<Configuration> themeConfigurations = configurationRepo.getAllByType("lookAndFeel");
        themeConfigurations.forEach(c -> {
        	if (themeProperties.contains(c.getName())) {
        		formattedProperties.append("$" + c.getName() + ": " + c.getValue() + ";\n");
        		formattedComments.append("* $" + c.getName() + ": " + c.getValue() + ";\n");
        	}
        	if (c.getName().equals("custom_css")) {
        		customCss.append("/* Custom CSS */\n\n"+c.getValue()+"\n\n/* End Custom CSS */\n");
        	}
        });
        
        return formattedComments.toString() + " \n*/\n"+ formattedProperties.toString()+"\n"+customCss;
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
