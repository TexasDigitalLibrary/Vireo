package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class OrganizationValidator extends BaseModelValidator {

    public OrganizationValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Organization requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Organization name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Organization name cannot be more than 255 characters", nameProperty, 255));

        String categoryProperty = "category";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Organization requires an category", categoryProperty, true));
    }

}
