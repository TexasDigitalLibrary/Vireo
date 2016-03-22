package org.tdl.vireo;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.tdl.vireo.condition.NotRunningTests;
import org.tdl.vireo.config.AppWebMvcConfig;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.SystemDataLoader;
import org.tdl.vireo.util.HashedFile;

import edu.tamu.framework.CoreContextInitializedHandler;
import edu.tamu.framework.model.repo.SymlinkRepo;

/**
 * Handler for when the servlet context refreshes.
 * 
 * @author
 *
 */
@Component
@Conditional(NotRunningTests.class)
@EnableConfigurationProperties(SymlinkRepo.class)
class AppContextInitializedHandler extends CoreContextInitializedHandler {

    @Value("${app.show-beans}")
    private Boolean showBeans;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ServletContext servletContext;

    @Autowired
    private SystemDataLoader systemDataLoader;

    @Autowired
    private AppWebMvcConfig appWebMvcConfig;

    @Autowired
    private HashedFile hashedFile;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${app.ui.path}")
    private String path;

    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);
    
    @Override
    protected void before(ContextRefreshedEvent event) {
        // TODO: something before context refresh?
        
        logger.info("Generating system defaults");
        systemDataLoader.generateSystemDefaults();

//        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(applicationContext, servletContext);
//        registry.addResourceHandler("/**").addResourceLocations("WEB-INF" + path + "/");
//        registry.addResourceHandler(configurationRepo.getByName(ConfigurationName.APPLICATION_ATTACHMENTS_PATH).getValue() + "/**").addResourceLocations("file:" + hashedFile.getStore().getAbsolutePath() + "/");
//        registry.setOrder(Integer.MAX_VALUE - 2);
//        appWebMvcConfig.addResourceHandlers(registry);
    }

    @Override
    protected void after(ContextRefreshedEvent event) {
        if (showBeans) {
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                logger.info(beanName);
            }
        }

        logger.info("Generating all system email templates");
        systemDataLoader.generateAllSystemEmailTemplates();

        logger.info("Generating all system embargos");
        systemDataLoader.generateAllSystemEmbargos();

        logger.info("Generating system submission states");
        systemDataLoader.loadSystemSubmissionStates();

        logger.info("Generating system organization");
        systemDataLoader.loadSystemOrganization();

       
        

    }
}
