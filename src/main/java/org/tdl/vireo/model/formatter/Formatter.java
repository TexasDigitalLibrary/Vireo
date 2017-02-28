package org.tdl.vireo.model.formatter;

import org.tdl.vireo.model.Submission;
import org.thymeleaf.context.Context;

public interface Formatter {
    
    public String getName();
    
    public Context craftContext(Submission submission);

}
