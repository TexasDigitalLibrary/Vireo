package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.util.ValidationUtility;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class CommitteeMemberValidator extends BaseModelValidator {
    
    public CommitteeMemberValidator() {
        String firstNameProperty = "firstName";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Committee Member requires a first name", firstNameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Committee Member first name must be at least 2 characters", firstNameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Committee Member first name cannot be more than 255 characters", firstNameProperty, 255));
        
        String lastNameProperty = "lastName";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Committee Member requires a last name", lastNameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Committee Member last name must be at least 2 characters", lastNameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Committee Member last name cannot be more than 255 characters", lastNameProperty, 255));
        
        String emailProperty = "email";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Committee Member requires an email", emailProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Committee Member email must be at least 2 characters", emailProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Committee Member email cannot be more than 255 characters", emailProperty, 255));
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "Not a valid email", emailProperty, ValidationUtility.EMAIL_REGEX));
    }
    
}
