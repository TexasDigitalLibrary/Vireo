package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class InputTypeValidator extends BaseModelValidator {

    public InputTypeValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Input Type requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Input Type name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Input Type name cannot be more than 100 characters", nameProperty, 100));
    }

}
