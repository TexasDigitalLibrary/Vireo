package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class AttachmentTypeValidator extends BaseModelValidator {

    public AttachmentTypeValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Attachment Type requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Attachment Type name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Attachment Type name cannot be more than 255 characters", nameProperty, 255));
    }

}
