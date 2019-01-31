package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ZipExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

@Entity
public class MarcXML21Packager extends AbstractPackager<ZipExportPackage> {

    public MarcXML21Packager() {

    }

    public MarcXML21Packager(String name) {
        setName(name);
    }

    public MarcXML21Packager(String name, AbstractFormatter formatter) {
        this(name);
        setFormatter(formatter);
    }

    @Override
    public ZipExportPackage packageExport(Submission submission, Map<String, String> dsDocs) {

        Map<String, File> pkgs = new HashMap<String, File>();
        try {
            // Add non submitted content
            for (Map.Entry<String, String> ds_entry : dsDocs.entrySet()) {
                String docName = ds_entry.getKey();
                String docContents = ds_entry.getValue();
                File ff = File.createTempFile(docName, "");
                FileUtils.writeStringToFile(ff, docContents, "UTF-8");
                pkgs.put(docName, ff);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new ZipExportPackage(submission, "http://www.loc.gov/MARC21/slim", pkgs);
    }
}
