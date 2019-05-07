package org.tdl.vireo.model.export;

import java.io.File;
import java.util.Map;

import org.tdl.vireo.model.Submission;

public class ZipExportPackage extends AbstractExportPackage {

    public ZipExportPackage(Submission submission, String format, Map<String, File> files) {
        super(submission, format, files);
    }

    public ZipExportPackage(Submission submission, String format,File file) {
      super(submission, format, file);
    }
}
