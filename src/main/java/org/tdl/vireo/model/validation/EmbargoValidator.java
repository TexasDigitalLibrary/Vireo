package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class EmbargoValidator extends BaseModelValidator {

    public EmbargoValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Embargo requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Embargo name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Embargo name cannot be more than 255 characters", nameProperty, 255));

        String descriptionProperty = "description";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Embargo requires a description", descriptionProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Embargo description must be at least 2 characters", descriptionProperty, 2));

        String isActiveProperty = "isActive";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Embargo requires an active flag", isActiveProperty, true));

        String isSystemRequiredProperty = "isSystemRequired";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Embargo requires a system required flag", isSystemRequiredProperty, true));

        String guarantorProperty = "guarantor";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Embargo requires a guarantor", guarantorProperty, true));
    }

}
