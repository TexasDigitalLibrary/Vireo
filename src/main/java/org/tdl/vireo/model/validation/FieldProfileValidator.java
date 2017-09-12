package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class FieldProfileValidator extends BaseModelValidator {

    public FieldProfileValidator() {
        
    	String defautValueProperty = "defaultValue";
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Field Profile requires a field predicate", defautValueProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Field Profile requires a field predicate", defautValueProperty, 140));
    	
    	String fieldPredicateProperty = "fieldPredicate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Profile requires a field predicate", fieldPredicateProperty, true));

        String inputTypeProperty = "inputType";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Profile requires a input type", inputTypeProperty, true));

        String repeatableProperty = "repeatable";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Profile requires a repeatable flag", repeatableProperty, true));

        String optionalProperty = "optional";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Profile requires an optional flag", optionalProperty, true));
    }

}
