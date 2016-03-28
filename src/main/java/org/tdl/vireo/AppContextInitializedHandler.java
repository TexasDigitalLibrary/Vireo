package org.tdl.vireo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

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
public class AppContextInitializedHandler extends CoreContextInitializedHandler {

    @Value("${app.show-beans}")
    private Boolean showBeans;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${app.ui.path}")
    private String path;

    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);
    
    @Override
    protected void before(ContextRefreshedEvent event) {
        // TODO: something before context refresh?
    }

    @Override
    protected void after(ContextRefreshedEvent event) {
        if (showBeans) {
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                logger.info(beanName);
            }
        }
    }
}
