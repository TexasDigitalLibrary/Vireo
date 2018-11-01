package org.tdl.vireo.model.export;

import org.tdl.vireo.model.Submission;

public interface ExportPackage {

    public Submission getSubmission();

    public String getFormat();

    public Object getPayload();

    public boolean isFile();

    public boolean isMap();

}
