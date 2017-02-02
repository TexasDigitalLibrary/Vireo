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
        if(submissionFieldProfile.getInputType().getValidationPatern() != null) {
        	this.addInputValidator(new InputValidator(InputValidationType.pattern, "Value is not a valid email address", valueProperty, submissionFieldProfile.getInputType().getValidationPatern()));
        }
        
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Value must be at least 5 characters", valueProperty, 5));

    }
    
}
