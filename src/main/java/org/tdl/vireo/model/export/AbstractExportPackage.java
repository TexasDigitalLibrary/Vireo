package org.tdl.vireo.model.export;

import java.io.File;
import java.util.Map;

import org.tdl.vireo.model.Submission;

public abstract class AbstractExportPackage implements ExportPackage {

    public final Submission submission;

    public final String format;

    public final Object payload;

    public AbstractExportPackage(Submission submission, String format, Object payload) {
        this.submission = submission;
        this.format = format;
        this.payload = payload;
    }

    public Submission getSubmission() {
        return submission;
    }

    public String getFormat() {
        return format;
    }

    public Object getPayload() {
        return payload;
    }

    public boolean isFile() {
        return payload instanceof File;
    }

    public boolean isMap() {
        return payload instanceof Map;
    }

}
