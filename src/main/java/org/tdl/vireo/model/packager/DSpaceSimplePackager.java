package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.export.DSpaceSimplePackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

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
            Set <ActionLog> actionLogSet = submission.getActionLogs();
            ArrayList<ActionLog> actionLogArray = new ArrayList<ActionLog>();
            actionLogArray.addAll(actionLogSet);
            actionLogArray.sort((a1,a2) -> a1.getActionDate().compareTo(a2.getActionDate()));

            StringBuilder actionLogStr = new StringBuilder();
            actionLogStr.append("Action Date, Action Entry, SubmissionState\n");

            SimpleDateFormat sd_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
            for(ActionLog al : actionLogArray){
                actionLogStr.append(sd_format.format(al.getActionDate().getTime())).append(",");
                actionLogStr.append('"'+al.getEntry().toString()+'"').append(",");
                actionLogStr.append(al.getSubmissionStatus().getName().toString()).append("\n");
            }

            File actionLogFile = File.createTempFile(actionLogName, null);
            FileUtils.writeStringToFile(actionLogFile, actionLogStr.toString(), StandardCharsets.UTF_8);
            pkgs.put(actionLogName,actionLogFile);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new DSpaceSimplePackage(submission, "http://purl.org/net/sword-types/METSDSpaceSIP", pkgs);
    }

}
