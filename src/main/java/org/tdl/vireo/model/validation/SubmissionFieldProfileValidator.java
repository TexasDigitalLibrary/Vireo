package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class SubmissionFieldProfileValidator extends BaseModelValidator {

    public SubmissionFieldProfileValidator() {
        String predicateProperty = "predicate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Field Profile requires a predicate", predicateProperty, true));

        String inputTypeProperty = "inputType";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Field Profile requires a input type", inputTypeProperty, true));

        String repeatableProperty = "repeatable";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Field Profile requires a repeatable flag", repeatableProperty, true));

        String optionalProperty = "optional";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Field Profile requires an optional flag", optionalProperty, true));
    }

}
