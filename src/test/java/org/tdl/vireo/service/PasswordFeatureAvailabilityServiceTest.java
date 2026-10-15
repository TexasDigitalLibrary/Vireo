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
 * Property 6: Setting resolution and availability conjunction.
 *
 * Validates: Requirements 1.13, 1.14, 1.16, 1.18, 1.19
 *
 * The property is one {@link ParameterizedTest} fed by an {@link MethodSource} generator producing
 * well over one hundred cases, built from the enumerated boundary values crossed with one another
 * and padded from a fixed-seed {@link Random}. No property-based testing library is used and no new
 * Maven dependency is introduced.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
@ExtendWith(MockitoExtension.class)
public class PasswordFeatureAvailabilityServiceTest {

    private static final String APPLICATION_TYPE = "application";

    private static final long GENERATOR_SEED = 20250101L;

    private static final int RANDOM_CASE_COUNT = 48;

    /**
     * The enumerated raw setting strings. Every shape the design's Property 6 input domain names is
     * present: null, {@code "true"}, {@code "TRUE"}, {@code " true "}, {@code "False"}, {@code "1"},
     * {@code "yes"}, empty and whitespace-only.
     */
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

    /**
     * Each case is a triple of raw stored strings for the Password_Login_Setting,
     * {@code auth.pass.change.enabled} and {@code auth.pass.reset.enabled}.
     *
     * The enumerated cross product pins every pairing of the boundary values for the
     * Password_Login_Setting against a capability setting; the random cases vary all three
     * independently, which is what covers the asymmetric pairings where one capability is enabled
     * and the other is not.
     *
     * @return The generated cases.
     */
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

    /**
     * Draw either one of the enumerated boundary values or a junk string of random characters.
     *
     * @param random The fixed-seed source.
     *
     * @return The drawn raw value.
     */
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

    /**
     * The independently computed resolution rule: trim, then compare against {@code "true"} with
     * letter case ignored. An absent value resolves false.
     *
     * @param raw The raw stored value.
     *
     * @return The expected resolved value.
     */
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

        // Requirements 1.13, 1.14: resolution is trim plus case-insensitive comparison, and an
        // absent or unrecognised value resolves false.
        assertEquals(expectedEnabled, passwordFeatureAvailabilityService.isPasswordLoginEnabled(), "Resolution of the Password_Login_Setting");
        assertEquals(expectedChange, passwordFeatureAvailabilityService.isChangeEnabled(), "Resolution of auth.pass.change.enabled");
        assertEquals(expectedReset, passwordFeatureAvailabilityService.isResetEnabled(), "Resolution of auth.pass.reset.enabled");

        // Requirement 1.18: change is available exactly under its conjunction.
        assertEquals(expectedEnabled && expectedChange, passwordFeatureAvailabilityService.isChangeAvailable(), "Availability of self-service password change");

