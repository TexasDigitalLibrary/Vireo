package org.tdl.vireo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.web.FilterChainProxy;
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
class AppContextInitializedHandler extends CoreContextInitializedHandler {

    @Autowired
    ApplicationContext applicationContext;
    
    @Autowired
    FilterChainProxy filterChainProxy;
    
    @Override
    protected void before(ContextRefreshedEvent event) {

    }

    @Override
    protected void after(ContextRefreshedEvent event) { 
        //String[] beanNames = applicationContext.getBeanDefinitionNames();
        //for (String beanName : beanNames) {
        //    System.out.println(beanName);
        //}
    }
    
}
