package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class SubmissionStateValidator extends BaseModelValidator {
    
    public SubmissionStateValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission State requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Submission State name must be at least 1 characters", nameProperty, 1));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Submission State name cannot be more than 255 characters", nameProperty, 255));
        
        String isArchivedProperty = "isArchived";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission State requires an archived flag", isArchivedProperty, true));

        String isPublishableProperty = "isPublishable";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission State requires a publishable flag", isPublishableProperty, true));

        String isDeletableProperty = "isDeletable";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission State requires a deletable flag", isDeletableProperty, true));

        String isEditableByReviewerProperty = "isEditableByReviewer";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission State requires an editable by reviewer flag", isEditableByReviewerProperty, true));

        String isEditableByStudentProperty = "isEditableByStudent";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission State requires an editable by student flag", isEditableByStudentProperty, true));

        String isActiveProperty = "isActive";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Submission State requires an active flag", isActiveProperty, true));
        
    }
    
}
