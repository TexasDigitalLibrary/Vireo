package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class ManagedConfigurationValidator extends BaseModelValidator {

    public ManagedConfigurationValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Managed Configuration requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Managed Configuration name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Managed Configuration name cannot be more than 255 characters", nameProperty, 255));

        String valueProperty = "value";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Managed Configuration requires a value", valueProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Managed Configuration value must be at least 2 characters", valueProperty, 2));

        String typeProperty = "type";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Managed Configuration requires a type", typeProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Managed Configuration type must be at least 2 characters", typeProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Managed Configuration type cannot be more than 255 characters", typeProperty, 255));

    }

}
