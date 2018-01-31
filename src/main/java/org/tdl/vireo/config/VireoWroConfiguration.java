package org.tdl.vireo.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.tdl.vireo.service.VireoThemeManager;
import org.tdl.vireo.wro.manager.factory.VireoConfigurableWroManagerFactory;

import edu.tamu.weaver.wro.config.WeaverWroConfiguration;
import ro.isdc.wro.manager.factory.WroManagerFactory;

@Configuration
public class VireoWroConfiguration extends WeaverWroConfiguration {
    @Autowired
    VireoThemeManager vireoThemeManagerService;

    
    @Override
    protected WroManagerFactory getWroManagerFactory(Properties properties) {
    	return new VireoConfigurableWroManagerFactory(properties, vireoThemeManagerService, getResourcePatternResolver());
    }

}
