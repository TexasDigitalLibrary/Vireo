package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class DegreeLevelValidator extends BaseModelValidator {

    public DegreeLevelValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Degree level requires a name", nameProperty, true));
    }

}
