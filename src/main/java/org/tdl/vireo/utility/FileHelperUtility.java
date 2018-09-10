package org.tdl.vireo.utility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.tdl.vireo.Application;

public class FileHelperUtility {

    private final Tika tika = new Tika();

    public String getMimeTypeOfResource(String relativePath) {
        return getMimeType(getResourceAbsolutePath(relativePath));
    }

    public String getMimeTypeOfAsset(String relativePath) {
        return getMimeType(getAssetAbsolutePath(relativePath));
    }

    public String getMimeType(String absolutePath) {
        Path path = Paths.get(absolutePath);
        return tika.detect(path.toString());
    }

    public String getMimeType(File file) {
        Path path = Paths.get(file.getAbsolutePath());
        return tika.detect(path.toString());
    }

    public static String getResourceAbsolutePath(String relativePath) {
        return cleanPath(Application.getRootPath() + relativePath);
    }

    public static String getAssetAbsolutePath(String relativePath) {
        return cleanPath(Application.getAssetsPath() + relativePath);
    }

    private static String cleanPath(String path) {
        if (path.contains(":") && path.charAt(0) == '/') {
            path = path.substring(1, path.length());
        }
        return path;
    }

}
