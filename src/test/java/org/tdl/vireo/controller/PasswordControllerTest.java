package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.CredentialNotificationService;
import org.tdl.vireo.service.PasswordFeatureAvailabilityService;
import org.tdl.vireo.utility.PasswordValidator;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import edu.tamu.weaver.validation.results.ValidationResults;
import edu.tamu.weaver.validation.utility.ValidationUtility;

/**
 * Tests for {@code POST /password/change}: gating, principal targeting and leak containment.
 *
 * BCrypt is real only where a case succeeds; other cases use a cheap prefix-based fake hash.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
public class PasswordControllerTest extends AbstractControllerTest {

    private static final String APPLICATION_TYPE = "application";

    private static final String CURRENT_PASSWORD = "currentpassword";

    private static final String NEW_PASSWORD = "newpassword";

    private static final String TOKEN = "tok.eyJ0b2tlbiI6Im5ldmVyLWxlYWstbWUifQ";

    /** The sentinel standing for the stored hash of {@link #CURRENT_PASSWORD}. */
    private static final String HASHED_STORED_PASSWORD = "HASHED_STORED_PASSWORD";

    /** A genuine BCrypt hash of {@link #CURRENT_PASSWORD}, computed once for the class. */
    private static final String REAL_HASH = new BCryptPasswordEncoder().encode(CURRENT_PASSWORD);

    private static final String FAKE_PREFIX = "fake-hash:";

    private static final String FAKE_HASH = FAKE_PREFIX + CURRENT_PASSWORD;

    /** Raw password-login and {@code auth.pass.change.enabled} setting pairs, including near-miss spellings. */
    private static final String[][] AVAILABILITY_PAIRS = {
        { "true", "true" },
        { " TRUE ", "True" },
        { "true", "false" },
        { "false", "true" },
        { "false", "false" },
        { null, "true" },
        { "true", null },
        { "yes", "true" },
        { "true", "1" },
        { "", "true" },
        { "true", "   " },
        { "truex", "true" }
    };

    /** Stored-password shape paired with netid: the eligible shape, then each ineligible one. */
    private static final String[][] ELIGIBILITY_SHAPES = {
        { HASHED_STORED_PASSWORD, null },
        { HASHED_STORED_PASSWORD, "netid1" },
        { HASHED_STORED_PASSWORD, "" },
        { "", null },
        { "   ", null }
    };

    /** The supplied current password: the matching value, then each non-matching shape. */
    private static final String[] SUPPLIED_CURRENT_PASSWORDS = {
        CURRENT_PASSWORD,
        "notthecurrentpassword",
        "",
        " currentpassword ",
        "CURRENTPASSWORD"
    };

    /** Candidate and confirmation pairs, including the length 5/6 boundary. */
    private static final String[][] CANDIDATE_PAIRS = {
        { NEW_PASSWORD, NEW_PASSWORD },
        { "abcdef", "abcdef" },
        { "abcde", "abcde" },
        { NEW_PASSWORD, "newpasswordx" },
        { null, null },
        { NEW_PASSWORD, null },
        { "      ", "      " },
        { "  padded  ", "  padded  " }
    };

    /** Body keys that must be ignored; the authenticated principal is the only target. */
    private static final String[] FOREIGN_IDENTIFIER_KEYS = {
        "email", "username", "userId", "id", "user", "netid", "targetEmail", "account", "principal"
    };

    /** Identifier value shapes, paired with the key names in rotation. */
    private static final String[] FOREIGN_IDENTIFIER_VALUES = {
        "foreign@institution.edu", "manager@institution.edu", "email1", "2", "1", "99999", null, "", "   ", "netid1", "foreign@institution.edu ", "EMAIL1"
    };

    /** Secret shapes used as the new password in one case and as a wrong current password in another. */
    private static final String[] SECRET_SHAPES = { "plainsecret", " spaced secret ", "p@ss|word.{x}", "Ünïcödé-sëcret", "0123456789abcdef", "UPPER-lower-123" };

    /** Client-asserted body entries; the server must ignore them and re-derive availability and eligibility. */
    private static final List<Map<String, String>> CLIENT_CLAIM_BODIES = Arrays.asList(
        new HashMap<String, String>(),
        claim("passwordEligible", "true"),
        claim("changePasswordAvailable", "true"),
        claim("eligible", "true"),
        claim(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, "true"),
        claim(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, "true"),
        claim("force", "true"),
        claim("netid", null)
    );

    private static Map<String, String> claim(String key, String value) {
        Map<String, String> body = new HashMap<>();
        body.put(key, value);

        return body;
    }

    @Mock
    private ConfigurationRepo configurationRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CredentialNotificationService credentialNotificationService;

    @InjectMocks
    private PasswordController passwordController;

    private PasswordFeatureAvailabilityService availabilityService;

    private User user1;

    private String storedPassword;

    @BeforeEach
    public void setup() {
        availabilityService = new PasswordFeatureAvailabilityService();

        ReflectionTestUtils.setField(availabilityService, "configurationRepo", configurationRepo);
        ReflectionTestUtils.setField(passwordController, "availabilityService", availabilityService);

        user1 = new User("email1", "firstname1", "lastname1", Role.ROLE_STUDENT);
        user1.setId(1L);

        storedPassword = FAKE_HASH;

        user1.setPassword(storedPassword);
        user1.setNetid(null);
    }

    @Test
    public void testChangePasswordRejectsACurrentPasswordMismatch() {
        stubSettings("true", "true");
        stubFakeCrypto();

        ApiResponse response = passwordController.changePassword(user1, request("notthecurrentpassword", NEW_PASSWORD, NEW_PASSWORD));

        assertRejected(response);

        Object payload = response.getPayload().get("ValidationResults");

        assertNotNull(payload, "a current-password mismatch reports an authentication failure through ValidationResults");
        assertTrue(payload instanceof ValidationResults);
        assertTrue(((ValidationResults) payload).getMessages().containsKey(ValidationUtility.BUSINESS_MESSAGE_KEY), "the failure rides under the business message key, matching AuthController.login");
    }

    @Test
    public void testChangePasswordRejectsACandidateTheValidatorRejects() {
        List<String[]> rejectedCandidates = Arrays.asList(
            new String[] { null, null },
            new String[] { "   ", "   " },
            new String[] { "short", "short" },
            new String[] { NEW_PASSWORD, "newpasswordx" }
        );

        for (String[] candidate : rejectedCandidates) {
            setup();

            stubSettings("true", "true");
            stubFakeCrypto();

            ApiResponse response = passwordController.changePassword(user1, request(CURRENT_PASSWORD, candidate[0], candidate[1]));

            assertRejected(response);

            assertEquals(PasswordValidator.messageFor(PasswordValidator.validate(candidate[0], candidate[1])), response.getMeta().getMessage(), "the rejection carries the shared validator message for the cause");
        }
    }

    @Test
    public void testChangePasswordRequiresTheStudentRole() throws Exception {
        // Spring Security rejects under-privileged callers before the handler runs.
        Method handler = PasswordController.class.getMethod("changePassword", User.class, Map.class);

        PreAuthorize preAuthorize = handler.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize, "the handler must carry a @PreAuthorize expression");
        assertEquals("hasRole('ROLE_STUDENT')", preAuthorize.value());

        RequestMapping requestMapping = handler.getAnnotation(RequestMapping.class);

        assertNotNull(requestMapping);
        assertEquals("/change", requestMapping.value()[0]);
        assertEquals(RequestMethod.POST, requestMapping.method()[0]);
    }

    // The password is replaced only when availability, eligibility, current-password match and
    // candidate validity all hold. Otherwise nothing is written, only the principal is ever
    // written, and no password or token leaks into the response or log. Lookups by email, netid
    // and id return a foreign Account so a handler honoring body identifiers would be caught.
    @ParameterizedTest(name = "[{index}] {8}")
    @MethodSource("provideChangeCases")
    public void changeEnforcesItsGatesTargetsThePrincipalAndLeaksNothing(String passwordLoginRaw, String changeRaw, String storedShape, String netid, String currentSupplied, String newPassword, String confirm, Map<String, String> extraFields, String label) throws Exception {
        // Expectation derived independently of the handler's evaluation order.
        boolean hashed = HASHED_STORED_PASSWORD.equals(storedShape);
        boolean available = resolvesTrue(passwordLoginRaw) && resolvesTrue(changeRaw);
        boolean eligible = netid == null && storedShape != null && !storedShape.trim().isEmpty();
        boolean currentMatches = eligible && hashed && CURRENT_PASSWORD.equals(currentSupplied);
        boolean candidateAccepted = PasswordValidator.validate(newPassword, confirm) == PasswordValidator.Result.ACCEPTED;
        boolean succeeds = available && eligible && currentMatches && candidateAccepted;

        stubSettings(passwordLoginRaw, changeRaw);

        if (succeeds) {
            stubRealCrypto();
        } else {
            stubFakeCrypto();
        }

        storedPassword = hashed ? (succeeds ? REAL_HASH : FAKE_HASH) : storedShape;

        user1.setPassword(storedPassword);
        user1.setNetid(netid);

        User foreignUser = new User("foreign@institution.edu", "firstname2", "lastname2", Role.ROLE_STUDENT);
        foreignUser.setId(2L);
        foreignUser.setPassword("foreign-stored-password");

        lenient().when(userRepo.findByEmail(anyString())).thenReturn(foreignUser);
        lenient().when(userRepo.findByNetid(anyString())).thenReturn(foreignUser);
        lenient().when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(foreignUser));
        lenient().when(userRepo.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> data = request(currentSupplied, newPassword, confirm);
        data.put("token", TOKEN);
        data.put("resetToken", TOKEN);
        data.putAll(extraFields);

        List<String> log = new ArrayList<>();
        ApiResponse response = capturingLog(log, () -> passwordController.changePassword(user1, data));

        if (succeeds) {
            assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), label);

            assertFalse(storedPassword.equals(user1.getPassword()), "the stored password must have been replaced: " + label);
            assertTrue(passwordEncoder.matches(newPassword, user1.getPassword()), "the replaced password must verify against the submitted new password: " + label);
            assertFalse(passwordEncoder.matches(CURRENT_PASSWORD, user1.getPassword()), "the replaced password must not verify against the previous password: " + label);

            // Only the principal is written or notified.
            verify(userRepo).update(user1);
            verifyNoMoreInteractions(userRepo);

            verify(credentialNotificationService).notifyPasswordChanged(user1);
            verifyNoMoreInteractions(credentialNotificationService);
        } else {
            assertRejected(response);

            // A rejection encodes nothing and sends no email.
            verify(cryptoService, never()).encodePassword(anyString());
            verifyNoInteractions(mockEmailService);
        }

        assertEquals("foreign-stored-password", foreignUser.getPassword(), "the Account named by the body must be left unchanged: " + label);

        assertNoSecretLeaked(response, log, label, currentSupplied, newPassword, confirm, TOKEN, storedPassword, user1.getPassword());
    }

    /**
     * Cases as (passwordLoginRaw, changeRaw, storedShape, netid, current, candidate, confirm, extraFields, label).
     * Each block varies one dimension while the others stay on a succeeding shape.
     */
    private static Stream<Arguments> provideChangeCases() {
        List<Arguments> cases = new ArrayList<>();

        for (String[] availability : AVAILABILITY_PAIRS) {
            addCase(cases, "availability " + availability[0] + "/" + availability[1], availability[0], availability[1], HASHED_STORED_PASSWORD, null, CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        }

        for (String[] eligibility : ELIGIBILITY_SHAPES) {
            addCase(cases, "eligibility " + eligibility[0] + "/" + eligibility[1], "true", "true", eligibility[0], eligibility[1], CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        }

        for (String supplied : SUPPLIED_CURRENT_PASSWORDS) {
            addCase(cases, "current password '" + supplied + "'", "true", "true", HASHED_STORED_PASSWORD, null, supplied, NEW_PASSWORD, NEW_PASSWORD);
        }

        for (String[] candidate : CANDIDATE_PAIRS) {
            addCase(cases, "candidate '" + candidate[0] + "'/'" + candidate[1] + "'", "true", "true", HASHED_STORED_PASSWORD, null, CURRENT_PASSWORD, candidate[0], candidate[1]);
        }

        for (String[] availability : AVAILABILITY_PAIRS) {
            if (resolvesTrue(availability[0]) && resolvesTrue(availability[1])) {
                continue;
            }

            for (String[] eligibility : ELIGIBILITY_SHAPES) {
                if (eligibility[1] == null && HASHED_STORED_PASSWORD.equals(eligibility[0])) {
                    continue;
                }

                addCase(cases, "both gates failing " + availability[0] + "/" + availability[1] + " " + eligibility[0] + "/" + eligibility[1], availability[0], availability[1], eligibility[0], eligibility[1], CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
            }
        }

        for (int i = 0; i < FOREIGN_IDENTIFIER_KEYS.length; i++) {
            Map<String, String> foreign = new HashMap<>();
            foreign.put(FOREIGN_IDENTIFIER_KEYS[i], FOREIGN_IDENTIFIER_VALUES[i % FOREIGN_IDENTIFIER_VALUES.length]);

            cases.add(Arguments.of("true", "true", HASHED_STORED_PASSWORD, null, CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD, foreign, "foreign " + foreign));
        }

        Map<String, String> allForeign = new HashMap<>();

        for (int i = 0; i < FOREIGN_IDENTIFIER_KEYS.length; i++) {
            allForeign.put(FOREIGN_IDENTIFIER_KEYS[i], FOREIGN_IDENTIFIER_VALUES[(i + 3) % FOREIGN_IDENTIFIER_VALUES.length]);
        }

        cases.add(Arguments.of("true", "true", HASHED_STORED_PASSWORD, null, CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD, allForeign, "foreign every key " + allForeign));

        for (String secret : SECRET_SHAPES) {
            addCase(cases, "secret as new password '" + secret + "'", "true", "true", HASHED_STORED_PASSWORD, null, CURRENT_PASSWORD, secret, secret);
            addCase(cases, "secret as wrong current password '" + secret + "'", "true", "true", HASHED_STORED_PASSWORD, null, secret, NEW_PASSWORD, NEW_PASSWORD);
        }

        return cases.stream();
    }

    private static void addCase(List<Arguments> cases, String label, String passwordLoginRaw, String changeRaw, String storedShape, String netid, String current, String newPassword, String confirm) {
        Map<String, String> claims = CLIENT_CLAIM_BODIES.get(cases.size() % CLIENT_CLAIM_BODIES.size());

        cases.add(Arguments.of(passwordLoginRaw, changeRaw, storedShape, netid, current, newPassword, confirm, claims, label + " claims=" + claims));
    }

    /** No secret may appear in the serialized response, its message, its payload, or the log. */
    private void assertNoSecretLeaked(ApiResponse response, List<String> log, String label, String... secrets) throws Exception {
        List<String> artefacts = new ArrayList<>(log);

        artefacts.add(new ObjectMapper().writeValueAsString(response));
        artefacts.add(String.valueOf(response.getMeta().getMessage()));
        artefacts.add(String.valueOf(response.getPayload()));

        for (String secret : secrets) {
            if (secret == null || secret.trim().isEmpty()) {
                continue;
            }

            for (String artefact : artefacts) {
                assertFalse(artefact.contains(secret), "No password or token value may appear in the response or the log, found '" + secret + "' in: " + artefact + " (" + label + ")");
                assertFalse(artefact.contains(secret.trim()), "No password or token value may appear in the response or the log, found '" + secret.trim() + "' in: " + artefact + " (" + label + ")");
            }
        }
    }

    /** Runs the handler with the logger at ALL (test config pins ERROR, rejections log at DEBUG) and collects log lines. */
    private ApiResponse capturingLog(List<String> lines, java.util.function.Supplier<ApiResponse> call) {
        Logger controllerLogger = (Logger) LoggerFactory.getLogger(PasswordController.class);
        Level originalLevel = controllerLogger.getLevel();
        ListAppender<ILoggingEvent> appender = new ListAppender<>();

        appender.start();

        controllerLogger.setLevel(Level.ALL);
        controllerLogger.setAdditive(false);
        controllerLogger.addAppender(appender);

        try {
            return call.get();
        } finally {
            controllerLogger.detachAppender(appender);
            controllerLogger.setAdditive(true);
            controllerLogger.setLevel(originalLevel);

            appender.stop();

            for (ILoggingEvent event : appender.list) {
                lines.add(event.getFormattedMessage());
            }
        }
    }

    /** Restated here rather than calling the service, so a service defect is not inherited. */
    private static boolean resolvesTrue(String raw) {
        return raw != null && raw.trim().equalsIgnoreCase("true");
    }

    /** A rejection is INVALID, leaves the password unchanged, and writes and notifies nothing. */
    private void assertRejected(ApiResponse response) {
        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
        assertEquals(storedPassword, user1.getPassword(), "the stored password must be left unchanged");

        verifyNoInteractions(userRepo);
        verifyNoInteractions(credentialNotificationService);
    }

    private Map<String, String> request(String currentPassword, String newPassword, String confirm) {
        Map<String, String> data = new HashMap<>();

        data.put("currentPassword", currentPassword);
        data.put("newPassword", newPassword);
        data.put("confirm", confirm);

        return data;
    }

    /** Lenient because the conjunction short-circuits on the password-login setting. */
    private void stubSettings(String passwordLoginEnabled, String changeEnabled) {
        lenient().when(configurationRepo.getValueByNameAndType(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, APPLICATION_TYPE)).thenReturn(passwordLoginEnabled);
        lenient().when(configurationRepo.getValueByNameAndType(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, APPLICATION_TYPE)).thenReturn(changeEnabled);
    }

    /** Routes the mocked {@code CryptoService} onto the real encoder; used only for succeeding cases. */
    private void stubRealCrypto() {
        lenient().when(cryptoService.validatePassword(any(), any())).thenAnswer(invocation -> invocation.getArgument(0) != null && passwordEncoder.matches(invocation.<String>getArgument(0), invocation.<String>getArgument(1)));
        lenient().when(cryptoService.encodePassword(anyString())).thenAnswer(invocation -> passwordEncoder.encode(invocation.<String>getArgument(0)));
    }

    /** Cheap hash stand-in: matches exactly when stored is the prefix plus the supplied value. */
    private void stubFakeCrypto() {
        lenient().when(cryptoService.validatePassword(any(), any())).thenAnswer(invocation -> invocation.getArgument(0) != null && (FAKE_PREFIX + invocation.<String>getArgument(0)).equals(invocation.<String>getArgument(1)));
        lenient().when(cryptoService.encodePassword(anyString())).thenAnswer(invocation -> FAKE_PREFIX + invocation.<String>getArgument(0));
    }

}
