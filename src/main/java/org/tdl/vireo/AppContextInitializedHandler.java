package org.tdl.vireo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.Environment;

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
    
    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);
    
    @Override
    protected void before(ContextRefreshedEvent event) {

    	// Why not configure context here?
    	// Seems more logical than static class code with CommandLineRunner
    	/*
    	String applicationClassPathRoot = Application.class.getResource("/").getPath();
        File applicationClassPath = new File(applicationClassPathRoot);
                
        // if we're running in an expanded war
        if(applicationClassPath.exists() && applicationClassPath.isDirectory()) {
            File customProps = new File(applicationClassPathRoot + "../../../conf/application.properties");
            if(customProps.exists() && customProps.isFile()) {
                System.setProperty("spring.config.location", "file://" + customProps.getAbsolutePath());
            }
        }
        // if we're a jar or a war
        else if(applicationClassPath.exists() && applicationClassPath.isFile() && (applicationClassPathRoot.endsWith(".jar") || applicationClassPathRoot.endsWith(".war"))) {
            File customProps = new File(applicationClassPath.getParent() + "/conf/application.properties");
            if(customProps.exists() && customProps.isFile()) {
                System.setProperty("spring.config.location", "file://" + customProps.getAbsolutePath());
            }
        }
        */
    }

    @Override
    protected void after(ContextRefreshedEvent event) { 
        if(showBeans) {
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        }
        /*
        logger.info("Classpath root is: " + Application.class.getResource("/").getPath());
        logger.info("RUNNING! [" + env.getProperty("security.user.password") + "]");
        */
    }
    
}
