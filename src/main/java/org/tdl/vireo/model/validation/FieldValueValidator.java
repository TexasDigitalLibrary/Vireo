package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class FieldValueValidator extends BaseModelValidator {
    
    public FieldValueValidator() {
        String predicateProperty = "predicate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Value requires a predicate", predicateProperty, true));
    }
    
}
