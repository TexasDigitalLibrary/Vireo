package org.tdl.vireo.config;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.jar.Manifest;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

public class VireoSpringBanner implements Banner {

    private static final String[] BANNER = { "",
            "                                                        ",
            "___      ___  ___   ________   _______    ________      ",
            "|\\  \\    /  /||\\  \\ |\\   __  \\ |\\  ___ \\  |\\   __  \\    ",
            "\\ \\  \\  /  / /\\ \\  \\\\ \\  \\|\\  \\\\ \\   __/| \\ \\  \\|\\  \\   ",
            " \\ \\  \\/  / /  \\ \\  \\\\ \\   _  _\\\\ \\  \\_|/__\\ \\  \\\\\\  \\  ",
            "  \\ \\    / /    \\ \\  \\\\ \\  \\\\  \\|\\ \\  \\_|\\ \\\\ \\  \\\\\\  \\ ",
            "   \\ \\__/ /      \\ \\__\\\\ \\__\\\\ _\\ \\ \\_______\\\\ \\_______\\",
            "    \\|__|/        \\|__| \\|__|\\|__| \\|_______| \\|_______|",
            "                                                        "
    };
    
    private static final String VIREO_BOOT = " :: 01010110 01101001 01110010 01100101 01101111 :: ";

    private static final int STRAP_LINE_SIZE = 42;
    
    @SuppressWarnings("deprecation")
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        for (String line : BANNER) {
            out.println(line);
        }
        
        String version = this.getClass().getPackage().getImplementationVersion();
        
        version = environment.getProperty("info.build.version");
        
        // shouldn't ever be null, but just in case get it from the manifest
        if (version == null) {           
            Manifest manifest = getManifest(this.getClass());            
            version = manifest.getMainAttributes().getValue("Implementation-Version");
        }
        
        version = (version == null ? "" : " (v" + version + ")");
        String padding = "";
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + VIREO_BOOT.length())) {
            padding += " ";
        }
        out.println(AnsiOutput.toString(AnsiElement.GREEN, VIREO_BOOT, AnsiElement.DEFAULT, padding, AnsiElement.FAINT, version));
        out.println();
    }
    
    private static Manifest getManifest(Class<?> clz) {
        String resource = "/" + clz.getName().replace(".", "/") + ".class";
        String fullPath = clz.getResource(resource).toString();
        String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
        if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith("/WEB-INF/classes")) {
            archivePath = archivePath.substring(0, archivePath.length() - "/WEB-INF/classes".length()); // Required for war/jar files
        }

        try (InputStream input = new URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
            return new Manifest(input);
        } catch (Exception e) {
            throw new RuntimeException("Loading MANIFEST for class " + clz + " failed!", e);
        }
    }
    
   
}
