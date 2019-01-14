package org.tdl.vireo.config;

import java.util.List;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.weaver.auth.resolver.WeaverCredentialsArgumentResolver;
import edu.tamu.weaver.auth.resolver.WeaverUserArgumentResolver;
import edu.tamu.weaver.validation.resolver.WeaverValidatedModelMethodProcessor;

@EnableWebMvc
@Configuration
@EntityScan(basePackages = { "org.tdl.vireo.model" })
@EnableJpaRepositories(basePackages = { "org.tdl.vireo.model.repo" })
public class AppWebMvcConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AppWebMvcConfig.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Value("${info.build.production:false}")
    private boolean production;

    @Value("${app.public.folder:public}")
    private String publicFolder;

    @Bean
    public TomcatEmbeddedServletContainerFactory containerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });
        return factory;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!production) {
            logger.info("/node_modules/** -> file:" + Application.getRootPath() + "node_modules/");
            registry.addResourceHandler("/node_modules/**").addResourceLocations("file:" + Application.getRootPath() + "node_modules/");
        }
        logger.info("/public/** -> file:" + Application.getAssetsPath() + publicFolder + "/");
        registry.addResourceHandler("/**").addResourceLocations("/app/");
        registry.addResourceHandler("/public/**").addResourceLocations("file:" + Application.getAssetsPath() + publicFolder + "/");
        registry.setOrder(Integer.MAX_VALUE - 2);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WeaverValidatedModelMethodProcessor(converters));
        argumentResolvers.add(new WeaverCredentialsArgumentResolver());
        argumentResolvers.add(new WeaverUserArgumentResolver<User, UserRepo>(userRepo));
    }

}
