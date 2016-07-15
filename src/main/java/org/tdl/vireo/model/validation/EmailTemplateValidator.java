package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class EmailTemplateValidator extends BaseModelValidator {
    
    public EmailTemplateValidator() {
        
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Email Template requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Email Template name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Email Template name cannot be more than 50 characters", nameProperty, 50));
        
        String subjectProperty = "subject";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Email Template requires a subject", subjectProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Email Template subject must be at least 2 characters", subjectProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Email Template subject cannot be more than 100 characters", subjectProperty, 100));
        
        String messageProperty = "message";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Email Template requires a message", messageProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Email Template message must be at least 2 characters", messageProperty, 2));
        
        String isSystemRequiredProperty = "isSystemRequired";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Email Template requires a system required flag", isSystemRequiredProperty, true));
        
    }
    
}
