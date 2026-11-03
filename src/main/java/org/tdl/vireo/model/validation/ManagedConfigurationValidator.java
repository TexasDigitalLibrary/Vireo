package org.tdl.vireo.model.validation;

import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.ManagedConfiguration;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.model.ValidatingEntity;
import edu.tamu.weaver.validation.results.ValidationResults;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class ManagedConfigurationValidator extends BaseModelValidator {

    private static final String APPLICATION_TYPE = "application";

    private static final String VALUE_PROPERTY = "value";

    private static final String EXPIRY_VALIDATION_MESSAGE = "Password Reset Link Expiry must be a whole number from 1 through 2147483647";

    private final InputValidator valueMinimumLength;

    public ManagedConfigurationValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Managed Configuration requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Managed Configuration name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Managed Configuration name cannot be more than 255 characters", nameProperty, 255));

        this.addInputValidator(new InputValidator(InputValidationType.required, "Managed Configuration requires a value", VALUE_PROPERTY, true));
        valueMinimumLength = new InputValidator(InputValidationType.minlength, "Managed Configuration value must be at least 2 characters", VALUE_PROPERTY, 2);
        this.addInputValidator(valueMinimumLength);

        String typeProperty = "type";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Managed Configuration requires a type", typeProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Managed Configuration type must be at least 2 characters", typeProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxlength, "Managed Configuration type cannot be more than 255 characters", typeProperty, 255));

    }

    @Override
    public <U extends ValidatingEntity> ValidationResults validate(U model) {
        if (!(model instanceof ManagedConfiguration) || !isPasswordResetExpiry((ManagedConfiguration) model)) {
            return super.validate(model);
        }

        ValidationResults results;
        this.removeInputValidator(valueMinimumLength);

        try {
            results = super.validate(model);
        } finally {
            this.addInputValidator(valueMinimumLength);
        }

        ManagedConfiguration configuration = (ManagedConfiguration) model;
        if (!isPositiveInteger(configuration.getValue())) {
            results.setValid(false);
            results.addMessage(VALUE_PROPERTY, InputValidationType.pattern.name(), EXPIRY_VALIDATION_MESSAGE);
        }

        return results;
    }

    private static boolean isPasswordResetExpiry(ManagedConfiguration configuration) {
        return ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS.equals(configuration.getName())
            && APPLICATION_TYPE.equals(configuration.getType());
    }

    private static boolean isPositiveInteger(String value) {
        if (value == null || !value.matches("[1-9][0-9]*")) {
            return false;
        }

        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
