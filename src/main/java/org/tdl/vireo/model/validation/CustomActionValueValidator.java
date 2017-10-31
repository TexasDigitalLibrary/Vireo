package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class CustomActionValueValidator extends BaseModelValidator {

    public CustomActionValueValidator() {
        String definitionProperty = "definition";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Custom Action Value requires a definition", definitionProperty, true));

        String valueProperty = "value";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Custom Action Value requires a value", valueProperty, true));
    }

}
