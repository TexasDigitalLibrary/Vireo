package org.tdl.vireo.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.formatter.Formatter;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class FormatterUtility {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public String renderManifest(Formatter formatter, Submission submission) throws Exception {
        Context context = new Context(Locale.getDefault());
        formatter.populateContext(context, submission);
        return templateEngine.process(formatter.getTemplate(), context);
    }

}
