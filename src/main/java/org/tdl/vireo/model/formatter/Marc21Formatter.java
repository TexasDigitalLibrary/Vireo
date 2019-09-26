package org.tdl.vireo.model.formatter;

import java.util.HashMap;

import javax.persistence.Entity;

import org.tdl.vireo.model.Submission;
import org.thymeleaf.context.Context;

@Entity
public class Marc21Formatter extends MarcXML21Formatter {

    public Marc21Formatter() {
        super();
        setName("marc21");
        HashMap<String, String> templates = new HashMap<String, String>();
        templates.put("submission", "marc21");
        setTemplates(templates);
    }

    @Override
    public void populateContext(Context context, Submission submission) {
        super.populateContext(context, submission);
        MarcBuilder marcBuilder = new MarcBuilder();
        context.setVariable("marcBuilder", marcBuilder);
        context.setVariable("SUBMITTER_FULL_NAME", submissionHelperUtility.getStudentFullName());
        context.setVariable("SUBMITTER_SHORT_NAME", submissionHelperUtility.getStudentShortName());
        context.setVariable("GRANTOR", submissionHelperUtility.getGrantor());
        context.setVariable("ABSTRACT", submissionHelperUtility.getAbstract());
        context.setVariable("KEYWORDS", submissionHelperUtility.getKeywordFieldValues());
    }

    @Override
    public String getSuffix() {
        return ".mrc";
    }

    @Override
    public String getTemplateMode() {
        return "TEXT";
    }

}
