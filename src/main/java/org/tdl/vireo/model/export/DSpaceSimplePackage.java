package org.tdl.vireo.model.export;

import java.io.File;
import java.util.List;

import org.tdl.vireo.model.Submission;

public class DSpaceSimplePackage extends AbstractExportPackage {

    public DSpaceSimplePackage(Submission submission, String format, List<File> file) {
        super(submission, format, file);
    }

}
