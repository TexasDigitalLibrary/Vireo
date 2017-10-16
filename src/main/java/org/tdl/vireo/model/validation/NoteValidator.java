package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class NoteValidator extends BaseModelValidator {

    public NoteValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Note requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Note name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Note name cannot be more than 100 characters", nameProperty, 100));

        String textProperty = "text";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Note requires a text", textProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Note text must be at least 1 characters", textProperty, 1));

        // String originatingWorkflowStepProperty = "originatingWorkflowStep";
        // this.addInputValidator(new InputValidator(InputValidationType.required, "Note requires an originating workflow step", originatingWorkflowStepProperty, true));

        String overrideableProperty = "overrideable";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Note requires an overrideable flag", overrideableProperty, true));
    }

}
