package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class SubmissionValidator extends BaseModelValidator {

    public SubmissionValidator() {
        String submitterProperty = "submitter";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission requires a submitter", submitterProperty, true));

        String organizationProperty = "organization";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission requires an organization", organizationProperty, true));

    }

}
