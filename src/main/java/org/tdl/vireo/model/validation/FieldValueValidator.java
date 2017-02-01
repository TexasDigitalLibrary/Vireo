package org.tdl.vireo.model.validation;

import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.SubmissionFieldProfile;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class FieldValueValidator extends BaseModelValidator {
    
    public FieldValueValidator(SubmissionFieldProfile fieldProfile) {
    	
    	System.out.println(fieldProfile);
    	
        String predicateProperty = "predicate";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Field Value requires a predicate", predicateProperty, true));
    }
    
}
