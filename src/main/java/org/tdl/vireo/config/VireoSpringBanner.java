package org.tdl.vireo.config;

import java.io.IOException;
import java.io.PrintStream;
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
    
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        for (String line : BANNER) {
            out.println(line);
        }
        String version = environment.getProperty("info.build.version");
        
        if (version == null) {
           
            Manifest manifest = null;
            
            try {
                manifest = new Manifest(this.getClass().getResourceAsStream("/META-INF/manifest.mf"));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
   
}
