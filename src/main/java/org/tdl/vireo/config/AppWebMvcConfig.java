package org.tdl.vireo.config;

import java.io.File;
import java.util.List;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
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
@EntityScan(basePackages = { "org.tdl.vireo.model", "edu.tamu.weaver.wro.model" })
@EnableJpaRepositories(basePackages = { "org.tdl.vireo.model.repo", "edu.tamu.weaver.wro.model.repo" })
public class AppWebMvcConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AppWebMvcConfig.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Value("${app.ui.path}")
    private String path;

    @Value("${info.build.production:false}")
    private boolean production;

    @Value("${app.public.folder:public}")
    private String publicFolder;

    @Bean
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/admin/h2console/*");
        registrationBean.addInitParameter("-webAllowOthers", "true");
        return registrationBean;
    }

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
        logger.info("PROPERTIES LOCATION: " + System.getProperty("spring.config.location"));
        if (!production) {
            logger.info("/node_modules/** -> file:" + File.separator + Application.getRootPath() + "node_modules" + File.separator);
            registry.addResourceHandler("/node_modules/**").addResourceLocations("file:" + File.separator + Application.getRootPath() + "node_modules" + File.separator);
        }
        logger.info("/public/** -> file:" + File.separator + Application.getAssetsPath() + publicFolder + File.separator);
        registry.addResourceHandler("/**").addResourceLocations(path + File.separator);
        registry.addResourceHandler("/public/**").addResourceLocations("file:" + File.separator + Application.getAssetsPath() + publicFolder + File.separator);
        registry.setOrder(Integer.MAX_VALUE - 2);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WeaverValidatedModelMethodProcessor(converters));
        argumentResolvers.add(new WeaverCredentialsArgumentResolver());
        argumentResolvers.add(new WeaverUserArgumentResolver<User, UserRepo>(userRepo));
    }

}
