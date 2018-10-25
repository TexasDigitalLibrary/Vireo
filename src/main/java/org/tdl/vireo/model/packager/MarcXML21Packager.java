package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

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
    public ZipExportPackage packageExport(Submission submission, String manifest) {

        String packageName = "submission_" + submission.getId().toString();
        File pkg = null;

        try {
            pkg = File.createTempFile(packageName, ".xml");

            FileOutputStream fos = new FileOutputStream(pkg);

            File submissionFile = File.createTempFile(packageName, null);
            FileUtils.writeStringToFile(submissionFile, manifest, "UTF-8");

            fos.write(Files.readAllBytes(submissionFile.toPath()));
            submissionFile.delete();
            fos.close();

        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new ZipExportPackage(submission, "http://www.loc.gov/MARC21/slim", pkg);
    }
}
