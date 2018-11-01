package org.tdl.vireo.utility;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.exception.UnsupportedFormatterException;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.packager.AbstractPackager;
import org.tdl.vireo.model.packager.Packager;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;

@Service
public class PackagerUtility {

    @Autowired
    private AbstractPackagerRepo abstractPackagerRepo;

    @Autowired
    private FormatterUtility formatterUtility;

    public ExportPackage packageExport(Packager<?> packager, Submission submission) throws Exception {
        Map<String, String> manifest = formatterUtility.renderManifestMap(packager.getFormatter(), submission);
        if (manifest.isEmpty()) {
            throw new UnsupportedFormatterException("Required manifest not found!");
        }
        return packager.packageExport(submission, manifest);
    }

    public ExportPackage packageExport(Packager<?> packager, Submission submission, List<SubmissionListColumn> columns) {
        return packager.packageExport(submission, columns);
    }

    public ExportPackage packageExport(Packager<?> packager, Submission submission, Map<String, String> dsDocs) {
        return packager.packageExport(submission, dsDocs);
    }

    public AbstractPackager<?> getPackager(String name) {
        return abstractPackagerRepo.findByName(name);
    }

}
