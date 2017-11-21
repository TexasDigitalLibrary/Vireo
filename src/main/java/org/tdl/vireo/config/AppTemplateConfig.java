package org.tdl.vireo.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class AppTemplateConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SpringResourceTemplateResolver xmlTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("classpath:/formats/");
        resolver.setSuffix(".xml");
        resolver.setTemplateMode("XML");
        resolver.setCharacterEncoding(UTF_8.name());
        resolver.setCheckExistence(true);
        resolver.setCacheable(false);
        return resolver;
    }

}
