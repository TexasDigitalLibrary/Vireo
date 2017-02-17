package org.tdl.vireo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
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
        // paths: "/data/attachments/**", "/conf/theme/**"
        // locations: BASE_PATH + symlink
        registry.addResourceHandler("/public/**").addResourceLocations("file:" + Application.BASE_PATH + "public/");

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
