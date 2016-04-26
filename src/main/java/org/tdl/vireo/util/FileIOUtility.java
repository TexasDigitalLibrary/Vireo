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
import javax.servlet.ServletInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tdl.vireo.Application;

@Service
public class FileIOUtility {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    public void write(byte[] bytes, String filePath) throws IOException {
        Path path = Paths.get(Application.BASE_PATH + filePath);
        Files.write(path, bytes);
    }

    public void write(InputStream is, String filePath) throws IOException {
        byte[] bytes = IOUtils.toByteArray(is);
        write(bytes, filePath);
    }

    public void writeImage(ServletInputStream stream, String filePath) throws IOException {

        String inputData = IOUtils.toString(stream, "UTF-8");
        String[] imageData = inputData.split(";");
        String[] encodedData = imageData[1].split(",");
        String[] mimeData = imageData[0].split(":");
        String fileExtension = mimeData[1].split("/")[1];
        
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(encodedData[1])));
        ImageIO.write(image, fileExtension, Files.newOutputStream(Paths.get(Application.BASE_PATH + filePath)));

    }

}
