package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import edu.tamu.framework.config.CoreWebMvcConfig;

@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class AppWebMvcConfig extends CoreWebMvcConfig {
            
    @Value("${app.ui.path}")
    private String path;
    
    @Autowired
    private Environment env;

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
       return new ResourceUrlEncodingFilter();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       boolean devMode = this.env.acceptsProfiles("dev");
       boolean useResourceCache = !devMode;
       Integer cachePeriod = devMode ? 0 : null;

       registry.addResourceHandler("/**")
          .addResourceLocations("classpath:" + path + "/")
          .setCachePeriod(cachePeriod)
          .resourceChain(useResourceCache)
          .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
          .addTransformer(new AppCacheManifestTransformer());
       
       registry.setOrder(Integer.MAX_VALUE - 2);
    }
    
}
