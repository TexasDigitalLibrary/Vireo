package org.tdl.vireo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for setting resolution, availability and expiry. Cases are boundary values plus
 * fixed-seed random draws, fed through {@link ParameterizedTest}.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
@ExtendWith(MockitoExtension.class)
public class PasswordFeatureAvailabilityServiceTest {

    private static final String APPLICATION_TYPE = "application";

    private static final long GENERATOR_SEED = 20250101L;

    private static final int RANDOM_CASE_COUNT = 48;

    /** Boundary raw setting strings. */
    private static final List<String> RAW_VALUES = Arrays.asList(
        null,
        "",
        " ",
        "\t\n",
        "true",
        "TRUE",
        "True",
        " true ",
        "\ttrue\n",
        "truex",
        "rue",
        "false",
        "False",
        " false ",
        "1",
        "0",
        "yes",
        "no",
        "on"
    );

    @Mock
    private ConfigurationRepo configurationRepo;

    @InjectMocks
    private PasswordFeatureAvailabilityService passwordFeatureAvailabilityService;

    /** Raw triples (enabled, change, reset); the random cases cover asymmetric change/reset pairings. */
    private static Stream<Arguments> provideRawSettingTriples() {
        List<Arguments> cases = new ArrayList<Arguments>();

        for (String enabledRaw : RAW_VALUES) {
            for (String capabilityRaw : RAW_VALUES) {
                cases.add(Arguments.of(enabledRaw, capabilityRaw, capabilityRaw));
            }
        }

        Random random = new Random(GENERATOR_SEED);

        for (int i = 0; i < RANDOM_CASE_COUNT; i++) {
            cases.add(Arguments.of(randomRawValue(random), randomRawValue(random), randomRawValue(random)));
        }

        return cases.stream();
    }

    /** Draws a boundary value or a random junk string. */
    private static String randomRawValue(Random random) {
        if (random.nextBoolean()) {
            return RAW_VALUES.get(random.nextInt(RAW_VALUES.size()));
        }

        int length = random.nextInt(8);

        StringBuilder junk = new StringBuilder();

        for (int i = 0; i < length; i++) {
            junk.append((char) ('!' + random.nextInt(90)));
        }

        return junk.toString();
    }

    /** Expected resolution: trimmed, case-insensitive {@code "true"}; null is false. */
    private static boolean expectedResolution(String raw) {
        return raw != null && raw.trim().equalsIgnoreCase("true");
    }

    @ParameterizedTest(name = "[{index}] enabled={0}, change={1}, reset={2}")
    @MethodSource("provideRawSettingTriples")
    public void testSettingResolutionAndAvailabilityConjunction(String enabledRaw, String changeRaw, String resetRaw) {
        stub(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, enabledRaw);
        stub(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, changeRaw);
        stub(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, resetRaw);

        boolean expectedEnabled = expectedResolution(enabledRaw);
        boolean expectedChange = expectedResolution(changeRaw);
        boolean expectedReset = expectedResolution(resetRaw);

        assertEquals(expectedEnabled, passwordFeatureAvailabilityService.isPasswordLoginEnabled(), "Resolution of the Password_Login_Setting");
        assertEquals(expectedChange, passwordFeatureAvailabilityService.isChangeEnabled(), "Resolution of auth.pass.change.enabled");
        assertEquals(expectedReset, passwordFeatureAvailabilityService.isResetEnabled(), "Resolution of auth.pass.reset.enabled");

        assertEquals(expectedEnabled && expectedChange, passwordFeatureAvailabilityService.isChangeAvailable(), "Availability of self-service password change");

        assertEquals(expectedEnabled && expectedReset, passwordFeatureAvailabilityService.isResetAvailable(), "Availability of administrator-initiated reset");
    }

    /** An absent login setting makes both capabilities unavailable. */
    @Test
    public void testAbsentPasswordLoginSettingLeavesBothCapabilitiesUnavailable() {
        stub(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, null);
        stub(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "true");
        stub(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "true");

        assertFalse(passwordFeatureAvailabilityService.isChangeAvailable(), "Change is unavailable when the Password_Login_Setting is absent");
        assertFalse(passwordFeatureAvailabilityService.isResetAvailable(), "Reset is unavailable when the Password_Login_Setting is absent");
    }

    /** Boundary raw expiry strings. */
    private static final List<String> RAW_EXPIRY_VALUES = Arrays.asList(
        null,
        "",
        "   ",
        "\t\n",
        "0",
        "-1",
        "1",
        "2",
        "24",
        String.valueOf(Integer.MAX_VALUE),
        "2147483648",
        "-2147483648",
        "99999999999999999999",
        "abc",
        "12.5",
        "1e3",
        "24abc",
        " 24 ",
        "+24",
        "0x18"
    );

