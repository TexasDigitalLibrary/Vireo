package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.tdl.vireo.Application;

import edu.tamu.framework.config.CoreWebMvcConfig;

@Configuration
@EnableWebMvc
@DependsOn("systemDataLoader")
public class AppWebMvcConfig extends CoreWebMvcConfig {

    @Value("${app.ui.path}")
    private String path;
    
    @Value("${info.build.production:false}")
    private boolean production;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	
    	if(!production) {
    		registry.addResourceHandler("/node_modules/**").addResourceLocations("file:" + Application.BASE_PATH + "node_modules/");
    	}

        registry.addResourceHandler("/**").addResourceLocations("WEB-INF" + path + "/");

        registry.setOrder(Integer.MAX_VALUE - 2);
    }

}
