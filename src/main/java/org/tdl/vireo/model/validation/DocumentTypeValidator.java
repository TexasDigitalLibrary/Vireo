package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class DocumentTypeValidator extends BaseModelValidator {

    public DocumentTypeValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Document Type requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Document Type name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Document Type name cannot be more than 255 characters", nameProperty, 255));
    }

}
