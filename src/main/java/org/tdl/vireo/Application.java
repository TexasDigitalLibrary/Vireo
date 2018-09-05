package org.tdl.vireo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationHome;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

@SpringBootApplication
@ComponentScan(basePackages = { "edu.tamu.*", "org.tdl.*" })
public class Application extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    // where to store public and private directories
    private static String assetsPath;

    // where is root of the app, i.e. where node_modules is
    private static String rootPath;

    @Value("${app.assets.uri:classpath:/}")
    private Resource assets;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }

    @PostConstruct
    public void init() throws IOException, URISyntaxException {
        assetsPath = assets.getURI().getSchemeSpecificPart();
        // ensure assetsPath ends with URI seperator
        if (!assetsPath.endsWith("/")) {
            assetsPath += "/";
        }
        ApplicationHome HOME = new ApplicationHome(Application.class);
        if (assets.getURI().getScheme().equals("jar")) {
            rootPath = HOME.getDir().getAbsolutePath() + File.separator + ".." + File.separator;
        } else {
            rootPath = HOME.getDir().getAbsolutePath() + File.separator + ".." + File.separator + ".." + File.separator;
        }
        logger.info("ASSETS PATH: " + assetsPath);
        logger.info("ROOT PATH: " + rootPath);
    }

    public static String getAssetsPath() {
        return assetsPath;
    }

    public static String getRootPath() {
        return rootPath;
    }

}
