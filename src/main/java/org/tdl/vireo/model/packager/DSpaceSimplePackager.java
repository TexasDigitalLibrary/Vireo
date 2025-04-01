package org.tdl.vireo.model.packager;

import javax.persistence.Entity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.DSpaceSimplePackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;
import org.tdl.vireo.utility.CsvUtility;

@Entity
public class DSpaceSimplePackager extends AbstractPackager<DSpaceSimplePackage> {

    public DSpaceSimplePackager() {

    }

    public DSpaceSimplePackager(String name) {
        setName(name);
    }

    public DSpaceSimplePackager(String name, AbstractFormatter formatter) {
        this(name);
        setFormatter(formatter);
    }

    @Override
    public DSpaceSimplePackage packageExport(Submission submission, Map<String, String> dsDocs) {

        Map<String, File> pkgs = new HashMap<String, File>();
        try {
            // Add non submitted content
            for (Map.Entry<String, String> ds_entry : dsDocs.entrySet()) {
                String docName = ds_entry.getKey();
                String docContents = ds_entry.getValue();
                File ff = File.createTempFile(docName, "");
                FileUtils.writeStringToFile(ff, docContents, StandardCharsets.UTF_8);
                pkgs.put(docName, ff);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        //Add action_log file to zip
        try {
            String actionLogName = "action_log.csv";

            File actionLogFile = CsvUtility.fromActionLog(submission.getActionLogs(), actionLogName);

            pkgs.put(actionLogName, actionLogFile);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new DSpaceSimplePackage(submission, "http://purl.org/net/sword-types/METSDSpaceSIP", pkgs);
    }

}
