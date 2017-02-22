package org.tdl.vireo.util;

import javax.activation.MimetypesFileTypeMap;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.Application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileIOUtility {

	@Autowired
	private ObjectMapper objectMapper;

	private final Tika tike = new Tika();
	
	public void write(byte[] data, String relativePath) throws IOException {
		Files.write(processRelativePath(relativePath), data);
	}
	
	public void copy(String oldRelativePath, String newRelativePath) throws IOException {
		Files.copy(getAbsolutePath(oldRelativePath), getAbsolutePath(newRelativePath), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public String find(String relativeFolderPath, String name) {
		String targetPath = null;
		Path folder = getAbsolutePath(relativeFolderPath);
		for(String path : folder.toFile().list()) {			
			if(path.contains(name)) {
				targetPath = path;
				break;
			}
		}
		return targetPath;
	}
	
	public void delete(String relativePath) throws IOException {
		Files.delete(Paths.get(getPath(relativePath)));
	}
	
	public String write(InputStream is, String relativePath) throws IOException {
		Path path = processRelativePath(relativePath);
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
		Path path = processRelativePath(relativePath);
		ImageIO.write(image, fileExtension, Files.newOutputStream(path));
	}

	public JsonNode getFileInfo(String relativePath) throws IOException {
		Path path = Paths.get(getPath(relativePath));
		BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
		Map<String, Object> fileInfo = new HashMap<String, Object>();
		String fileName = path.getFileName().toString();
		fileInfo.put("name", fileName.substring(fileName.indexOf('-') + 1));
		fileInfo.put("type", tike.detect(path.toString()));
		fileInfo.put("time", attr.creationTime().toMillis());
		fileInfo.put("size", attr.size());
		fileInfo.put("uploaded", true);
		return objectMapper.valueToTree(fileInfo);
	}

	public Path getAbsolutePath(String relativePath) {
		return Paths.get(getPath(relativePath));
	}

	private String getPath(String relativePath) {
		String path = Application.BASE_PATH + relativePath;
		if (path.contains(":") && path.charAt(0) == '/') {
			path = path.substring(1, path.length());
		}
		return path;
	}
	
	private Path processRelativePath(String relativePath) throws IOException {
		Path path = Paths.get(getPath(relativePath));
		Path parentDir = path.getParent();
		if (!Files.exists(parentDir)) {
			Files.createDirectories(parentDir);
		}
		return path;
	}

}
