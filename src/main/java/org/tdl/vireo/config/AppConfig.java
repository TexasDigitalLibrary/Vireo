package org.tdl.vireo.config;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.tdl.vireo.controller.interceptor.AppRestInterceptor;

import edu.tamu.framework.config.CoreWebAppConfig;

@Configuration
@ComponentScan(basePackages = {"org.tdl.vireo.config", "org.tdl.vireo.controller", "org.tdl.vireo.model"})
@EnableJpaRepositories(basePackages={"org.tdl.vireo.model.repo"})
@EntityScan(basePackages={"org.tdl.vireo.model"})
public class AppConfig extends CoreWebAppConfig {

    @Bean
    public AppRestInterceptor restInterceptor() {
        return new AppRestInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restInterceptor()).addPathPatterns("/**").excludePathPatterns("/");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
