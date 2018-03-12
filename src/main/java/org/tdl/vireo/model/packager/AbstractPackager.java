package org.tdl.vireo.model.packager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

import org.tdl.vireo.exception.UnsupportedFormatterException;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;
import org.tdl.vireo.utility.FileHelperUtility;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractPackager<EP extends ExportPackage> extends BaseEntity implements Packager<EP> {

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    public AbstractFormatter formatter;

    @Column(unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return "application/zip";
    }

    public String getFileExtension() {
        return "zip";
    }

    public AbstractFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(AbstractFormatter formatter) {
        this.formatter = formatter;
    }

    protected Path getAbsolutePath(String relativePath) {
        return Paths.get(FileHelperUtility.getPath(relativePath));
    }

    public EP packageExport(Submission submission, List<SubmissionListColumn> columns) throws UnsupportedFormatterException {
        throw new UnsupportedFormatterException("Exporter does not support submission list columns!");
    }

}
