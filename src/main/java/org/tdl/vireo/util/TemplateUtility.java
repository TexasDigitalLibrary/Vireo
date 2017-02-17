package org.tdl.vireo.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;

@Service
public class TemplateUtility {

    @Autowired
    private AppInfoUtility appInfoUtil;

    private static final String FULL_NAME = "FULL_NAME";
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String LAST_NAME = "LAST_NAME";
    private static final String DOCUMENT_TITLE = "DOCUMENT_TITLE";
    private static final String SUBMISSION_TYPE = "SUBMISSION_TYPE";
    private static final String DEPOSIT_URI = "DEPOSIT_URI";
    private static final String GRAD_SEMESTER = "GRAD_SEMESTER";
    private static final String STUDENT_URL = "STUDENT_URL";
    private static final String SUBMISSION_URL = "SUBMISSION_URL";
    private static final String ADVISOR_URL = "ADVISOR_URL";
    private static final String SUBMISSION_STATUS = "SUBMISSION_STATUS";
    private static final String SUBMISSION_ASSIGNED_TO = "SUBMISSION_ASSIGNED_TO";
    private static final String REGISTRATION_URL = "REGISTRATION_URL";

    public String templateParameters(String content, Map<String, String> parameters) {
        for (String name : parameters.keySet()) {
            content = content.replaceAll("\\{"+name+"\\}", parameters.get(name));
        }
        return content;
    }

    public String templateParameters(String content, String[][] parameters) {
        for(String[] parameter : parameters) {
            content = content.replaceAll("\\{"+parameter[0]+"\\}", parameter[1]);
        }
        return content;
    }

    public String compileTemplate(EmailTemplate emailTemplate, Submission submission)  {
        return compileString(emailTemplate.getMessage(), submission);
    }

    public String compileString(String preCompiled, Submission submission) {

        User submitter = submission.getSubmitter();

        String compiled = preCompiled
                .replaceAll("\\{"+FULL_NAME+"\\}", submitter.getSetting("displayName"))
                .replaceAll("\\{"+FIRST_NAME+"\\}", submitter.getFirstName())
                .replaceAll("\\{"+LAST_NAME+"\\}", submitter.getLastName())

                //TODO: We should use a url builder service to create/retrieve these.
                .replaceAll("\\{"+STUDENT_URL+"\\}", STUDENT_URL)
                .replaceAll("\\{"+SUBMISSION_URL+"\\}", SUBMISSION_URL)
                .replaceAll("\\{"+ADVISOR_URL+"\\}", appInfoUtil.getRunningAddress() + "/review/" + submission.getAdvisorAccessHash())
                .replaceAll("\\{"+DEPOSIT_URI+"\\}", DEPOSIT_URI)
                .replaceAll("\\{"+REGISTRATION_URL+"\\}", REGISTRATION_URL)

                //TODO: these are  field predicate and we will need a strategy to obtain this reliably.
                .replaceAll("\\{"+DOCUMENT_TITLE+"\\}", DOCUMENT_TITLE)
                .replaceAll("\\{"+SUBMISSION_TYPE+"\\}", SUBMISSION_TYPE)
                .replaceAll("\\{"+GRAD_SEMESTER+"\\}", GRAD_SEMESTER);


            if(submission.getSubmissionState() != null) {
                compiled = compiled.replaceAll("\\{"+SUBMISSION_STATUS+"\\}", submission.getSubmissionState().getName());
            };

            if(submission.getAssignee() != null) {
                compiled = compiled.replaceAll("\\{"+SUBMISSION_ASSIGNED_TO+"\\}", submission.getAssignee().getSetting("displayName"));
            };

        return compiled;
    }


}
