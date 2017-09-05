package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
        setName("ProQuest");
    }

    public ProQuestUmiPackager(AbstractFormatter formatter) {
        this();
        setFormatter(formatter);
    }

    @Override
    public ExportPackage packageExport(String manifest, Submission submission) {

        String mimeType = null;
        String format = "ProQuest";
        String manifestName = "proquest.xml";

        File pkg = null;
        try {

            pkg = File.createTempFile("ProQuest", ".zip");

            mimeType = "application/zip";

            FileOutputStream fos = new FileOutputStream(pkg);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Copy the manifest
            File manifestFile = File.createTempFile(manifestName, null);
            FileUtils.writeStringToFile(manifestFile, manifest, "UTF-8");

            ZipEntry ze = new ZipEntry(manifestName);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(manifestFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }

            in.close();
            zos.closeEntry();

            manifestFile.delete();

            List<FieldValue> documentFieldValues = submission.getAllDocumentFieldValues();
            for (FieldValue documentFieldValue : documentFieldValues) {

                // TODO: add file whitelist for publish

                String fileName = documentFieldValue.getFileName();

                File exportFile = File.createTempFile(fileName, null);

                FileUtils.copyFile(getAbsolutePath(documentFieldValue.getValue()).toFile(), exportFile);

                ze = new ZipEntry(fileName);
                zos.putNextEntry(ze);
                in = new FileInputStream(exportFile);

                while ((len = in.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }

                in.close();
                zos.closeEntry();

            }

            zos.close();
            fos.close();

        } catch (IOException ioe) {
            throw new RuntimeException("Unable to generate package", ioe);
        }

        return new TemplateExportPackage(submission, mimeType, format, pkg, null);
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
