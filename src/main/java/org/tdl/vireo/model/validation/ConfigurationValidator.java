package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class ConfigurationValidator extends BaseModelValidator {
    
    public ConfigurationValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Configuration requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Configuration name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Configuration name cannot be more than 255 characters", nameProperty, 255));
        
        String valueProperty = "value";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Configuration requires a value", valueProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Configuration value must be at least 2 characters", valueProperty, 2));
        
        String typeProperty = "type";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Configuration requires a type", typeProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Configuration type must be at least 2 characters", typeProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Configuration type cannot be more than 255 characters", typeProperty, 255));
        
        String isSystemRequiredProperty = "isSystemRequired";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Configuration requires a system required flag", isSystemRequiredProperty, true));
    }
    
}
