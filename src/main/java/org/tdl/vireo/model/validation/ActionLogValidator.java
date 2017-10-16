package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class ActionLogValidator extends BaseModelValidator {

    public ActionLogValidator() {
        String submissionProperty = "submission";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Action Log requires a submission", submissionProperty, true));

        String submissionStatusProperty = "submissionStatus";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Action Log requires a submission status", submissionStatusProperty, true));

        String userProperty = "user";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Action Log requires a user", userProperty, true));

        String entryProperty = "entry";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Action Log requires a entry", entryProperty, true));

        String privateFlagProperty = "privateFlag";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Action Log requires a private flag", privateFlagProperty, true));
    }

}
