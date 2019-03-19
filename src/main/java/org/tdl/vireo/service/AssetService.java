package org.tdl.vireo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.tdl.vireo.utility.FileHelperUtility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AssetService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    private final FileHelperUtility fileHelperUtility = new FileHelperUtility();

    public void write(byte[] data, String relativePath) throws IOException {
        Files.write(processAssetsRelativePath(relativePath), data);
    }

    public void copy(String oldRelativePath, String newRelativePath) throws IOException {
        Files.copy(getAssetsAbsolutePath(oldRelativePath), getAssetsAbsolutePath(newRelativePath), StandardCopyOption.REPLACE_EXISTING);
    }

    public String find(String relativeFolderPath, String name) {
        String targetPath = null;
        Path folder = getAssetsAbsolutePath(relativeFolderPath);
        for (String path : folder.toFile().list()) {
            if (path.contains(name)) {
                targetPath = path;
                break;
            }
        }
        return targetPath;
    }

    public void delete(String relativePath) throws IOException {
        Files.delete(Paths.get(FileHelperUtility.getAssetAbsolutePath(relativePath)));
    }

    public String write(InputStream is, String relativePath) throws IOException {
        Path path = processAssetsRelativePath(relativePath);
        String[] rawFileData = IOUtils.toString(is, "UTF-8").split(";");
        String[] encodedData = rawFileData[1].split(",");
        byte[] fileData = Base64.getDecoder().decode(encodedData[1]);
        Files.write(path, fileData);
        return relativePath;
    }

    public void writeImage(InputStream inputStream, String relativePath) throws IOException {
        String inputData = IOUtils.toString(inputStream, "UTF-8");
        String[] imageData = inputData.split(";");
        String[] encodedData = imageData[1].split(",");
        String[] mimeData = imageData[0].split(":");
        String fileExtension = mimeData[1].split("/")[1];
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(encodedData[1])));
        Path path = processAssetsRelativePath(relativePath);
        ImageIO.write(image, fileExtension, Files.newOutputStream(path));
    }

    public JsonNode getAssetFileInfo(String relativePath) throws IOException {
        Path path = Paths.get(FileHelperUtility.getAssetAbsolutePath(relativePath));
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        Map<String, Object> fileInfo = new HashMap<String, Object>();
        String fileName = path.getFileName().toString();
        String readableFileSize = FileUtils.byteCountToDisplaySize(attr.size());
        fileInfo.put("name", fileName.substring(fileName.indexOf('-') + 1));
        fileInfo.put("type", fileHelperUtility.getMimeTypeOfAsset(relativePath));
        fileInfo.put("time", attr.creationTime().toMillis());
        fileInfo.put("size", attr.size());
        fileInfo.put("readableSize", readableFileSize);
        fileInfo.put("uploaded", true);
        return objectMapper.valueToTree(fileInfo);
    }

    public Path getAssetsAbsolutePath(String relativePath) {
        return Paths.get(FileHelperUtility.getAssetAbsolutePath(relativePath));
    }

    private Path processAssetsRelativePath(String relativePath) throws IOException {
        Path path = Paths.get(FileHelperUtility.getAssetAbsolutePath(relativePath));
        Path parentDir = path.getParent();
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        return path;
    }

    public File getFileFromResource(String resourcePath) throws IOException {
        Resource resource = getResource(resourcePath);
        if (resource.getURI().getScheme().equals("jar")) {
            return createTempFileFromStream(resource.getInputStream());
        }
        return resource.getFile();
    }

    public List<File> getResouceDirectoryListing(String resourceDirectory) throws IOException {
        Resource resource = getResource(resourceDirectory);
        URI uri = resource.getURI();
        if (uri.getScheme().equals("jar")) {
            String directory = resourceDirectory.replace("classpath:", "/WEB-INF/classes").replace("file:", "/");
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, String>emptyMap());
            Path directoryPath = fileSystem.getPath(directory);
            Iterator<Path> it = Files.walk(directoryPath, 1).filter(Files::isRegularFile).iterator();
            List<File> files = new ArrayList<File>();
            while (it.hasNext()) {
                files.add(createTempFileFromStream(Files.newInputStream(it.next())));
            }
            return files;
        }
        return Arrays.asList(resource.getFile().listFiles());
    }

    public Resource getResource(String resourcePath) {
        return resourcePatternResolver.getResource(resourcePath);
    }

    private File createTempFileFromStream(InputStream stream) throws IOException {
        File tempFile = File.createTempFile("resource", ".tmp");
        tempFile.deleteOnExit();
        IOUtils.copy(stream, new FileOutputStream(tempFile));
        return tempFile;
    }

}
