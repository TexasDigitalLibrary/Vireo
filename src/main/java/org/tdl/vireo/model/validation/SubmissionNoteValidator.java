package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class SubmissionNoteValidator extends BaseModelValidator {
    
    public SubmissionNoteValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Note requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Submission Note name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Submission Note name cannot be more than 100 characters", nameProperty, 100));
        
        String textProperty = "text";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Note requires a text", textProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Submission Note text must be at least 1 characters", textProperty, 1));
        
        String originatingWorkflowStepProperty = "originatingWorkflowStep";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Note requires an originating workflow step", originatingWorkflowStepProperty, true));
        
        String overrideableProperty = "overrideable";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Note requires an overrideable flag", overrideableProperty, true));
    }
    
}
