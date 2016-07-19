package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class SubmissionViewColumnValidator extends BaseModelValidator {
    
    public SubmissionViewColumnValidator() {
        String labelProperty = "label";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission view column requires a label", labelProperty, true));
        
        String sortProperty = "sort";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission view column requires a sort", sortProperty, true));
        
        String pathProperty = "path";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission view column requires a path", pathProperty, true));                
    }
    
}
