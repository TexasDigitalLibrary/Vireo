package org.tdl.vireo.utility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.formatter.Formatter;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;

@Service
public class FormatterUtility {

    @Resource(name = "templateResolver")
    private SpringResourceTemplateResolver resolver;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public Map<String, String> renderManifestMap(Formatter formatter, Submission submission) throws Exception {
        resolver.setSuffix(formatter.getSuffix());
        resolver.setTemplateMode(formatter.getTemplateMode());
        Map<String, String> renderMap = new HashMap<String, String>();
        Context context = new Context(Locale.getDefault());
        formatter.populateContext(context, submission);
        Map<String, String> templates = formatter.getTemplates();
        for (Map.Entry<String, String> template : templates.entrySet()) {
            renderMap.put(template.getKey(), templateEngine.process(template.getValue(), context));
        }
        return renderMap;
    }

}
