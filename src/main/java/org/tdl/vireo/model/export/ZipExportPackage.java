package org.tdl.vireo.model.export;

import java.io.File;

import org.tdl.vireo.model.Submission;

public class ZipExportPackage extends AbstractExportPackage {

    public ZipExportPackage(Submission submission, String format, File file) {
        super(submission, format, file);
    }

}
