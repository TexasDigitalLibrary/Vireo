package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class CustomActionValueValidator extends BaseModelValidator {
    
    public CustomActionValueValidator() {
        String submissionProperty = "submission";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Custom Action Value requires a submission", submissionProperty, true));
        
        String definitionProperty = "definition";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Custom Action Value requires a definition", definitionProperty, true));
        
        String valueProperty = "value";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Custom Action Value requires a value", valueProperty, true));
    }
    
}
