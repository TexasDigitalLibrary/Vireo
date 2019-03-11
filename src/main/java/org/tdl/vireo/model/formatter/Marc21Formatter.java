package org.tdl.vireo.model.formatter;

import java.util.HashMap;

import javax.persistence.Entity;

import org.tdl.vireo.model.Submission;
import org.thymeleaf.context.Context;

@Entity
public class Marc21Formatter extends AbstractFormatter {

    public Marc21Formatter() {
        super();
        setName("marc21");
        HashMap<String, String> templates = new HashMap<String, String>();
        templates.put(DEFAULT_TEMPLATE_KEY, "marc21");
        setTemplates(templates);
    }

    @Override
    public void populateContext(Context context, Submission submission) {
        super.populateContext(context, submission);
        MarcStringBuilder leaderBuilder = new MarcStringBuilder();
        context.setVariable("leaderBuilder", leaderBuilder);
    }
}
