package org.tdl.vireo.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.packager.AbstractPackager;
import org.tdl.vireo.model.packager.Packager;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;

@Service
public class PackagerUtility {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AbstractPackagerRepo abstractPackagerRepo;

    @Autowired
    private FormatterUtility formatterUtility;

    public ExportPackage packageExport(Packager packager, Submission submission) throws Exception {
        String manifest = formatterUtility.renderManifest(packager.getFormatter(), submission);
        LOG.debug(manifest);
        return packager.packageExport(manifest, submission);
    }

    public AbstractPackager getPackager(String name) {
        return abstractPackagerRepo.findByName(name);
    }

}
