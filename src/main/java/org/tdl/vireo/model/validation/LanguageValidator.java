package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class LanguageValidator extends BaseModelValidator {
    
    public LanguageValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Language requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Language name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Language name cannot be more than 50 characters", nameProperty, 50));
    }
    
}
