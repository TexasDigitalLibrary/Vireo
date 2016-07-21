package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class SubmissionListColumnValidator extends BaseModelValidator {
    
    public SubmissionListColumnValidator() {
        String labelProperty = "label";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission list column requires a label", labelProperty, true));
        
        String sortProperty = "sort";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission list column requires a sort", sortProperty, true));
        
        String pathProperty = "path";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission list column requires a path", pathProperty, true));                
    }
    
}
