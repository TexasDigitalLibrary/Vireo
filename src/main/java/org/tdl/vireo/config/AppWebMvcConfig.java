package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.util.HashedFile;

@Configuration
@EnableWebMvc
@DependsOn({"delegatingApplicationListener"})
public class AppWebMvcConfig extends WebMvcConfigurerAdapter {

    @Value("${app.ui.path}")
    private String path;

    @Autowired
    private HashedFile hashedFile;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        System.out.println(configurationRepo);
        System.out.println(ConfigurationName.APPLICATION_ATTACHMENTS_PATH);
        System.out.println(configurationRepo.getByName(ConfigurationName.APPLICATION_ATTACHMENTS_PATH).getValue() + "/**");
        System.out.println("file:" + hashedFile.getStore().getAbsolutePath() + "/");

        registry.addResourceHandler("/**").addResourceLocations("WEB-INF" + path + "/");
        //registry.addResourceHandler(configurationRepo.getByName(ConfigurationName.APPLICATION_ATTACHMENTS_PATH).getValue() + "/**").addResourceLocations("file:" + hashedFile.getStore().getAbsolutePath() + "/");
        registry.setOrder(Integer.MAX_VALUE - 2);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new html5Forwarder();
    }

    private static class html5Forwarder implements EmbeddedServletContainerCustomizer {
        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/"));
        }
    }

}