package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.tdl.vireo.config.constant.ConfigurationName;

import edu.tamu.weaver.validation.results.ValidationResults;

public class ManagedConfigurationTest extends AbstractModelTest<ManagedConfiguration> {

    @InjectMocks
    private ManagedConfiguration managedConfiguration;

    @Override
    protected ManagedConfiguration getInstance() {
        return managedConfiguration;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("value", "value"),
            Arguments.of("type", "value"),
            Arguments.of("name", "value")
        );
    }

    protected static Stream<Arguments> provideValidationParameters() {
        return Stream.of(
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "1", "application", true),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "9", "application", true),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "24", "application", true),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, String.valueOf(Integer.MAX_VALUE), "application", true),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, null, "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, " ", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "0", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "-1", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "1.5", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "one", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "2147483648", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "1", "other", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "1", "application", false),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "10", "application", true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidationParameters")
    public void validatesPasswordResetExpiryWithoutWeakeningOtherSettings(String name, String value,
            String type, boolean expectedValid) {
        ManagedConfiguration configuration = new ManagedConfiguration(name, value, type);

        ValidationResults results = configuration.validate(configuration);

        assertEquals(expectedValid, results.isValid());
        if (!expectedValid) {
            assertTrue(results.getMessages().containsKey("value"));
        }
    }

    @Test
    public void expiryExceptionDoesNotAlterGenericValidation() {
        ManagedConfiguration configuration = new ManagedConfiguration(
            ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "1", "application");

        assertTrue(configuration.validate(configuration).isValid());

        configuration.setName(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED);

        ValidationResults results = configuration.validate(configuration);
        assertEquals(false, results.isValid());
        assertTrue(results.getMessages().containsKey("value"));
    }

}
