package org.tdl.vireo.model.export;

import java.util.Map;

import org.tdl.vireo.model.Submission;

public class ExcelExportPackage extends AbstractExportPackage {

    public ExcelExportPackage(Submission submission, String format, Map<String, String> row) {
        super(submission, format, row);
    }

}