    private static final int RANDOM_EXPIRY_CASE_COUNT = 96;

    /** Boundary expiry values plus seeded random draws. */
    private static Stream<Arguments> provideRawExpiryValues() {
        List<Arguments> cases = new ArrayList<Arguments>();

        for (String raw : RAW_EXPIRY_VALUES) {
            cases.add(Arguments.of(raw));
        }

        Random random = new Random(GENERATOR_SEED);

        for (int i = 0; i < RANDOM_EXPIRY_CASE_COUNT; i++) {
            cases.add(Arguments.of(randomRawExpiryValue(random)));
        }

        return cases.stream();
    }

    /** Draws a boundary, a valid, a non-positive or a junk expiry value. */
    private static String randomRawExpiryValue(Random random) {
        switch (random.nextInt(4)) {
            case 0:
                return RAW_EXPIRY_VALUES.get(random.nextInt(RAW_EXPIRY_VALUES.size()));
            case 1:
                return String.valueOf(1 + random.nextInt(Integer.MAX_VALUE));
            case 2:
                return String.valueOf(-random.nextInt(1000));
            default:
                int length = 1 + random.nextInt(8);

                StringBuilder junk = new StringBuilder();

                for (int i = 0; i < length; i++) {
                    junk.append((char) ('a' + random.nextInt(26)));
                }

                return junk.toString();
        }
    }

    /** Expected resolution: a trimmed integer of at least 1, otherwise the default of 24. */
    private static int expectedExpiry(String raw) {
        if (raw == null) {
            return PasswordFeatureAvailabilityService.DEFAULT_RESET_EXPIRY_HOURS;
        }

        int parsed;

        try {
            parsed = Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return PasswordFeatureAvailabilityService.DEFAULT_RESET_EXPIRY_HOURS;
        }

        if (parsed < 1) {
            return PasswordFeatureAvailabilityService.DEFAULT_RESET_EXPIRY_HOURS;
        }

        return parsed;
    }

    /** Expiry resolves per the rule, with a single read and no write-back to the store. */
    @ParameterizedTest(name = "[{index}] expiryHours={0}")
    @MethodSource("provideRawExpiryValues")
    public void testExpiryValueResolution(String raw) {
        stub(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, raw);

        int expected = expectedExpiry(raw);

        assertEquals(expected, passwordFeatureAvailabilityService.getResetExpiryHours(), "Resolution of auth.pass.reset.expiryHours for raw value '" + raw + "'");

        verify(configurationRepo, times(1)).getValueByNameAndType(eq(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS), eq(APPLICATION_TYPE));

        verify(configurationRepo, never()).create(anyString(), anyString(), anyString());
        verify(configurationRepo, never()).create(any(ManagedConfiguration.class));
        verify(configurationRepo, never()).save(any(ManagedConfiguration.class));
        verify(configurationRepo, never()).update(any(ManagedConfiguration.class));
        verify(configurationRepo, never()).reset(any(ManagedConfiguration.class));
        verify(configurationRepo, never()).delete(any(ManagedConfiguration.class));

        verifyNoMoreInteractions(configurationRepo);
    }

    private void stub(String settingName, String rawValue) {
        lenient().when(configurationRepo.getValueByNameAndType(eq(settingName), eq(APPLICATION_TYPE))).thenReturn(rawValue);
    }

    /** Stubs a setting against a mutable holder so the value can change between requests. */
    private void stubDynamic(String settingName, String[] holder) {
        lenient().when(configurationRepo.getValueByNameAndType(eq(settingName), eq(APPLICATION_TYPE))).thenAnswer(invocation -> holder[0]);
    }

    // Seeding, constant and per-request resolution assertions.

    private static final String SYSTEM_DEFAULTS_RESOURCE = "/settings/SYSTEM_Defaults.json";

    private static final String SHIBBOLETH_TYPE = "shibboleth";

