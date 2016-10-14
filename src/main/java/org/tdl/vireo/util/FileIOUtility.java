package org.tdl.vireo.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.tdl.vireo.Application;

@Service
public class FileIOUtility {
    
    public void write(byte[] bytes, String relativePath) throws IOException {
        Files.write(Paths.get(getPath(relativePath)), bytes);
    }

    public void write(InputStream is, String path) throws IOException {
        byte[] bytes = IOUtils.toByteArray(is);
        write(bytes, path);
    }

    public void writeImage(InputStream stream, String relativePath) throws IOException {
        String inputData = IOUtils.toString(stream, "UTF-8");
        String[] imageData = inputData.split(";");
        String[] encodedData = imageData[1].split(",");
        String[] mimeData = imageData[0].split(":");
        String fileExtension = mimeData[1].split("/")[1];
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(encodedData[1])));
        Path path = Paths.get(getPath(relativePath));
        Path parentDir = path.getParent();
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        ImageIO.write(image, fileExtension, Files.newOutputStream(path));
    }
    
    private String getPath(String relativePath) {
    	String path = Application.BASE_PATH + relativePath;
        if(path.contains(":") && path.charAt(0) == '/') {
        	path = path.substring(1, path.length());
        }    
        return path;
    }

}
