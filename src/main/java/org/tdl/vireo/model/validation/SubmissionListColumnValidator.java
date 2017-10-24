package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

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
