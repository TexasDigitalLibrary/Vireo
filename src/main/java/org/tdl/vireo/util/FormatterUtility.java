package org.tdl.vireo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.formatter.Formatter;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class FormatterUtility {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public String renderManifest(Formatter formatter, Submission submission) throws Exception {
        return templateEngine.process(formatter.getTemplate(), formatter.craftContext(submission));
    }

}
