package org.tdl.vireo.model.packager;

import java.io.File;

import javax.persistence.Entity;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.export.TemplateExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

@Entity
public class DSpaceMetsPackager extends AbstractPackager {

    public DSpaceMetsPackager() {
        setName("DSpace METS");
    }

    public DSpaceMetsPackager(AbstractFormatter formatter) {
        this();
        setFormatter(formatter);
    }

    @Override
    public ExportPackage packageExport(String manifest, Submission submission) {
        // TODO: build package properties
        return new TemplateExportPackage(submission, "TODO", "TODO", new File("TODO"), "TODO");
    }

}
