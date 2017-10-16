package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class LanguageValidator extends BaseModelValidator {

    public LanguageValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Language requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Language name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Language name cannot be more than 50 characters", nameProperty, 50));
    }

}
