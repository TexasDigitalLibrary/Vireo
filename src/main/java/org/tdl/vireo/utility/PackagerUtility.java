package org.tdl.vireo.utility;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PackagerUtility.class);

    @Autowired
    private AbstractPackagerRepo abstractPackagerRepo;

    @Autowired
    private FormatterUtility formatterUtility;

    public ExportPackage packageExport(Packager<?> packager, Submission submission) throws Exception {
        Optional<String> manifest = formatterUtility.renderManifest(packager.getFormatter(), submission);
        if (manifest.isPresent()) {
            logger.debug(manifest.get());
        } else {
            throw new UnsupportedFormatterException("Required manifest not found!");
        }
        return packager.packageExport(submission, manifest.get());
    }

    public ExportPackage packageExport(Packager<?> packager, Submission submission, List<SubmissionListColumn> columns) {
        return packager.packageExport(submission, columns);
    }

    public AbstractPackager<?> getPackager(String name) {
        return abstractPackagerRepo.findByName(name);
    }

}
