package org.tdl.vireo.model.formatter;

import javax.persistence.Entity;

import org.tdl.vireo.model.Submission;
import org.thymeleaf.context.Context;

@Entity
public class ExcelFormatter extends AbstractFormatter {

    public ExcelFormatter() {
        super();
        setName("Excel");
    }

    @Override
    public void populateContext(Context context, Submission submission) {
        // NOTE: no context required
    }

}
