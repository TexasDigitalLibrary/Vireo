package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ZipExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

@Entity
public class ProQuestUmiPackager extends AbstractPackager<ZipExportPackage> {

    public ProQuestUmiPackager() {

    }

    public ProQuestUmiPackager(String name) {
        setName(name);
    }

    public ProQuestUmiPackager(String name, AbstractFormatter formatter) {
        this(name);
        setFormatter(formatter);
    }

    @Override
    public ZipExportPackage packageExport(Submission submission, Map<String, String> dsDocs) {

        Map<String, File> pkgs = new HashMap<String, File>();
        try {
            // Add non submitted content
            for (Map.Entry<String, String> ds_entry : dsDocs.entrySet()) {
                String docName = submission.getId()+"_"+ds_entry.getKey();
                String docContents = ds_entry.getValue();
                File ff = File.createTempFile(docName, "");
                FileUtils.writeStringToFile(ff, docContents, StandardCharsets.UTF_8);
                pkgs.put(docName, ff);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new ZipExportPackage(submission, "ProQuest UMI", pkgs);
    }
}
