package org.tdl.vireo.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;

@Service
@SuppressWarnings("unused")
public class TemplateUtility {

    @Value("${app.url}")
    private String url;

    private static final String FULL_NAME = "FULL_NAME";
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String LAST_NAME = "LAST_NAME";
    private static final String DOCUMENT_TITLE = "DOCUMENT_TITLE";
    private static final String SUBMISSION_TYPE = "SUBMISSION_TYPE";
    private static final String DEPOSIT_URI = "DEPOSIT_URI";
    private static final String STUDENT_URL = "STUDENT_URL";
    private static final String SUBMISSION_URL = "SUBMISSION_URL";
    private static final String ADVISOR_URL = "ADVISOR_URL";
    private static final String SUBMISSION_STATUS = "SUBMISSION_STATUS";
    private static final String SUBMISSION_ASSIGNED_TO = "SUBMISSION_ASSIGNED_TO";
    private static final String REGISTRATION_URL = "REGISTRATION_URL";

    private final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private final static SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy");

    public String templateParameters(String content, Map<String, String> parameters) {
        for (String name : parameters.keySet()) {
            content = content.replaceAll("\\{" + name + "\\}", parameters.get(name));
        }
        return content;
    }

    public String templateParameters(String content, String[][] parameters) {
        for (String[] parameter : parameters) {
            content = content.replaceAll("\\{" + parameter[0] + "\\}", parameter[1]);
        }
        return content;
    }

    public String compileTemplate(EmailTemplate emailTemplate, Submission submission) {
        return compileString(emailTemplate.getMessage(), submission);
    }

    public String compileString(String preCompiled, Submission submission) {

        User submitter = submission.getSubmitter();

        String compiled = preCompiled
                .replaceAll("\\{" + FULL_NAME + "\\}", submitter.getSetting("displayName"))
                .replaceAll("\\{" + FIRST_NAME + "\\}", submitter.getFirstName())
                .replaceAll("\\{" + LAST_NAME + "\\}", submitter.getLastName())

                // TODO: We should use a url builder service to create/retrieve these.
                .replaceAll("\\{" + STUDENT_URL + "\\}", url + "/submission/" + submission.getId() + "/view")
                .replaceAll("\\{" + SUBMISSION_URL + "\\}", url + "/submission/" + submission.getId())
                .replaceAll("\\{" + ADVISOR_URL + "\\}", submission.getAdvisorReviewURL())

                .replaceAll("\\{" + DOCUMENT_TITLE + "\\}", findValue("dc.title", submission))
                .replaceAll("\\{" + SUBMISSION_TYPE + "\\}", findValue("submission_type", submission));

                // This is being handled elswhere and may not be useful, since
                // sending this uri from an email workflow rule seems illogical.
                // This is because these rule trigger from submission state changes
                // and a user must be registered already to have a submission.
                //.replaceAll("\\{" + REGISTRATION_URL + "\\}", REGISTRATION_URL);

        //This template is often used before a DepositURL is set for the particular submission so we should check for null values
        if (submission.getDepositURL() != null) {
            compiled = compiled.replaceAll("\\{" + DEPOSIT_URI + "\\}", submission.getDepositURL());
        }

        if (submission.getSubmissionStatus() != null) {
            compiled = compiled.replaceAll("\\{" + SUBMISSION_STATUS + "\\}", submission.getSubmissionStatus().getName());
        }

        if (submission.getAssignee() != null) {
            compiled = compiled.replaceAll("\\{" + SUBMISSION_ASSIGNED_TO + "\\}", submission.getAssignee().getSetting("displayName"));
        }

        compiled = replacePredicates(compiled, submission);

        return compiled;
    }

    private String replacePredicates(String compiled, Submission submission) {

        for (SubmissionWorkflowStep sws : submission.getSubmissionWorkflowSteps()) {

            for (SubmissionFieldProfile afp : sws.getAggregateFieldProfiles()) {

                String predicateKey = afp.getFieldPredicate().getValue();
                String fieldValue = findValue(predicateKey, submission);

                try {
                    fieldValue = monthYearFormat.format(dateTimeFormat.parse(fieldValue));
                } catch (ParseException e) {
                    // most fieldValues are likely not dates and will generate parse exceptions.
                }

                compiled = compiled.replaceAll("\\{" + predicateKey + "\\}", fieldValue);
            };

        };

        return compiled;
    }

    private String findValue(String predicateKey, Submission submission) {
        return submission.getFieldValuesByPredicateValue(predicateKey)
            .stream()
            .map(v-> { return v.getValue(); })
            .collect(Collectors.joining(", "));
    }

}
