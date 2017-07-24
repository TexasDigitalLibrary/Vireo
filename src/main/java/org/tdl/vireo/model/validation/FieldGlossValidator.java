package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class FieldGlossValidator extends BaseModelValidator {

    public FieldGlossValidator() {
        String valueProperty = "value";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Gloss requires a value", valueProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Field Gloss value must be at least 1 characters", valueProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Field Gloss value cannot be more than 255 characters", valueProperty, 255));

        String languageProperty = "language";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Gloss requires a language", languageProperty, true));
    }

}
