package org.tdl.vireo.model.packager;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.ExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.MINIMAL_CLASS)
public interface Packager {

    public String getName();

    public AbstractFormatter getFormatter();

    public ExportPackage packageExport(String manifest, Submission submission);

}
