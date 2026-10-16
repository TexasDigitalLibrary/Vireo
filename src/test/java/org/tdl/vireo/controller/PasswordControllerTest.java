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
 * Server-side enforcement, targeting and leak-containment assertions for {@code POST /password/change}.
 *
 * Validates: Requirements 2.5, 2.6, 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 6.4, 7.6, 10.6, 13.2, 13.3, 14.4, 16.9
 *
 * One parameterized test carries Properties 12 (change arm), 13, 14 and 26 (change arm), because
 * they share a single setup: a request against an Account under some availability state. Every case
 * pins the same outcomes. The stored password is replaced exactly when the availability conjunction,
 * the eligibility rule, the current-password check and the candidate validator all pass. A rejected
 * case leaves the stored password byte-identical and produces no write, no notification, no encode
 * and no email. Only the authenticated Account is ever written. No password or token value appears
 * in the response or the log.
 *
 * BCrypt is real only where a case succeeds, since that is where the test asserts the persisted
 * value verifies against the new password. Every other case uses a cheap prefix-based stand-in for
 * the stored hash, which discriminates match from mismatch identically.
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

    /**
     * The raw Password_Login_Setting and {@code auth.pass.change.enabled} pairs: both on, each arm
     * off on its own, both off, the trimmed and mixed-case accepting spellings, absent values, and
     * the near-miss strings an administrator might expect to mean true.
     */
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

    /**
     * Candidate and confirmation pairs: accepted, the length 6 and length 5 boundary, mismatched,
     * absent, whitespace-only, and an accepted value carrying surrounding whitespace.
     */
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

    /**
     * Identifier key names an implementation might read out of the request body to pick a target
     * Account. None is honored; the authenticated principal is the only source of the target.
     */
    private static final String[] FOREIGN_IDENTIFIER_KEYS = {
        "email", "username", "userId", "id", "user", "netid", "targetEmail", "account", "principal"
    };

    /** Identifier value shapes, paired with the key names in rotation. */
    private static final String[] FOREIGN_IDENTIFIER_VALUES = {
        "foreign@institution.edu", "manager@institution.edu", "email1", "2", "1", "99999", null, "", "   ", "netid1", "foreign@institution.edu ", "EMAIL1"
    };

    /** Secret shapes used as the new password in one case and as a wrong current password in another. */
    private static final String[] SECRET_SHAPES = { "plainsecret", " spaced secret ", "p@ss|word.{x}", "Ünïcödé-sëcret", "0123456789abcdef", "UPPER-lower-123" };

    /**
     * What a client might assert about its own rendering, carried in the request body. None of it
     * is read: the server re-derives availability from the configuration store and eligibility from
     * the stored Account fields on every request.
     */
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
        // Requirement 5 criterion 5: an authentication-failure result, nothing written.
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
        // Requirement 5 criterion 6: every rejection cause the shared validator reports.
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
        // Requirement 5 criteria 4, 8: the baseline authenticated role, matching
        // /user/settings/update. An unauthenticated or under-privileged caller is turned away by
        // Spring Security before the handler body runs, which is what this annotation expresses.
        Method handler = PasswordController.class.getMethod("changePassword", User.class, Map.class);

        PreAuthorize preAuthorize = handler.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize, "the handler must carry a @PreAuthorize expression");
        assertEquals("hasRole('ROLE_STUDENT')", preAuthorize.value());

        RequestMapping requestMapping = handler.getAnnotation(RequestMapping.class);

        assertNotNull(requestMapping);
        assertEquals("/change", requestMapping.value()[0]);
        assertEquals(RequestMethod.POST, requestMapping.method()[0]);
    }

    // Feature: user-password-reset, Property 12 (change arm): For any ineligible Account shape and
    // any setting pair that resolves the capability unavailable, a Password_Change_Request is
    // rejected, the stored password is left unchanged, and no email is sent. The reset and
    // redemption arms join this property in tasks 12 and 13.
    //
    // Feature: user-password-reset, Property 13: For any combination of availability state,
    // Account eligibility shape, current-password match or mismatch, and candidate password
    // validity, the stored password is replaced exactly when all four conditions hold; and whenever
    // it is replaced, the persisted value verifies against the submitted new password and does not
    // verify against the previous password.
    //
    // Feature: user-password-reset, Property 14: For any Password_Change_Request body carrying
    // arbitrary foreign Account identifiers, only the stored password of the authenticated Account
    // is written.
    //
    // Feature: user-password-reset, Property 26 (change arm): For any password value and any token
    // value, the /password/change response (status, message, payload) and the log output written
    // while handling the request do not carry it. The password-changed notification arm is in
    // CredentialNotificationServiceTest; the reset, redemption and email-change arms are added in
    // later tasks, each as new scenarios in the case list and new artefacts in the check below.
    //
    // Validates: Requirements 2.5, 2.6, 5.1, 5.2, 5.3, 5.5, 5.6, 5.7, 6.4, 7.6, 7.8, 7.9, 9.7, 9.8, 10.6, 13.2, 13.3, 14.4
    //
    // Every request carries the token entries and the client-asserted entries alongside the three
    // honored keys, and the repository lookups by email, netid and id are all stubbed to return a
    // foreign Account, so a handler that honored any of them would be observable.
    @ParameterizedTest(name = "[{index}] {8}")
    @MethodSource("provideChangeCases")
    public void changeEnforcesItsGatesTargetsThePrincipalAndLeaksNothing(String passwordLoginRaw, String changeRaw, String storedShape, String netid, String currentSupplied, String newPassword, String confirm, Map<String, String> extraFields, String label) throws Exception {
        // The four conditions of the conjunction, derived from the requirement text rather than
        // from the handler, so the expectation is independent of the implementation's order.
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

            // Only the authenticated Account was written or notified, and nothing else was looked up.
            verify(userRepo).update(user1);
            verifyNoMoreInteractions(userRepo);

            verify(credentialNotificationService).notifyPasswordChanged(user1);
            verifyNoMoreInteractions(credentialNotificationService);
        } else {
            assertRejected(response);

            // No credential material is produced on a rejected path, and no email leaves by any route.
            verify(cryptoService, never()).encodePassword(anyString());
            verifyNoInteractions(mockEmailService);
        }

        assertEquals("foreign-stored-password", foreignUser.getPassword(), "the Account named by the body must be left unchanged: " + label);

        assertNoSecretLeaked(response, log, label, currentSupplied, newPassword, confirm, TOKEN, storedPassword, user1.getPassword());
    }

    /**
     * The cases for the change arm. Every case is a nine-tuple of the raw Password_Login_Setting
     * value, the raw {@code auth.pass.change.enabled} value, the stored-password shape, the netid,
     * the supplied current password, the candidate, the confirmation, extra request body entries,
     * and a label.
     *
     * Each block walks one dimension through its boundary values with the others held at a shape
     * that succeeds on its own, so each is pinned as individually load-bearing. The both-failing
     * block crosses the two server-side gates, so a handler that returned early on one gate and
     * skipped the other is still caught. The foreign-identifier block runs accepted requests with
     * identifier entries that point at another Account. The secret block runs unusual password
     * values through the accepting path and the current-password-mismatch path. Client-asserted
     * body entries are rotated across the first blocks.
     *
     * @return The generated cases.
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

    /**
     * Neither a password value nor a token value may appear in the serialized response, its
     * message, its payload, or any log line written while handling the request.
     */
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

    /**
     * Run the handler with the controller's logger lowered to ALL, since the test configuration
     * pins org.tdl.vireo at ERROR and the handler logs its rejections at DEBUG, collecting what it
     * writes into {@code lines}. The level is restored afterwards.
     */
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

    /**
     * The server's boolean resolution, restated from Requirement 1 criterion 13 rather than called
     * through the service, so the property's expectation does not inherit a defect in it.
     */
    private static boolean resolvesTrue(String raw) {
        return raw != null && raw.trim().equalsIgnoreCase("true");
    }

    /**
     * The three outcomes every rejection case pins: INVALID, an unchanged stored password, and no
     * notification dispatch. No write reaches the repository either.
     */
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

    /**
     * Stub the two settings the change conjunction reads. Lenient, because the conjunction
     * short-circuits on the Password_Login_Setting.
     */
    private void stubSettings(String passwordLoginEnabled, String changeEnabled) {
        lenient().when(configurationRepo.getValueByNameAndType(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, APPLICATION_TYPE)).thenReturn(passwordLoginEnabled);
        lenient().when(configurationRepo.getValueByNameAndType(ConfigurationName.APPLICATION_AUTH_PASS_CHANGE_ENABLED, APPLICATION_TYPE)).thenReturn(changeEnabled);
    }

    /**
     * Route the mocked {@code CryptoService} onto the real encoder, so verification and encoding
     * are the genuine operations. Used only where a case succeeds.
     */
    private void stubRealCrypto() {
        lenient().when(cryptoService.validatePassword(any(), any())).thenAnswer(invocation -> invocation.getArgument(0) != null && passwordEncoder.matches(invocation.<String>getArgument(0), invocation.<String>getArgument(1)));
        lenient().when(cryptoService.encodePassword(anyString())).thenAnswer(invocation -> passwordEncoder.encode(invocation.<String>getArgument(0)));
    }

    /**
     * A cheap stand-in for hashing: a stored value matches exactly when it is the prefix plus the
     * supplied value, so a match and a mismatch are told apart without paying for BCrypt.
     */
    private void stubFakeCrypto() {
        lenient().when(cryptoService.validatePassword(any(), any())).thenAnswer(invocation -> invocation.getArgument(0) != null && (FAKE_PREFIX + invocation.<String>getArgument(0)).equals(invocation.<String>getArgument(1)));
        lenient().when(cryptoService.encodePassword(anyString())).thenAnswer(invocation -> FAKE_PREFIX + invocation.<String>getArgument(0));
    }

}
