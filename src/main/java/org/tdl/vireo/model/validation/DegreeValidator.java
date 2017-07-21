package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class DegreeValidator extends BaseModelValidator {

    public DegreeValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Degree requires a name", nameProperty, true));

        String levelProperty = "level";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Degree requires a level", levelProperty, true));
    }

}
