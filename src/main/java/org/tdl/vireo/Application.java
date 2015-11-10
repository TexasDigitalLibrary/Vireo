package org.tdl.vireo;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.jar.Manifest;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan(basePackages={"edu.tamu.framework", "edu.tamu.auth", "org.tdl.vireo"})
public class Application extends SpringBootServletInitializer implements Banner {

    private static final String[] BANNER = { "",
            "                                             ",
            " _______  _______  _______  _______  _______ ",
            "|\\     /||\\     /||\\     /||\\     /||\\     /|",
            "| +---+ || +---+ || +---+ || +---+ || +---+ |",
            "| |   | || |   | || |   | || |   | || |   | |",
            "| |V  | || |i  | || |r  | || |e  | || |o  | |",
            "| +---+ || +---+ || +---+ || +---+ || +---+ |",
            "|/_____\\||/_____\\||/_____\\||/_____\\||/_____\\|",
            "                                             ", };

    private static final String VIREO_BOOT = " :: 01010110 01101001 01110010 01100101 01101111 :: ";

    private static final int STRAP_LINE_SIZE = 42;

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        for (String line : BANNER) {
            printStream.println(line);
        }
        String version = this.getClass().getPackage().getImplementationVersion();
        if (version == null) {
            Manifest manifest = getManifest(this.getClass());
            version = manifest.getMainAttributes().getValue("Implementation-Version");
        }
        version = (version == null ? "" : " (v" + version + ")");
        String padding = "";
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + VIREO_BOOT.length())) {
            padding += " ";
        }

        printStream.println(AnsiOutput.toString(AnsiElement.GREEN, VIREO_BOOT, AnsiElement.DEFAULT, padding, AnsiElement.FAINT, version));
        printStream.println();
    }

    private static Manifest getManifest(Class<?> clz) {
        String resource = "/" + clz.getName().replace(".", "/") + ".class";
        String fullPath = clz.getResource(resource).toString();
        String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
        if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith("/WEB-INF/classes")) {
            archivePath = archivePath.substring(0, archivePath.length() - "/WEB-INF/classes".length()); // Required for wars
        }

        try (InputStream input = new URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
            return new Manifest(input);
        } catch (Exception e) {
            throw new RuntimeException("Loading MANIFEST for class " + clz + " failed!", e);
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.banner(this);
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
