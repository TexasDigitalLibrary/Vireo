package org.tdl.vireo.wro.model.resource.locator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

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
    @Override
    public boolean accept(final String url) {
        boolean accepted = false;
        if (url != null) {
            final String extension = FilenameUtils.getExtension(url);
            // scss file have either no extension or scss
            // maybe check for the "_"?
            if ("".equals(extension) || "scss".equals(extension)) {
                try {
                    accepted = getScssFile(url).isPresent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!accepted) {
            LOG.debug("Possible scss file not found {}", url);
        }
        return accepted;
    }

    private Optional<File> getScssFile(String url) throws IOException {

        Optional<File> file = Optional.empty();

        if (!url.endsWith(".scss")) {
            url += ".scss";
        }

        Resource resource = fileIOUtility.getResource(url);

        if (!resource.exists()) {
            url = "classpath:" + url;
            resource = fileIOUtility.getResource(url);
        }

        if (!resource.exists()) {
            final int lastSlash = url.lastIndexOf('/') + 1;
            String cleanUrl = url.substring(0, lastSlash);
            cleanUrl = cleanUrl + "_" + url.substring(lastSlash, url.length());
            resource = fileIOUtility.getResource(cleanUrl);
        }

        if (resource.exists() && resource.isReadable()) {
            file = Optional.of(resource.getFile());
        }

        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream locate(final String uri) throws IOException {
        Validate.notNull(uri, "URI cannot be NULL!");
        LOG.debug("loading scss file: {}", uri);
        return new FileInputStream(getScssFile(uri).get());
    }

}
