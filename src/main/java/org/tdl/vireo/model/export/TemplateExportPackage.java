package org.tdl.vireo.model.export;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.Submission;

public class TemplateExportPackage implements ExportPackage {

    public final Submission submission;
    public final String mimeType;
    public final String format;
    public final File file;
    public final String entryName;

    public TemplateExportPackage(Submission submission, String mimeType, String format, File file, String entryName) {
        this.submission = submission;
        this.mimeType = mimeType;
        this.format = format;
        this.file = file;
        this.entryName = entryName;
    }

    @Override
    public Submission getSubmission() {
        return submission;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String getEntryName() {
        return entryName;
    }

    @Override
    public void delete() {
        if (file != null && file.exists()) {

            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException ioe) {
                    throw new RuntimeException("Unable to cleanup export package: " + file.getAbsolutePath(), ioe);
                }
            } else {
                file.delete();
            }

        }
    }

}
