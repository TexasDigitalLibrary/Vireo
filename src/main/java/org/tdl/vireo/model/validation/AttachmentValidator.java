package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class AttachmentValidator extends BaseModelValidator {
    
    public AttachmentValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Attachment requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Attachment name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Attachment name cannot be more than 255 characters", nameProperty, 255));
        
        String typeProperty = "type";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Attachment requires a type", typeProperty, true));
        
        String dateProperty = "date";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Attachment requires a date", dateProperty, true));
    }
    
}
