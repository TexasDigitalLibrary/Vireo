package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class FieldPredicateValidator extends BaseModelValidator {

    public FieldPredicateValidator() {
        String valueProperty = "value";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Predicate requires a value", valueProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Field Predicate value must be at least 1 characters", valueProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Field Predicate value cannot be more than 255 characters", valueProperty, 255));
    }

}
