package org.tdl.vireo.model.formatter;

import org.tdl.vireo.model.Submission;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.MINIMAL_CLASS)
public interface Formatter {

    public String getName();

    public String getTemplate();

    public void populateContext(Context context, Submission submission);

}
