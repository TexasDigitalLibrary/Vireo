package org.tdl.vireo.model.packager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ZipExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

@Entity
public class DSpaceSimplePackager extends AbstractPackager<ZipExportPackage> {

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
    public ZipExportPackage packageExport(Submission submission, String manifest) {
//System.out.println("SUBMISSION "+submission.getId());
//System.out.println("MANIFEST"+manifest);

        String packageName = "submission-" + submission.getId() + "-";
        String manifestName = "manifest.xml";

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

/***/
            // Create Contents file
			String contentsFileName = "contents";
            File contentsFile = File.createTempFile(contentsFileName, null);
System.out.println("AAAA");
            List<FieldValue> documentFieldValuesForContent = submission.getAllDocumentFieldValues();
            for (FieldValue documentFieldValue : documentFieldValuesForContent) {
System.out.println("FKEY FFF "+documentFieldValue.getFileName()+" FFF "+submission.getPrimaryDocumentFieldValue().getFieldPredicate().getValue());
            	FileUtils.writeStringToFile(contentsFile,documentFieldValue.getFileName()+"\tbundle:CONTENT", "UTF-8");
				System.out.println("BBBB "+documentFieldValue.getFieldPredicate().getValue().equals("_doctype_primary"));
            }


            // Add manifest to zip
            zos.putNextEntry(new ZipEntry(contentsFileName));
            zos.write(Files.readAllBytes(contentsFile.toPath()));
            zos.closeEntry();

            contentsFile.delete();
/***/

            List<FieldValue> documentFieldValues = submission.getAllDocumentFieldValues();
            for (FieldValue documentFieldValue : documentFieldValues) {

                // TODO: add file whitelist for publish

                File exportFile = getAbsolutePath(documentFieldValue.getValue()).toFile();

                zos.putNextEntry(new ZipEntry(documentFieldValue.getFileName()));
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

}
