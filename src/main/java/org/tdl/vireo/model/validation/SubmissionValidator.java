package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class SubmissionValidator extends BaseModelValidator {
    
    public SubmissionValidator() {
        String submitterProperty = "submitter";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission requires a submitter", submitterProperty, true));
        
        String organizationProperty = "organization";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission requires an organization", organizationProperty, true));
        
    }
    
}
