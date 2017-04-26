package org.tdl.vireo.model.validation;

import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.SubmissionFieldProfile;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class FieldValueValidator extends BaseModelValidator {

    public FieldValueValidator(SubmissionFieldProfile submissionFieldProfile) {

        String predicateProperty = "fieldPredicate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Value requires a predicate", predicateProperty, true));

        InputType inputType = submissionFieldProfile.getInputType();
        String valueProperty = "value";

        if (inputType.getValidationPatern() != null) {
            String validationMessage = inputType.getValidationMessage() != null ? inputType.getValidationMessage() : "Field is not a valid format";
            this.addInputValidator(new InputValidator(InputValidationType.pattern, validationMessage, valueProperty, submissionFieldProfile.getInputType().getValidationPatern()));
        }
        if (!submissionFieldProfile.getOptional() && !inputType.getName().equals("INPUT_CHECKBOX")) {
            this.addInputValidator(new InputValidator(InputValidationType.minlength, "Required fields cannot be empty", valueProperty, 1));
        }

    }

}
