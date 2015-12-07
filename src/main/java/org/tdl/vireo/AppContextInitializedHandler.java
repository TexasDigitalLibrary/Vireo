package org.tdl.vireo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.tdl.vireo.service.EmailService;

import edu.tamu.framework.CoreContextInitializedHandler;
import edu.tamu.framework.model.repo.SymlinkRepo;

/**
 * Handler for when the servlet context refreshes.
 * 
 * @author
 *
 */
@Component
@EnableConfigurationProperties(SymlinkRepo.class)
class AppContextInitializedHandler extends CoreContextInitializedHandler {

    @Value("${app.show-beans}")
    private Boolean showBeans;

    @Autowired
    private Environment env;

    @Autowired
    ApplicationContext applicationContext;
    
    @Autowired
    private EmailService emailService;

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
        logger.info("Classpath root is: " + Application.class.getResource("/").getPath());
        logger.info("RUNNING! [" + env.getProperty("security.user.password") + "]");
        
        emailService.init();
    }
}
