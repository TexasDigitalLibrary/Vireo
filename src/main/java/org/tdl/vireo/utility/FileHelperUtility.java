package org.tdl.vireo.utility;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.tdl.vireo.Application;

public class FileHelperUtility {

    private final Tika tika = new Tika();

    // TODO: fix problems on Windows!!!
    public String getMimeType(String relativePath) {
        String safePath = Application.BASE_PATH + relativePath;
        if (safePath.contains(":") && safePath.charAt(0) == '/') {
            safePath = safePath.substring(1, safePath.length());
        }
        Path path = Paths.get(safePath);
        return tika.detect(path.toString());
    }

}
