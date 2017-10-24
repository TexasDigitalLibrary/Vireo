package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class EmailWorkflowRuleValidator extends BaseModelValidator {

    public EmailWorkflowRuleValidator() {
        String submissionStatusProperty = "submissionStatus";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Email Workflow Rule requires a submission status", submissionStatusProperty, true));

        String emailTemplateProperty = "emailTemplate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Email Workflow Rule requires an email template", emailTemplateProperty, true));

    }

}
