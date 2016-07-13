package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.util.ValidationUtility;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class GraduationMonthValidator extends BaseModelValidator {
    
    public GraduationMonthValidator() {
        String monthProperty = "month";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Graduation Month requires a month", monthProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "Not a valid month", monthProperty, ValidationUtility.MONTH_REGEX));
    }
    
}
