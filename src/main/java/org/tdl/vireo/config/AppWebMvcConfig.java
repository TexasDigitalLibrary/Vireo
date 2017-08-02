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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").addResourceLocations("WEB-INF" + path + "/");

        // TODO: investigate and implement dynamic resource locations at runtime via symlinks
        // paths: "/data/documents/**", "/conf/theme/**"
        // locations: BASE_PATH + symlink
        registry.addResourceHandler("/public/**").addResourceLocations("file:" + Application.BASE_PATH + "public/");

        registry.setOrder(Integer.MAX_VALUE - 2);
    }

}
