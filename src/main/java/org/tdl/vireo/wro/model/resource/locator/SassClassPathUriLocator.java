package org.tdl.vireo.wro.model.resource.locator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.tdl.vireo.utility.FileIOUtility;

import ro.isdc.wro.model.resource.locator.UriLocator;
import ro.isdc.wro.model.resource.locator.support.LocatorProvider;

/**
 * Custom loader to support SASS files containing imports and loaded by classpath 
 * 
 * Adapted from WRO native SassUriLocator: https://github.com/wro4j/wro4j/pull/1048/files
 * 
 * @author Jason Savell
 */
public class SassClassPathUriLocator implements UriLocator {
    private static final Logger LOG = LoggerFactory.getLogger(SassClassPathUriLocator.class);
    /**
     * Alias used to register this locator with {@link LocatorProvider}.
     */
    public static final String ALIAS = "sassClassPathUri";
    
    private FileIOUtility fileIOUtility;
    
    public SassClassPathUriLocator(FileIOUtility fileIOUtility) {
    	this.fileIOUtility = fileIOUtility;
    }

    /**
     * {@inheritDoc}
     */
    public boolean accept(final String url) {
        if (url == null) return false;
        final String extension = FilenameUtils.getExtension(url);
        // scss file have either no extension or scss
        // maybe check for the "_"?
        if ("".equals(extension) || "scss".equals(extension)) {
            boolean result = getScssFile(url) != null;
            if (!result) {
                LOG.debug("Possible scss file not found {}", url);
            }
            return result;
        } else {
            return false;
        }
    }
    
    File getScssFile(String url) {
        if (url == null) return null;

        if (url.startsWith("file:")) url = url.replace("file:", "");
        Resource resource = fileIOUtility.getResource(url);
        url = "classpath:"+url;
        if (!resource.exists()) {
            resource = fileIOUtility.getResource(url+".scss");
            if (!resource.exists()) {
	            if (!url.endsWith(".scss") && !url.endsWith("/")) {
	                final int lastSlash = url.lastIndexOf('/') + 1;
	                String cleanUrl = url.substring(0, lastSlash);
	                cleanUrl = cleanUrl + "_" + url.substring(lastSlash, url.length()) + ".scss";
	                resource = fileIOUtility.getResource(cleanUrl);
	            } else {
    				resource = fileIOUtility.getResource(url);
	            }
            }
            
        }
        if (resource.isReadable()) {
	    	try {
				return resource.getFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream locate(final String uri) throws IOException {
        Validate.notNull(uri, "URI cannot be NULL!");
        LOG.debug("loading scss file: {}", uri);
        return new FileInputStream(getScssFile(uri));
    }
}
