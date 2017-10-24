package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class DegreeValidator extends BaseModelValidator {

    public DegreeValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Degree requires a name", nameProperty, true));

        String levelProperty = "level";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Degree requires a level", levelProperty, true));
    }

}
