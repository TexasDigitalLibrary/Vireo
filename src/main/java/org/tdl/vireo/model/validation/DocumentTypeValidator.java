package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class DocumentTypeValidator extends BaseModelValidator {
    
    public DocumentTypeValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Document Type requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Document Type name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Document Type name cannot be more than 255 characters", nameProperty, 255));
    }
    
}
