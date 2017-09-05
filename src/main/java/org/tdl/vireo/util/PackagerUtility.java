package org.tdl.vireo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Submission;
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

    public ExportPackage packageExport(Packager packager, Submission submission) throws Exception {
        String manifest = formatterUtility.renderManifest(packager.getFormatter(), submission);

        System.out.println("\n\n" + manifest + "\n\n");

        return packager.packageExport(manifest, submission);
    }

    public AbstractPackager getPackager(String name) {
        return abstractPackagerRepo.findByName(name);
    }

}
