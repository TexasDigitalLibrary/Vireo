package org.tdl.vireo.model.packager;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

public interface Packager {

    public String getName();
    
    public AbstractFormatter getFormatter();
    
    public ExportPackage packageExport(String manifest, Submission submission);

}
