package org.tdl.vireo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.tdl.vireo.condition.NotRunningTests;
import org.tdl.vireo.service.EmailService;
import org.tdl.vireo.service.SystemDataLoader;

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
    private EmailService emailService;

    @Autowired
    private SystemDataLoader systemDataLoader;

    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);

    @Override
    protected void before(ContextRefreshedEvent event) {
        //TODO: something before context refresh?
    }

    @Override
    protected void after(ContextRefreshedEvent event) {
        if (showBeans) {
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                logger.info(beanName);
            }
        }
        
        emailService.init();
        
        logger.info("Generating all system email templates");
        systemDataLoader.generateAllSystemEmailTemplates();
        
        logger.info("Generating all system embargos");
        systemDataLoader.generateAllSystemEmbargos();
        
        logger.info("Generating system submission states");
        systemDataLoader.loadSystemSubmissionStates();
        
        logger.info("Generating system organization");
        systemDataLoader.loadSystemOrganization();
        
        logger.info("Generating system defaults");
        systemDataLoader.generateSystemDefaults();
    }
}
