package org.tdl.vireo.model.formatter;

import org.tdl.vireo.model.Submission;
import org.thymeleaf.context.Context;

public interface Formatter {

    public String getName();

    public String getTemplate();

    public void populateContext(Context context, Submission submission);

}
