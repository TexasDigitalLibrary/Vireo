package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class CustomActionDefinitionValidator extends BaseModelValidator {

    public CustomActionDefinitionValidator() {
        String labelProperty = "label";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Custom Action Definition requires a label", labelProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Custom Action Definition label must be at least 2 characters", labelProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Custom Action Definition label cannot be more than 50 characters", labelProperty, 50));

        String isStudentVisibleProperty = "isStudentVisible";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Custom Action Definition requires a student visible flag", isStudentVisibleProperty, true));
    }

}
