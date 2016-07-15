package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.util.ValidationUtility;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class UserValidator extends BaseModelValidator {
    
    public UserValidator() {
        String emailProperty = "email";
        this.addInputValidator(new InputValidator(InputValidationType.required, "User requires an email", emailProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "User email must be at least 1 characters", emailProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "User email cannot be more than 255 characters", emailProperty, 255));
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "Invalid email", emailProperty, ValidationUtility.EMAIL_REGEX));
        
        String firstNameProperty = "firstName";
        this.addInputValidator(new InputValidator(InputValidationType.required, "User requires a first name", firstNameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "User first name must be at least 1 characters", firstNameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "User first name cannot be more than 255 characters", firstNameProperty, 255));
        
        String lastNameProperty = "lastName";
        this.addInputValidator(new InputValidator(InputValidationType.required, "User requires a last name", lastNameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "User last name must be at least 1 characters", lastNameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "User last name cannot be more than 255 characters", lastNameProperty, 255));
    }
    
}
