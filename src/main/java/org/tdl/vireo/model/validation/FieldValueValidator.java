package org.tdl.vireo.model.validation;

import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.SubmissionFieldProfile;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class FieldValueValidator extends BaseModelValidator {

    public FieldValueValidator(SubmissionFieldProfile submissionFieldProfile) {

        String predicateProperty = "fieldPredicate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Value requires a predicate", predicateProperty, true));

        InputType inputType = submissionFieldProfile.getInputType();
        String valueProperty = "value";

        if (inputType.getValidationPattern() != null) {
            String validationMessage = inputType.getValidationMessage() != null ? inputType.getValidationMessage() : "Field is not a valid format";
            this.addInputValidator(new InputValidator(InputValidationType.pattern, validationMessage, valueProperty, submissionFieldProfile.getInputType().getValidationPattern()));
        }
        if (!submissionFieldProfile.getOptional()) {
            this.addInputValidator(new InputValidator(InputValidationType.minlength, "Required fields cannot be empty", valueProperty, 1));
        }

    }

}
