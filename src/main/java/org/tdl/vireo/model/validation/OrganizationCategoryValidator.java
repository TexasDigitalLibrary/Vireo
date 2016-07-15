package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class OrganizationCategoryValidator extends BaseModelValidator {
    
    public OrganizationCategoryValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Organization Category requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Organization Category name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Organization Category name cannot be more than 100 characters", nameProperty, 100));
    }
    
}