        // Requirement 1.19: reset is available exactly under its conjunction.
        assertEquals(expectedEnabled && expectedReset, passwordFeatureAvailabilityService.isResetAvailable(), "Availability of administrator-initiated reset");
    }

    /**
     * Requirement 1.16: an absent Password_Login_Setting leaves both capabilities unavailable, for
     * every value of the two capability settings.
     */
    @Test
    public void testAbsentPasswordLoginSettingLeavesBothCapabilitiesUnavailable() {
        stub(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, null);
        stub(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "true");
        stub(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "true");

        assertFalse(passwordFeatureAvailabilityService.isChangeAvailable(), "Change is unavailable when the Password_Login_Setting is absent");
        assertFalse(passwordFeatureAvailabilityService.isResetAvailable(), "Reset is unavailable when the Password_Login_Setting is absent");
    }

    /**
     * The enumerated raw expiry strings. Every boundary the design's Property 7 input domain names
     * is present: absent, empty, whitespace-only, the rejected lower neighbours {@code "0"} and
     * {@code "-1"}, the accepted lower bound {@code "1"}, the seeded default {@code "24"}, the
     * accepted upper bound {@link Integer#MAX_VALUE}, the rejected upper neighbour
     * {@code "2147483648"}, non-numeric shapes, and a whitespace-padded valid value.
     */
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

    /**
     * Each case is one raw stored string for {@code auth.pass.reset.expiryHours}.
     *
     * The enumerated boundary values are crossed with randomized valid and invalid draws from a
     * fixed-seed {@link Random}, giving well over one hundred cases in total.
     *
     * @return The generated cases.
     */
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

    /**
     * Draw either an enumerated boundary value, a randomized valid value in 1 to
     * {@link Integer#MAX_VALUE}, a randomized rejected integer at or below zero, or a randomized
     * non-numeric junk string.
     *
     * @param random The fixed-seed source.
     *
     * @return The drawn raw value.
     */
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

    /**
     * The independently computed resolution rule: trim, parse as an integer, and accept only a
     * value in 1 to {@link Integer#MAX_VALUE} inclusive. Every other input yields 24.
     *
     * @param raw The raw stored value.
     *
     * @return The expected resolved expiry in hours.
     */
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

    /**
     * Property 7: Expiry value resolution.
     *
     * Validates: Requirements 1.8
     *
     * A value parsing as an integer in 1 to {@link Integer#MAX_VALUE} inclusive resolves to itself;
     * every other value resolves to 24. On every input, rejected or accepted, the configuration
     * store sees the single read and no write whatsoever, so the stored value is left unchanged.
     *
     * @param raw The raw stored value under test.
     */
    @ParameterizedTest(name = "[{index}] expiryHours={0}")
    @MethodSource("provideRawExpiryValues")
    public void testExpiryValueResolution(String raw) {
        stub(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, raw);

        int expected = expectedExpiry(raw);

        assertEquals(expected, passwordFeatureAvailabilityService.getResetExpiryHours(), "Resolution of auth.pass.reset.expiryHours for raw value '" + raw + "'");

        // Requirement 1.8: the stored value is left unchanged. The resolution reads the setting
        // once and performs no write-back of the fallback value.
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

    /**
     * Stub a setting against a mutable holder, so the stored value can be flipped between requests
     * without re-stubbing.
     *
     * @param settingName The setting name.
     * @param holder The single-element holder carrying the current raw stored value.
     */
    private void stubDynamic(String settingName, String[] holder) {
        lenient().when(configurationRepo.getValueByNameAndType(eq(settingName), eq(APPLICATION_TYPE))).thenAnswer(invocation -> holder[0]);
    }

    /*
     * Task 1.6 — seeding, constant, and per-request resolution assertions.
     */

    private static final String SYSTEM_DEFAULTS_RESOURCE = "/settings/SYSTEM_Defaults.json";

    private static final String SHIBBOLETH_TYPE = "shibboleth";

    /**
     * The four seeded settings and the values Requirement 1 criteria 1, 2, 3 and 9 state.
     *
     * @return The generated cases.
     */
    private static Stream<Arguments> provideSeededDefaults() {
        return Stream.of(
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, "true"),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "false"),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "false"),
            Arguments.of(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "24")
        );
    }

    /**
     * Read the Default_Settings_Seed exactly as {@code DefaultSettingsService} reads it.
     *
     * @return The parsed seed.
     *
     * @throws IOException If the resource cannot be read.
     */
    private static JsonNode readSystemDefaults() throws IOException {
        try (InputStream stream = PasswordFeatureAvailabilityServiceTest.class.getResourceAsStream(SYSTEM_DEFAULTS_RESOURCE)) {
            assertNotNull(stream, "The Default_Settings_Seed resource " + SYSTEM_DEFAULTS_RESOURCE + " is on the classpath");

            // The seed carries a pre-existing multi-line string value, so raw control characters
            // inside a string are tolerated here exactly as the application's reader tolerates
            // them. Nothing about the seed file is changed by this test.
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

            return objectMapper.readTree(stream);
        }
    }

    /**
     * Resolve a seeded value the way {@code DefaultSettingsService.init} flattens the seed: the
     * entries under a top-level key are single-field objects, read through {@code asText()}.
     *
     * @param defaults The parsed seed.
     * @param type The top-level key, which is the configuration type.
     * @param name The setting name.
     *
     * @return The seeded value, or null when the setting is absent under that type.
     */
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

    /**
     * Requirements 1.1, 1.2, 1.3, 1.9, 1.10, 1.15: each of the four settings is seeded under the
     * {@code application} top-level key with the stated value, and under no other type.
     *
     * Seeding under the wrong type is invisible to {@code getSettingByNameAndType}, which filters
     * {@code getSettingsByType(type)}, so the type is asserted and not merely the presence.
     *
     * @param settingName The setting name under test.
     * @param expectedValue The value the requirement states.
     *
     * @throws IOException If the seed cannot be read.
     */
    @ParameterizedTest(name = "[{index}] {0} = {1}")
    @MethodSource("provideSeededDefaults")
    public void testSettingIsSeededUnderTheApplicationKey(String settingName, String expectedValue) throws IOException {
        JsonNode defaults = readSystemDefaults();

        assertEquals(expectedValue, seededValue(defaults, APPLICATION_TYPE, settingName), "Seeded default for '" + settingName + "' under the application key");

        assertNull(seededValue(defaults, SHIBBOLETH_TYPE, settingName), "'" + settingName + "' is not seeded under the shibboleth key");
    }

    /**
     * Requirement 1.4: each Password_Management_Setting is named in {@code ConfigurationName}, and
     * the already-declared constant for the Password_Login_Setting is reused unchanged.
     */
    @Test
    public void testConfigurationNameConstants() {
        assertEquals("auth.pass.enabled", ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, "The Password_Login_Setting constant");
        assertEquals("auth.pass.change.enabled", ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "The auth.pass.change.enabled constant");
        assertEquals("auth.pass.reset.enabled", ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, "The auth.pass.reset.enabled constant");
        assertEquals("auth.pass.reset.expiryHours", ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, "The auth.pass.reset.expiryHours constant");
    }

    /**
     * Requirement 1.5: every setting is resolved through
     * {@code configurationRepo.getValueByNameAndType(name, "application")} within the handling of
     * each request, and no resolved value is retained across requests.
     *
     * A value cached at startup or held in a static field would answer the second and later
     * requests with no read at all, so the read count scaling with the request count is what
     * separates conformance from a cache.
     */
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

    /**
     * Requirements 1.5, 1.6: a stored value that changes between two requests governs the second
     * request, with no application restart.
     *
     * Each of the three booleans is flipped on its own, so a service that happened to carry one
     * value over while re-reading the others cannot pass.
     */
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

    /**
     * Requirements 1.5, 1.6: the expiry value is likewise resolved per request, so an Administrator
     * save of a new expiry governs the next evaluation.
     */
    @Test
    public void testFlippedExpiryValueGovernsTheNextRequest() {
        String[] expiry = { "24" };

        stubDynamic(ConfigurationName.APPLICATION_AUTH_PASS_RESET_EXPIRY_HOURS, expiry);

        assertEquals(24, passwordFeatureAvailabilityService.getResetExpiryHours(), "The seeded expiry governs the first request");

        expiry[0] = "48";

        assertEquals(48, passwordFeatureAvailabilityService.getResetExpiryHours(), "The flipped expiry governs the second request");
    }

}
