package org.tdl.vireo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

    // where to store public and private directories
    private static String assetsPath;

    // where is root of the app, i.e. where node_modules is
    private static String rootPath;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }

    @Value("${app.assets.uri:classpath:/}")
    public void setup(Resource assets) throws IOException, URISyntaxException {

        System.out.println("\n\n\n\n\nASSETS URI: " + assets.getURI() + "\n\n\n\n\n");

        System.out.println("\n\n\n\n\nASSETS URI SCHEME PART: " + assets.getURI().getSchemeSpecificPart() + "\n\n\n\n\n");

        System.out.println("\n\n\n\n\nASSETS URI SCHEME: " + assets.getURI().getScheme() + "\n\n\n\n\n");

        ApplicationHome HOME = new ApplicationHome(Application.class);

        if (assets.getURI().getScheme().equals("jar")) {
            rootPath = HOME.getDir().getAbsolutePath() + File.separator + ".." + File.separator;
        } else {
            rootPath = HOME.getDir().getAbsolutePath() + File.separator + ".." + File.separator + ".." + File.separator;
        }

        assetsPath = assets.getURI().toString().replace(assets.getURI().getSchemeSpecificPart(), "");

        System.setProperty("spring.config.location", "file:" + File.separator + assetsPath + "conf/");

        System.out.println("\n\n\n\n\nASSETS PATH: " + assetsPath + "\n\n\n\n\n");

        System.out.println("\n\n\n\n\nROOT PATH: " + rootPath + "\n\n\n\n\n");
    }

    public static String getAssetsPath() {
        return assetsPath;
    }

    public static String getRootPath() {
        return rootPath;
    }

}
