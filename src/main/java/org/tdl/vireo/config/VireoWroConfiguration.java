package org.tdl.vireo.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.tdl.vireo.service.VireoThemeManagerService;
import org.tdl.vireo.utility.FileIOUtility;
import org.tdl.vireo.wro.manager.factory.VireoConfigurableWroManagerFactory;

import edu.tamu.weaver.wro.config.WroConfiguration;
import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.handler.factory.SimpleRequestHandlerFactory;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import wro4j.http.handler.CustomRequestHandler;

@Configuration
public class VireoWroConfiguration extends WroConfiguration {
    private static final String[] OTHER_WRO_PROP = new String[] { ConfigurableProcessorsFactory.PARAM_PRE_PROCESSORS, ConfigurableProcessorsFactory.PARAM_POST_PROCESSORS };
    
    @Autowired
    private VireoThemeManagerService themeManagerService;
    
    @Autowired
    private FileIOUtility fileIOUtility;
    
    @Bean
    public FilterRegistrationBean webResourceOptimizer(Environment env) {
        FilterRegistrationBean fr = new FilterRegistrationBean();
        ConfigurableWroFilter filter = new ConfigurableWroFilter();
        Properties props = buildWroProperties(env);
        filter.setProperties(props);
        filter.setWroManagerFactory(new VireoConfigurableWroManagerFactory(props, themeManagerService,fileIOUtility));
        filter.setRequestHandlerFactory(new SimpleRequestHandlerFactory().addHandler(new CustomRequestHandler()));
        filter.setProperties(props);
        fr.setFilter(filter);
        fr.addUrlPatterns("/wro/*");
        return fr;
    }
    
    private Properties buildWroProperties(Environment env) {
        Properties prop = new Properties();
        for (ConfigConstants c : ConfigConstants.values()) {
            addProperty(env, prop, c.name());
        }
        for (String name : OTHER_WRO_PROP) {
            addProperty(env, prop, name);
        }
        addProperty(env,prop,"uriLocators");
        return prop;
    }

    private void addProperty(Environment env, Properties to, String name) {
        String value = env.getProperty("wro." + name);
        if (value != null) {
            to.put(name, value);
        }
}
}
