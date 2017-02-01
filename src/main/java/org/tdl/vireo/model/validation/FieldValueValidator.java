package org.tdl.vireo.model.validation;

import org.tdl.vireo.model.SubmissionFieldProfile;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class FieldValueValidator extends BaseModelValidator {
    
    public FieldValueValidator(SubmissionFieldProfile submissionFieldProfile) {
    	    	
        String predicateProperty = "fieldPredicate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Value requires a predicate", predicateProperty, true));
        
        String valueProperty = "value";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "Value is not a valid email address", valueProperty, submissionFieldProfile.getInputType().getValidationPatern()));
        
    }
    
}
