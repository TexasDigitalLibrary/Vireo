package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ZipExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

@Entity
public class DSpaceMetsPackager extends AbstractPackager<ZipExportPackage> {

    public DSpaceMetsPackager() {

    }

    public DSpaceMetsPackager(String name) {
        setName(name);
    }

    public DSpaceMetsPackager(String name, AbstractFormatter formatter) {
        this(name);
        setFormatter(formatter);
    }

    @Override
    public ZipExportPackage packageExport(Submission submission, String manifest) {

        String packageName = "submission-" + submission.getId() + "-";
        String manifestName = "mets.xml";

        File pkg = null;
        try {

            pkg = File.createTempFile(packageName, ".zip");

            FileOutputStream fos = new FileOutputStream(pkg);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Copy the manifest
            File manifestFile = File.createTempFile(manifestName, null);
            FileUtils.writeStringToFile(manifestFile, manifest, StandardCharsets.UTF_8);

            // Add manifest to zip
            zos.putNextEntry(new ZipEntry(manifestName));
            zos.write(Files.readAllBytes(manifestFile.toPath()));
            zos.closeEntry();

            manifestFile.delete();

            List<FieldValue> documentFieldValues = submission.getAllDocumentFieldValues();
            for (FieldValue documentFieldValue : documentFieldValues) {
                // TODO: add file whitelist for publish

                File exportFile = getAbsolutePath(documentFieldValue.getValue()).toFile();

                zos.putNextEntry(new ZipEntry(documentFieldValue.getExportFileName()));
                zos.write(Files.readAllBytes(exportFile.toPath()));
                zos.closeEntry();
            }

            zos.close();
            fos.close();

        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new ZipExportPackage(submission, "http://purl.org/net/sword-types/METSDSpaceSIP", pkg);
    }

    @Override
    public ZipExportPackage packageExport(Submission submission, Map<String, String> dsDocs) {
        String packageName = "submission_" + submission.getId()+ "/mets.xml";
        Map<String, File> pkgs = new HashMap<String, File>();
        try {
            // Add non submitted content
            for (Map.Entry<String, String> ds_entry : dsDocs.entrySet()) {
                String docContents = ds_entry.getValue();
                File ff = File.createTempFile(packageName, "");
                FileUtils.writeStringToFile(ff, docContents, "UTF-8");
                pkgs.put(packageName, ff);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new ZipExportPackage(submission, "http://purl.org/net/sword-types/METSDSpaceSIP", pkgs);
    }

}
