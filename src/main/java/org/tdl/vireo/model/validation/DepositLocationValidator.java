package org.tdl.vireo.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.utility.ValidationUtility;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class DepositLocationValidator extends BaseModelValidator {

    public DepositLocationValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Deposit Location name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Deposit Location name cannot be more than 255 characters", nameProperty, 255));

        String repositoryProperty = "repository";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a repository", repositoryProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Deposit Location repository must be at least 2 characters", repositoryProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Deposit Location repository cannot be more than 255 characters", repositoryProperty, 255));
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "Must be an url", repositoryProperty, ValidationUtility.URL_REGEX));

        String collectionProperty = "collection";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a collection", collectionProperty, true));

        String usernameProperty = "username";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a username", usernameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Deposit Location username must be at least 2 characters", usernameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Deposit Location username cannot be more than 255 characters", usernameProperty, 255));

        String passwordProperty = "password";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a password", passwordProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Deposit Location password must be at least 2 characters", passwordProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Deposit Location password cannot be more than 255 characters", passwordProperty, 255));

        String onBehalfOfProperty = "onBehalfOf";
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Deposit Location onBehalfOf must be at least 2 characters", onBehalfOfProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Deposit Location onBehalfOf cannot be more than 255 characters", onBehalfOfProperty, 255));

        String packagerProperty = "packager";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a packager", packagerProperty, true));

        String depositorProperty = "depositor";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a depositor", depositorProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Deposit Location depositor must be at least 2 characters", depositorProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Deposit Location depositor cannot be more than 255 characters", depositorProperty, 255));

        String timeoutProperty = "timeout";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Deposit Location requires a timeout", timeoutProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "Must be an integer", timeoutProperty, ValidationUtility.INTEGER_REGEX));

    }

}