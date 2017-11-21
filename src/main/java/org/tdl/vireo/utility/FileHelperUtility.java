package org.tdl.vireo.utility;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.tdl.vireo.Application;

public class FileHelperUtility {

    private final Tika tika = new Tika();

    // TODO: fix problems on Windows!!!
    public String getMimeType(String relativePath) {
        Path path = Paths.get(getPath(relativePath));
        return tika.detect(path.toString());
    }
    
    public static String getPath(String relativePath) {
        String path = Application.BASE_PATH + relativePath;
        if (path.contains(":") && path.charAt(0) == '/') {
            path = path.substring(1, path.length());
        }
        return path;
    }

}
