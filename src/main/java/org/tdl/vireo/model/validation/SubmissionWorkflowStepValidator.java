package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class SubmissionWorkflowStepValidator extends BaseModelValidator {

    public SubmissionWorkflowStepValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Workflow Step requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Submission Workflow Step name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Submission Workflow Step name cannot be more than 255 characters", nameProperty, 255));

        String overrideableProperty = "overrideable";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Workflow Step requires an flag", overrideableProperty, true));

        String originatingOrganizationProperty = "originatingOrganization";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission Workflow Step requires an originating organization", originatingOrganizationProperty, true));
    }

}
