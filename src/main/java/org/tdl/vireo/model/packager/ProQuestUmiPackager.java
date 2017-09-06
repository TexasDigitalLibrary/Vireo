package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.export.TemplateExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

@Entity
public class ProQuestUmiPackager extends AbstractPackager {

    public ProQuestUmiPackager() {
        setName("ProQuestUMI");
    }

    public ProQuestUmiPackager(AbstractFormatter formatter) {
        this();
        setFormatter(formatter);
    }

    @Override
    public ExportPackage packageExport(String manifest, Submission submission) {
        
        String packageName = "submission-" + submission.getId() + "-";
        String manifestName = "proquest.xml";

        File pkg = null;
        try {

            pkg = File.createTempFile(packageName, ".zip");

            FileOutputStream fos = new FileOutputStream(pkg);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Copy the manifest
            File manifestFile = File.createTempFile(manifestName, null);
            FileUtils.writeStringToFile(manifestFile, manifest, "UTF-8");

            // Add manifest to zip
            zos.putNextEntry(new ZipEntry(manifestName));
            zos.write(Files.readAllBytes(manifestFile.toPath()));
            zos.closeEntry();

            manifestFile.delete();

            List<FieldValue> documentFieldValues = submission.getAllDocumentFieldValues();
            for (FieldValue documentFieldValue : documentFieldValues) {

                // TODO: add file whitelist for publish

                File exportFile = getAbsolutePath(documentFieldValue.getValue()).toFile();

                zos.putNextEntry(new ZipEntry(exportFile.getName()));
                zos.write(Files.readAllBytes(exportFile.toPath()));
                zos.closeEntry();

            }

            zos.close();
            fos.close();

        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new TemplateExportPackage(submission, "application/zip", "ProQuest UMI", pkg, null);
    }

    public Path getAbsolutePath(String relativePath) {
        return Paths.get(getPath(relativePath));
    }

    private String getPath(String relativePath) {
        String path = Application.BASE_PATH + relativePath;
        if (path.contains(":") && path.charAt(0) == '/') {
            path = path.substring(1, path.length());
        }
        return path;
    }

}