    /** The four seeded settings and their expected default values. */
    private static Stream<Arguments> provideSeededDefaults() {
        return Stream.of(
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, "true"),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "false"),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "false"),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "24")
        );
    }

    /** Reads the settings seed the way {@code DefaultSettingsService} does. */
    private static JsonNode readSystemDefaults() throws IOException {
        try (InputStream stream = PasswordFeatureAvailabilityServiceTest.class.getResourceAsStream(SYSTEM_DEFAULTS_RESOURCE)) {
            assertNotNull(stream, "The Default_Settings_Seed resource " + SYSTEM_DEFAULTS_RESOURCE + " is on the classpath");

            // The seed has a multi-line string value, so tolerate raw control characters as the application does.
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

            return objectMapper.readTree(stream);
        }
    }

    /** Returns the seeded value for a setting under a type, or null when absent. */
    private static String seededValue(JsonNode defaults, String type, String name) {
        JsonNode entries = defaults.get(type);

        if (entries == null || !entries.isArray()) {
            return null;
        }

        for (JsonNode entry : entries) {
            if (entry.has(name)) {
                return entry.get(name).asText();
            }
        }

        return null;
    }

    /** Each setting is seeded under {@code application} only; a wrong type would be invisible to lookups by type. */
    @ParameterizedTest(name = "[{index}] {0} = {1}")
    @MethodSource("provideSeededDefaults")
    public void testSettingIsSeededUnderTheApplicationKey(String settingName, String expectedValue) throws IOException {
        JsonNode defaults = readSystemDefaults();

        assertEquals(expectedValue, seededValue(defaults, APPLICATION_TYPE, settingName), "Seeded default for '" + settingName + "' under the application key");

        assertNull(seededValue(defaults, SHIBBOLETH_TYPE, settingName), "'" + settingName + "' is not seeded under the shibboleth key");
    }

    /** The setting names are declared in {@code ConfigurationName}. */
    @Test
    public void testConfigurationNameConstants() {
        assertEquals("auth.pass.enabled", ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, "The Password_Login_Setting constant");
        assertEquals("auth.pass.change.enabled", ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "The auth.pass.change.enabled constant");
        assertEquals("auth.pass.reset.enabled", ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "The auth.pass.reset.enabled constant");
        assertEquals("auth.pass.reset.expiryHours", ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "The auth.pass.reset.expiryHours constant");
    }

    /** Every setting is read on every request; read counts scaling with requests rules out caching. */
    @Test
    public void testEverySettingIsResolvedOnEveryRequest() {
        stub(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, "true");
        stub(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "true");
        stub(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "true");
        stub(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "24");

        int requests = 5;

        for (int i = 0; i < requests; i++) {
            passwordFeatureAvailabilityService.isChangeAvailable();
            passwordFeatureAvailabilityService.isResetAvailable();
            passwordFeatureAvailabilityService.getResetExpiryHours();
        }

        // Both conjunctions read the Password_Login_Setting, so it is read twice per request.
        verify(configurationRepo, times(2 * requests)).getValueByNameAndType(eq(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED), eq(APPLICATION_TYPE));
        verify(configurationRepo, times(requests)).getValueByNameAndType(eq(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED), eq(APPLICATION_TYPE));
        verify(configurationRepo, times(requests)).getValueByNameAndType(eq(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED), eq(APPLICATION_TYPE));
        verify(configurationRepo, times(requests)).getValueByNameAndType(eq(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS), eq(APPLICATION_TYPE));

        verifyNoMoreInteractions(configurationRepo);
    }

    /** A changed stored value governs the next request; each boolean is flipped independently. */
    @Test
    public void testFlippedStoredValueGovernsTheNextRequest() {
        String[] enabled = { "true" };
        String[] change = { "true" };
        String[] reset = { "true" };

        stubDynamic(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, enabled);
        stubDynamic(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, change);
        stubDynamic(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, reset);

        assertTrue(passwordFeatureAvailabilityService.isChangeAvailable(), "Change is available on the first request");
        assertTrue(passwordFeatureAvailabilityService.isResetAvailable(), "Reset is available on the first request");

        change[0] = "false";

        assertFalse(passwordFeatureAvailabilityService.isChangeAvailable(), "The flipped auth.pass.change.enabled governs the second request");
        assertTrue(passwordFeatureAvailabilityService.isResetAvailable(), "Reset is unaffected by the change setting");

        change[0] = "true";
        reset[0] = "false";

        assertTrue(passwordFeatureAvailabilityService.isChangeAvailable(), "The restored auth.pass.change.enabled governs the next request");
        assertFalse(passwordFeatureAvailabilityService.isResetAvailable(), "The flipped auth.pass.reset.enabled governs the next request");

        reset[0] = "true";
        enabled[0] = "false";

        assertFalse(passwordFeatureAvailabilityService.isChangeAvailable(), "The flipped Password_Login_Setting governs the next request for change");
        assertFalse(passwordFeatureAvailabilityService.isResetAvailable(), "The flipped Password_Login_Setting governs the next request for reset");
    }

    /** A changed expiry value governs the next request. */
    @Test
    public void testFlippedExpiryValueGovernsTheNextRequest() {
        String[] expiry = { "24" };

        stubDynamic(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, expiry);

        assertEquals(24, passwordFeatureAvailabilityService.getResetExpiryHours(), "The seeded expiry governs the first request");

        expiry[0] = "48";

        assertEquals(48, passwordFeatureAvailabilityService.getResetExpiryHours(), "The flipped expiry governs the second request");
    }

}
