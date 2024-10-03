package org.tdl.vireo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.utility.FileHelperUtility;

@Service
public class AssetService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetService.class);

    private final FileHelperUtility fileHelperUtility = new FileHelperUtility();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    public void write(byte[] data, String relativePath) throws IOException {
        Files.write(processAssetsRelativePath(relativePath), data);
    }

    public void rename(String oldRelativePath, String newRelativePath) throws IOException {
        Path oldPath = getAssetsAbsolutePath(oldRelativePath);
        Path newPath = getAssetsAbsolutePath(newRelativePath);

        BasicFileAttributes attr = Files.readAttributes(oldPath, BasicFileAttributes.class);
        Long creationTime = attr.creationTime().toMillis();

        Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);

        Files.setAttribute(newPath, "creationTime", FileTime.fromMillis(creationTime));
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
        Path path = getAssetsAbsolutePath(relativePath);
        if (Files.exists(path)) {
            Files.delete(getAssetsAbsolutePath(relativePath));
        } else {
            LOG.warn("File not found while trying to delete at path: '" + path.getFileName().toString() + "'");
        }
    }

    public String write(InputStream is, String relativePath) throws IOException {
        Path path = processAssetsRelativePath(relativePath);
        String[] rawFileData = IOUtils.toString(is, StandardCharsets.UTF_8).split(";");
        String[] encodedData = rawFileData[1].split(",");
        byte[] fileData = Base64.getDecoder().decode(encodedData[1]);
        Files.write(path, fileData);
        return relativePath;
    }

    public void writeImage(InputStream inputStream, String relativePath) throws IOException {
        String inputData = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String[] imageData = inputData.split(";");
        String[] encodedData = imageData[1].split(",");
        String[] mimeData = imageData[0].split(":");
        String fileExtension = mimeData[1].split("/")[1];
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(encodedData[1])));
        Path path = processAssetsRelativePath(relativePath);
        ImageIO.write(image, fileExtension, Files.newOutputStream(path));
    }

    /**
     * Check to see if the asset file exists on the filesystem.
     *
     * @param relativePath The path to check.
     *
     * @return TRUE if file exists and FALSE otherwise.
     */
    public boolean assetFileExists(String relativePath) {
        return Files.exists(getAssetsAbsolutePath(relativePath));
    }

    /**
     * Get the file name, without the entire path.
     *
     * @param relativePath The path to process.
     *
     * @return The file name without the path.
     */
    public String getAssetFileName(String relativePath) {
        Path path = getAssetsAbsolutePath(relativePath);
        String fileName = path.getFileName().toString();

        return fileName.substring(fileName.indexOf('-') + 1);
    }

    public JsonNode getAssetFileInfo(String relativePath, Submission submission) throws IOException {
        Path path = getAssetsAbsolutePath(relativePath);
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        Map<String, Object> fileInfo = new HashMap<String, Object>();
        String fileName = path.getFileName().toString();
        String name = fileName.substring(fileName.indexOf('-') + 1);
        String readableFileSize = FileUtils.byteCountToDisplaySize(attr.size());
        fileInfo.put("name", name);
        fileInfo.put("type", fileHelperUtility.getMimeTypeOfAsset(relativePath));
        fileInfo.put("time", attr.creationTime().toMillis());
        fileInfo.put("size", attr.size());
        fileInfo.put("readableSize", readableFileSize);
        fileInfo.put("uploaded", true);

        Calendar creationDate = Calendar.getInstance();

        //creationDate.setTimeInMillis(attr.creationTime().toMillis());
        //current hack to find action_logs based on migrated file dates 
        creationDate.setTimeInMillis(0);

        Page<ActionLog> actionLogs = actionLogRepo.findBySubmissionIdAndEntryLikeAndBeforeActionDate(submission.getId(), name, creationDate, PageRequest.of(0, 1));

        if (!actionLogs.isEmpty()) {
            fileInfo.put("uploader", actionLogs.getContent().get(0).getUser().getName());
        }

        return objectMapper.valueToTree(fileInfo);
    }

    public Path getAssetsAbsolutePath(String relativePath) {
        return Paths.get(FileHelperUtility.getAssetAbsolutePath(relativePath));
    }

    private Path processAssetsRelativePath(String relativePath) throws IOException {
        Path path = getAssetsAbsolutePath(relativePath);
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

    public List<File> getResourceDirectoryListing(String resourceDirectory) throws IOException {
        Resource resource = getResource(resourceDirectory);
        URI uri = resource.getURI();
        if (uri.getScheme().equals("jar")) {
            String directory = null;
            if (uri.toString().contains(".jar!")) {
                directory = resourceDirectory.replace("classpath:", "/BOOT-INF/classes").replace("file:", "/");
            }else{ //.war!
                directory = resourceDirectory.replace("classpath:", "/WEB-INF/classes").replace("file:", "/");
            }
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

    private Resource getResource(String resourcePath) {
        return resourcePatternResolver.getResource(resourcePath);
    }

    private File createTempFileFromStream(InputStream stream) throws IOException {
        File tempFile = File.createTempFile("resource", ".tmp");
        tempFile.deleteOnExit();
        IOUtils.copy(stream, new FileOutputStream(tempFile));
        return tempFile;
    }

}
