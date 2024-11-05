package org.tdl.vireo.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
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
public class AppWebMvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AppWebMvcConfig.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Value("${app.config.uri:classpath:/appConfig.js}")
    private String appConfigUri;

    @Value("${app.public.folder:public}")
    private String publicFolder;

    @Bean
    public TomcatServletWebServerFactory containerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
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
        registry.addResourceHandler("/appConfig.js").addResourceLocations(appConfigUri);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/");

        registry.addResourceHandler("/public/**").addResourceLocations("file:" + Application.getAssetsPath() + publicFolder + "/");
        registry.addResourceHandler("/application.yml")
                .setCachePeriod(0)
                .addResourceLocations("classpath:/")
                .resourceChain(true)
                .addResolver(new ResourceResolver() {

                    @Override
                    public Resource resolveResource(HttpServletRequest request, String requestPath,
                            List<? extends Resource> locations, ResourceResolverChain chain) {
                        return null;
                    }

                    @Override
                    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations,
                            ResourceResolverChain chain) {
                                return null;
                    }

                })
                .addTransformer((resource, requestPath, transformerChain) -> null);

        registry.setOrder(Integer.MAX_VALUE - 2);

        logger.info("/public/** -> file:" + Application.getAssetsPath() + publicFolder + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WeaverValidatedModelMethodProcessor(converters));
        argumentResolvers.add(new WeaverCredentialsArgumentResolver());
        argumentResolvers.add(new WeaverUserArgumentResolver<User, UserRepo>(userRepo));
    }

}
