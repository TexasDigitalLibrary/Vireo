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

    /**
     * Cleans a file path to ensure compatibility across different file systems.
     *
     * @param path The path to clean
     * @return The cleaned path
     */
    private static String cleanPath(String path) {
        // Check if this is a Windows path with drive letter that has an extra leading slash
        // Example: "/C:/folder" should become "C:/folder"
        if (path.length() >= 3 &&
                path.charAt(0) == '/' &&
                Character.isLetter(path.charAt(1)) &&
                path.charAt(2) == ':') {

            return path.substring(1);
        }

        // For all other paths (including Linux paths with colons), return as is
        return path;
    }

}
