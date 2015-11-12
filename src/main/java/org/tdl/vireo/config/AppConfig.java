package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.tdl.vireo.controller.interceptor.AppRestInterceptor;

import edu.tamu.framework.config.CoreWebAppConfig;

@Configuration
@ComponentScan(basePackages = {"org.tdl.vireo.config", "org.tdl.vireo.controller"})
public class AppConfig extends CoreWebAppConfig {
    
    @Bean
    public AppRestInterceptor restInterceptor() {
        return new AppRestInterceptor();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restInterceptor()).addPathPatterns("/**");
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
