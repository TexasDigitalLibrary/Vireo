package org.tdl.vireo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
	basePackages = { "edu.tamu.*", "org.tdl.*" }
)
public class Application extends SpringBootServletInitializer {

    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static String BASE_PATH = "/var/lib/vireo/";

    /**
     * {@inheritDoc}
     *
     * This configuration is for when running inside of Tomcat/Jetty
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        init(false);
        return application.sources(Application.class);
    }

    /**
     * Main method for when running as a stand-alone Spring Boot Application
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        init(true);
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }

    /**
     * Shared init() method for when starting as either stand-alone Spring Boot app or as a Tomcat/Jetty webapp
     */
    public static void init(boolean isSpringBoot) {
        String applicationClassPathRoot = Application.class.getResource("/").getPath();
        File applicationClassPath = new File(applicationClassPathRoot);
        // if we're running in an expanded war
        if (applicationClassPath.exists() && applicationClassPath.isDirectory()) {
            BASE_PATH = applicationClassPathRoot + (isSpringBoot ? "../../" : "../../../");
            File customProps = new File(BASE_PATH + "conf/application.properties");
            if (customProps.exists() && customProps.isFile()) {
                logger.info("Loading application.properties from " + BASE_PATH + "conf directory relative to our classpath");
                System.setProperty("spring.config.location", "file://" + customProps.getAbsolutePath());
            }
        }
        // if we're a jar or a war
        else if (applicationClassPath.exists() && applicationClassPath.isFile() && (applicationClassPathRoot.endsWith(".jar") || applicationClassPathRoot.endsWith(".war"))) {
            BASE_PATH = applicationClassPath.getParent();
            File customProps = new File(BASE_PATH + "/conf/application.properties");
            if (customProps.exists() && customProps.isFile()) {
                logger.info("Loading application.properties from  " + BASE_PATH + "conf directory in same parent directory as our .jar/.war");
                System.setProperty("spring.config.location", "file://" + customProps.getAbsolutePath());
            }
        } else {
            logger.info("Couldn't discern how we're running to be able to load an external application.properties file!");
        }
    }
}
