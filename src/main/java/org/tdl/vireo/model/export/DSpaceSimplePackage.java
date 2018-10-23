package org.tdl.vireo.model.export;

import java.io.File;
//import java.util.List;
import java.util.Map;

import org.tdl.vireo.model.Submission;

public class DSpaceSimplePackage extends AbstractExportPackage {

    //public DSpaceSimplePackage(Submission submission, String format, List<File> file) {
    public DSpaceSimplePackage(Submission submission, String format, Map<String,File> file) {
        super(submission, format, file);
    }

}
