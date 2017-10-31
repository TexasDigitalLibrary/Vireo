package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class VocabularyWordValidator extends BaseModelValidator {

    public VocabularyWordValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Vocabulary Word requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Vocabulary Word name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Vocabulary Word name cannot be more than 255 characters", nameProperty, 255));
    }

}
