package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class VocabularyWordValidator extends BaseModelValidator {
    
    public VocabularyWordValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Vocabulary Word requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Vocabulary Word name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Vocabulary Word name cannot be more than 255 characters", nameProperty, 255));
    }
    
}
