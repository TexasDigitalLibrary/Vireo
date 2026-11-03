package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
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
import javax.mail.MessagingException;

import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.CredentialNotificationService;
import org.tdl.vireo.service.PasswordFeatureAvailabilityService;
import org.tdl.vireo.service.ResetTokenService;
import org.tdl.vireo.service.ResetTokenVerification;
import org.tdl.vireo.service.VireoEmailSender;
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
 * Tests for {@code /password/change}, {@code /password/reset} and {@code /password/reset/redeem}.
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

    @Mock
    private ResetTokenService resetTokenService;

    @Mock
    private VireoEmailSender emailSender;

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

    // -----------------------------------------------------------------------------------------
    // POST /password/reset: enforcement and authorization. Each rejection pins INVALID, no token
    // constructed, no email sent, and an unchanged stored password.
    // -----------------------------------------------------------------------------------------

    private static final String RESET_EMAIL = "email1";

    @Test
    public void testInitiateResetRequiresTheManagerRole() throws Exception {
        // Spring Security rejects under-privileged callers before the handler runs, so the role
        // check is enforced at the annotation level independently of what the client rendered.
        Method handler = PasswordController.class.getMethod("initiateReset", Map.class);

        PreAuthorize preAuthorize = handler.getAnnotation(PreAuthorize.class);

        assertNotNull(preAuthorize, "the handler must carry a @PreAuthorize expression");
        assertEquals("hasRole('ROLE_MANAGER')", preAuthorize.value());

        RequestMapping requestMapping = handler.getAnnotation(RequestMapping.class);

        assertNotNull(requestMapping);
        assertEquals("/reset", requestMapping.value()[0]);
        assertEquals(RequestMethod.POST, requestMapping.method()[0]);
    }

    @Test
    public void testInitiateResetRejectsAnIneligibleAccount() {
        // Availability holds; the target Account exists but is ineligible (it has a netid), so no
        // reset token is constructed and no email is sent.
        stubResetSettings("true", "true");

        user1.setNetid("netid1");
        String unchanged = user1.getPassword();

        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);

        ApiResponse response = passwordController.initiateReset(resetRequest(RESET_EMAIL));

        assertResetRejected(response, ApiStatus.INVALID);
        assertEquals(unchanged, user1.getPassword(), "an ineligible account's stored password must be left unchanged");
    }

    @Test
    public void testInitiateResetRejectsWhenNoAccountForTheSuppliedEmail() {
        // Availability holds; no Account exists for the supplied email, which is ineligible, so no
        // reset token is constructed and no email is sent. There is no Account to write.
        stubResetSettings("true", "true");

        lenient().when(userRepo.findByEmail(anyString())).thenReturn(null);

        ApiResponse response = passwordController.initiateReset(resetRequest("absent@institution.edu"));

        assertResetRejected(response, ApiStatus.INVALID);

        // No Account existed, so nothing was written: only the read happened.
        verify(userRepo).findByEmail("absent@institution.edu");
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void testInitiateResetRejectsWhenPasswordLoginDisabled() {
        // auth.pass.enabled false short-circuits isResetAvailable before any lookup, so no reset
        // token is constructed and no email is sent.
        stubResetSettings("false", "true");

        user1.setNetid(null);
        String unchanged = user1.getPassword();

        lenient().when(userRepo.findByEmail(anyString())).thenReturn(user1);

        ApiResponse response = passwordController.initiateReset(resetRequest(RESET_EMAIL));

        assertResetRejected(response, ApiStatus.INVALID);
        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged");

        // The availability gate rejects before any Account lookup.
        verifyNoInteractions(userRepo);
    }

    @Test
    public void testInitiateResetRejectsWhenResetDisabled() {
        // auth.pass.reset.enabled false makes isResetAvailable false, so no reset token is
        // constructed and no email is sent.
        stubResetSettings("true", "false");

        user1.setNetid(null);
        String unchanged = user1.getPassword();

        lenient().when(userRepo.findByEmail(anyString())).thenReturn(user1);

        ApiResponse response = passwordController.initiateReset(resetRequest(RESET_EMAIL));

        assertResetRejected(response, ApiStatus.INVALID);
        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged");

        // The availability gate rejects before any Account lookup.
        verifyNoInteractions(userRepo);
    }

    /** A reset rejection pins the given status, constructs no token, and sends no email. */
    private void assertResetRejected(ApiResponse response, ApiStatus expected) {
        assertEquals(expected, response.getMeta().getStatus());

        verify(resetTokenService, never()).construct(any());
        verifyNoInteractions(emailSender);
        verifyNoInteractions(credentialNotificationService);
    }

    private Map<String, String> resetRequest(String email) {
        Map<String, String> data = new HashMap<>();

        data.put("email", email);

        return data;
    }

    /** Lenient because the reset conjunction short-circuits on the password-login setting. */
    private void stubResetSettings(String passwordLoginEnabled, String resetEnabled) {
        lenient().when(configurationRepo.getValueByNameAndType(ConfigurationName.APPLICATION_AUTH_PASS_ENABLED, APPLICATION_TYPE)).thenReturn(passwordLoginEnabled);
        lenient().when(configurationRepo.getValueByNameAndType(ConfigurationName.APPLICATION_AUTH_PASS_RESET_ENABLED, APPLICATION_TYPE)).thenReturn(resetEnabled);
    }

    // -----------------------------------------------------------------------------------------
    // POST /password/reset beyond the gates: missing template (ERROR naming it), mail failure
    // (ERROR), and a successful send (SUCCESS). The stored password is never written.
    // -----------------------------------------------------------------------------------------

    private static final String RESET_SUBJECT = "Reset your Vireo password";

    private static final String RESET_MESSAGE = "Follow {RESET_URL} to reset your password.";

    private static final String RESET_TOKEN = "resettoken.payload";

    @Test
    public void testInitiateResetReportsAnErrorWhenTheTemplateIsMissing() {
        // Availability and eligibility hold, but the reset-request template is not installed, so
        // resolveTemplate (backed by findByNameOverride) returns null. The handler reports ERROR
        // naming the absent template, sends no email, and leaves the stored password untouched.
        stubResetSettings("true", "true");

        user1.setNetid(null);
        String unchanged = user1.getPassword();

        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        when(credentialNotificationService.resolveTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE)).thenReturn(null);

        ApiResponse response = passwordController.initiateReset(resetRequest(RESET_EMAIL));

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
        assertTrue(response.getMeta().getMessage().contains(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE), "the error message must name the absent template");

        // No token was constructed and no email was sent.
        verify(resetTokenService, never()).construct(any());
        verifyNoInteractions(emailSender);

        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged when the template is missing");
    }

    @Test
    public void testInitiateResetReportsAnErrorWhenTheSendFails() throws Exception {
        // Availability, eligibility and template resolution all hold, but the send raises a
        // MessagingException. The handler converts it to ERROR and leaves the stored password
        // untouched: the reset path never writes the password.
        stubResetSettings("true", "true");

        user1.setNetid(null);
        String unchanged = user1.getPassword();

        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        when(credentialNotificationService.resolveTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE)).thenReturn(new EmailTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE, RESET_SUBJECT, RESET_MESSAGE));
        when(resetTokenService.construct(user1)).thenReturn(RESET_TOKEN);
        lenient().when(templateUtility.templateParameters(any(String.class), any(String[][].class))).thenReturn(RESET_MESSAGE);

        MessagingException mailFailure = new MessagingException("transport failure");
        doThrow(mailFailure).when(emailSender).sendEmail(anyString(), anyString(), anyString());

        Logger controllerLogger = (Logger) LoggerFactory.getLogger(PasswordController.class);
        Level originalLevel = controllerLogger.getLevel();
        boolean originalAdditive = controllerLogger.isAdditive();
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        ApiResponse response;

        appender.start();
        controllerLogger.setLevel(Level.ALL);
        controllerLogger.setAdditive(false);
        controllerLogger.addAppender(appender);

        try {
            response = passwordController.initiateReset(resetRequest(RESET_EMAIL));
        } finally {
            controllerLogger.detachAppender(appender);
            controllerLogger.setAdditive(originalAdditive);
            controllerLogger.setLevel(originalLevel);
            appender.stop();
        }

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged when the send fails");

        assertEquals(1, appender.list.size(), "the mail failure must produce one controller log event");
        ILoggingEvent event = appender.list.get(0);
        assertEquals(Level.WARN, event.getLevel(), "the mail failure must be visible at the production log level");
        assertEquals("Unable to send a password reset email.", event.getFormattedMessage());

        List<String> logLines = Arrays.asList(event.getFormattedMessage());
        assertNoSecretLeaked(response, logLines, "reset mail failure", RESET_EMAIL, user1.getEmail(), CURRENT_PASSWORD, unchanged, RESET_TOKEN);
    }

    @Test
    public void testInitiateResetSendsToTheRecipientOnSuccess() throws MessagingException {
        // Availability, eligibility and template resolution all hold and the send succeeds, so the
        // handler reports SUCCESS and the email targets the account's own email address.
        stubResetSettings("true", "true");

        user1.setNetid(null);

        when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        when(credentialNotificationService.resolveTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE)).thenReturn(new EmailTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE, RESET_SUBJECT, RESET_MESSAGE));
        when(resetTokenService.construct(user1)).thenReturn(RESET_TOKEN);
        when(templateUtility.templateParameters(any(String.class), any(String[][].class))).thenReturn(RESET_MESSAGE);

        ApiResponse response = passwordController.initiateReset(resetRequest(RESET_EMAIL));

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        // The response message is generic and names no recipient, keeping the recipient address out of the payload.
        assertEquals("A password reset link has been sent.", response.getMeta().getMessage());

        // The send itself targets the recipient's own email address and carries the resolved subject.
        verify(emailSender).sendEmail(eq(user1.getEmail()), eq(RESET_SUBJECT), anyString());
    }

    // -----------------------------------------------------------------------------------------
    // Initiation has no side effects: it constructs a token and sends mail but never writes the
    // stored password. Cases are enumerated email/hash shapes plus a fixed-seed Random.
    // -----------------------------------------------------------------------------------------

    static Stream<Arguments> provideResetNoSideEffectCases() {
        List<String> emails = new ArrayList<>(Arrays.asList(
            "email1",
            "student@institution.edu",
            "a@b.co",
            "UPPER.Case@Institution.EDU"));

        // Eligible stored-hash shapes only: the reset arm is driven for eligible Accounts.
        List<String> storedHashes = new ArrayList<>(Arrays.asList(
            FAKE_HASH,
            "HASHED_STORED_PASSWORD",
            "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012345",
            "plainhash"));

        List<Arguments> cases = new ArrayList<>();
        for (String email : emails) {
            for (String hash : storedHashes) {
                cases.add(Arguments.of(email, hash));
            }
        }

        java.util.Random random = new java.util.Random(20250101L);
        while (cases.size() < 100) {
            String email = "user" + random.nextInt(100000) + "@institution.edu";
            String hash = "$2a$10$" + random.nextLong();
            cases.add(Arguments.of(email, hash));
        }

        return cases.stream();
    }

    @ParameterizedTest(name = "[{index}] reset no side effects {0}")
    @MethodSource("provideResetNoSideEffectCases")
    public void initiateResetConstructsTokenAndSendsMailButWritesNoPassword(String email, String storedHash) throws MessagingException {
        setup();

        // Availability and eligibility hold, and the template resolves and the send succeeds, so
        // the full happy path runs: a token is constructed and mail is sent.
        stubResetSettings("true", "true");

        user1.setEmail(email);
        user1.setNetid(null);
        user1.setPassword(storedHash);

        String hashBefore = user1.getPassword();

        when(userRepo.findByEmail(email)).thenReturn(user1);
        when(credentialNotificationService.resolveTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE)).thenReturn(new EmailTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE, RESET_SUBJECT, RESET_MESSAGE));
        when(resetTokenService.construct(user1)).thenReturn(RESET_TOKEN);
        when(templateUtility.templateParameters(any(String.class), any(String[][].class))).thenReturn(RESET_MESSAGE);

        ApiResponse response = passwordController.initiateReset(resetRequest(email));

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), "eligible initiation succeeds for " + email);

        // Requirement 7.5: a token is still constructed and mail is still sent.
        verify(resetTokenService).construct(user1);
        verify(emailSender).sendEmail(eq(user1.getEmail()), eq(RESET_SUBJECT), anyString());

        // No side effect on the stored password: the initiation path never writes the Account.
        verify(userRepo, never()).update(any(User.class));
        verify(userRepo, never()).save(any(User.class));

        // The stored password hash is byte-identical before and after initiation.
        assertEquals(hashBefore, user1.getPassword(), "the stored password hash must be unchanged by initiation for " + email);
        assertTrue(storedHash.equals(user1.getPassword()), "the stored password hash must be byte-identical to the original for " + email);
    }

    // -----------------------------------------------------------------------------------------
    // Initiation is gated on auth.pass.enabled, auth.pass.reset.enabled and eligibility together;
    // every failing combination constructs no token, sends no email and writes nothing.
    // -----------------------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideResetEnforcementCases")
    public void resetEnforcesItsGatesIndependentlyOfTheClient(String passwordLoginRaw, String resetRaw, String[] eligibilityShape, String label) throws MessagingException {
        setup();

        boolean available = resolvesTrue(passwordLoginRaw) && resolvesTrue(resetRaw);
        boolean eligible = eligibilityShape[1] == null && eligibilityShape[0] != null && !eligibilityShape[0].trim().isEmpty();
        boolean succeeds = available && eligible;

        stubResetSettings(passwordLoginRaw, resetRaw);

        user1.setPassword(eligibilityShape[0]);
        user1.setNetid(eligibilityShape[1]);

        String unchanged = user1.getPassword();

        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        lenient().when(credentialNotificationService.resolveTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE))
            .thenReturn(new EmailTemplate(CredentialNotificationService.PASSWORD_RESET_REQUEST_TEMPLATE, RESET_SUBJECT, RESET_MESSAGE));
        lenient().when(resetTokenService.construct(user1)).thenReturn(RESET_TOKEN);
        lenient().when(templateUtility.templateParameters(any(String.class), any(String[][].class))).thenReturn(RESET_MESSAGE);

        ApiResponse response = passwordController.initiateReset(resetRequest(RESET_EMAIL));

        if (succeeds) {
            assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), label);

            verify(resetTokenService).construct(user1);
            verify(emailSender).sendEmail(eq(user1.getEmail()), eq(RESET_SUBJECT), anyString());
        } else {
            assertEquals(ApiStatus.INVALID, response.getMeta().getStatus(), label);

            verify(resetTokenService, never()).construct(any());
            verifyNoInteractions(emailSender);
        }

        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged: " + label);
    }

    /**
     * Cases as (passwordLoginRaw, resetRaw, eligibilityShape, label): availability varied alone
     * against an eligible shape, eligibility varied alone against an available pair, and every
     * combination where both an availability pair and an eligibility shape fail together.
     */
    private static Stream<Arguments> provideResetEnforcementCases() {
        List<Arguments> cases = new ArrayList<>();

        for (String[] availability : AVAILABILITY_PAIRS) {
            cases.add(Arguments.of(availability[0], availability[1], new String[] { HASHED_STORED_PASSWORD, null }, "availability " + availability[0] + "/" + availability[1]));
        }

        for (String[] eligibility : ELIGIBILITY_SHAPES) {
            cases.add(Arguments.of("true", "true", eligibility, "eligibility " + eligibility[0] + "/" + eligibility[1]));
        }

        for (String[] availability : AVAILABILITY_PAIRS) {
            if (resolvesTrue(availability[0]) && resolvesTrue(availability[1])) {
                continue;
            }

            for (String[] eligibility : ELIGIBILITY_SHAPES) {
                if (eligibility[1] == null && HASHED_STORED_PASSWORD.equals(eligibility[0])) {
                    continue;
                }

                cases.add(Arguments.of(availability[0], availability[1], eligibility, "both gates failing " + availability[0] + "/" + availability[1] + " " + eligibility[0] + "/" + eligibility[1]));
            }
        }

        return cases.stream();
    }

    // -----------------------------------------------------------------------------------------
    // POST /password/reset/redeem: enforcement. Each rejection cause - either setting off, each
    // token failure class, an ineligible Account - pins INVALID, an unchanged password, and no
    // notification.
    // -----------------------------------------------------------------------------------------

    private static final String REDEEM_NEW_PASSWORD = "brandnewpassword";

    @Test
    public void testRedeemResetRejectsWhenPasswordLoginDisabled() {
        // auth.pass.enabled false short-circuits isResetAvailable before any token verification.
        stubResetSettings("false", "true");

        String unchanged = user1.getPassword();

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, REDEEM_NEW_PASSWORD, REDEEM_NEW_PASSWORD));

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged");

        // The availability gate rejects before the token is even read.
        verifyNoInteractions(resetTokenService);
        verifyNoInteractions(userRepo);
        verifyNoInteractions(credentialNotificationService);
    }

    @Test
    public void testRedeemResetRejectsWhenResetDisabled() {
        // auth.pass.reset.enabled false makes isResetAvailable false.
        stubResetSettings("true", "false");

        String unchanged = user1.getPassword();

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, REDEEM_NEW_PASSWORD, REDEEM_NEW_PASSWORD));

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged");

        verifyNoInteractions(resetTokenService);
        verifyNoInteractions(userRepo);
        verifyNoInteractions(credentialNotificationService);
    }

    @Test
    public void testRedeemResetRejectsAnExpiredToken() {
        // Availability holds, but the token aged past its expiry window, so the response carries
        // the expiry-specific instruction rather than the generic message.
        stubResetSettings("true", "true");

        String unchanged = user1.getPassword();

        when(resetTokenService.verify(TOKEN)).thenReturn(ResetTokenVerification.expired());

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, REDEEM_NEW_PASSWORD, REDEEM_NEW_PASSWORD));

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
        assertTrue(response.getMeta().getMessage().toLowerCase().contains("expired"), "an expired token must report the expiry-specific message");
        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged");

        // No Account is even looked up once the token fails verification.
        verifyNoInteractions(userRepo);
        verifyNoInteractions(credentialNotificationService);
    }

    @Test
    public void testRedeemResetRejectsAnInvalidToken() {
        // Availability holds, but the token is malformed, tampered, unknown, or stale from an
        // unrelated password change: every one of those causes reports the same generic message.
        stubResetSettings("true", "true");

        String unchanged = user1.getPassword();

        when(resetTokenService.verify(TOKEN)).thenReturn(ResetTokenVerification.invalid());

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, REDEEM_NEW_PASSWORD, REDEEM_NEW_PASSWORD));

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
        assertEquals("This password reset link is no longer valid.", response.getMeta().getMessage());
        assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged");

        verifyNoInteractions(userRepo);
        verifyNoInteractions(credentialNotificationService);
    }

    @Test
    public void testRedeemResetRejectsAnIneligibleAccount() {
        // The token itself verifies, but the Account it names carries a netid and so is ineligible.
        stubResetSettings("true", "true");

        user1.setNetid("netid1");
        String unchanged = user1.getPassword();

        when(resetTokenService.verify(TOKEN)).thenReturn(ResetTokenVerification.valid(RESET_EMAIL, Instant.now()));
        when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, REDEEM_NEW_PASSWORD, REDEEM_NEW_PASSWORD));

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
        assertEquals(unchanged, user1.getPassword(), "an ineligible account's stored password must be left unchanged");

        verify(userRepo, never()).update(any(User.class));
        verifyNoInteractions(credentialNotificationService);
    }

    private Map<String, String> redeemRequest(String token, String newPassword, String confirm) {
        Map<String, String> data = new HashMap<>();

        data.put("token", token);
        data.put("newPassword", newPassword);
        data.put("confirm", confirm);

        return data;
    }

    // -----------------------------------------------------------------------------------------
    // Redemption replaces the stored password only when availability, token verification,
    // eligibility and candidate validity all hold. Each dimension is varied against an
    // otherwise-succeeding baseline.
    // -----------------------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] {6}")
    @MethodSource("provideRedemptionConjunctionCases")
    public void redeemResetSucceedsExactlyUnderItsConjunction(String passwordLoginRaw, String resetRaw, ResetTokenVerification.Status tokenStatus, String[] eligibilityShape, String newPassword, String confirm, String label) {
        setup();

        boolean available = resolvesTrue(passwordLoginRaw) && resolvesTrue(resetRaw);
        boolean tokenValid = tokenStatus == ResetTokenVerification.Status.VALID;
        boolean eligible = eligibilityShape[1] == null && eligibilityShape[0] != null && !eligibilityShape[0].trim().isEmpty();
        boolean candidateAccepted = PasswordValidator.validate(newPassword, confirm) == PasswordValidator.Result.ACCEPTED;
        boolean succeeds = available && tokenValid && eligible && candidateAccepted;

        stubResetSettings(passwordLoginRaw, resetRaw);

        if (succeeds) {
            stubRealCrypto();
        } else {
            stubFakeCrypto();
        }

        user1.setPassword(eligibilityShape[0]);
        user1.setNetid(eligibilityShape[1]);

        String unchanged = user1.getPassword();

        ResetTokenVerification verification;
        if (tokenValid) {
            verification = ResetTokenVerification.valid(RESET_EMAIL, Instant.now());
        } else if (tokenStatus == ResetTokenVerification.Status.EXPIRED) {
            verification = ResetTokenVerification.expired();
        } else {
            verification = ResetTokenVerification.invalid();
        }

        lenient().when(resetTokenService.verify(TOKEN)).thenReturn(verification);
        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        lenient().when(userRepo.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, newPassword, confirm));

        if (succeeds) {
            assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), label);

            assertFalse(unchanged.equals(user1.getPassword()), "the stored password must have been replaced: " + label);
            assertTrue(passwordEncoder.matches(newPassword, user1.getPassword()), "the replaced password must verify against the submitted new password: " + label);

            verify(userRepo).update(user1);
            verify(credentialNotificationService).notifyPasswordChanged(user1);
        } else {
            assertEquals(ApiStatus.INVALID, response.getMeta().getStatus(), label);
            assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged: " + label);

            verify(userRepo, never()).update(any(User.class));
            verifyNoInteractions(credentialNotificationService);
        }
    }

    /**
     * Cases as (passwordLoginRaw, resetRaw, tokenStatus, eligibilityShape, newPassword, confirm,
     * label). Each block varies one dimension while the others stay on a succeeding shape.
     */
    private static Stream<Arguments> provideRedemptionConjunctionCases() {
        List<Arguments> cases = new ArrayList<>();

        for (String[] availability : AVAILABILITY_PAIRS) {
            cases.add(Arguments.of(availability[0], availability[1], ResetTokenVerification.Status.VALID, new String[] { HASHED_STORED_PASSWORD, null }, NEW_PASSWORD, NEW_PASSWORD,
                "availability " + availability[0] + "/" + availability[1]));
        }

        for (ResetTokenVerification.Status status : ResetTokenVerification.Status.values()) {
            cases.add(Arguments.of("true", "true", status, new String[] { HASHED_STORED_PASSWORD, null }, NEW_PASSWORD, NEW_PASSWORD,
                "token status " + status));
        }

        for (String[] eligibility : ELIGIBILITY_SHAPES) {
            cases.add(Arguments.of("true", "true", ResetTokenVerification.Status.VALID, eligibility, NEW_PASSWORD, NEW_PASSWORD,
                "eligibility " + eligibility[0] + "/" + eligibility[1]));
        }

        for (String[] candidate : CANDIDATE_PAIRS) {
            cases.add(Arguments.of("true", "true", ResetTokenVerification.Status.VALID, new String[] { HASHED_STORED_PASSWORD, null }, candidate[0], candidate[1],
                "candidate '" + candidate[0] + "'/'" + candidate[1] + "'"));
        }

        return cases.stream();
    }

    // -----------------------------------------------------------------------------------------
    // A rejected candidate leaves the token usable. The real ResetTokenService is wired in here
    // so the token's continued validity is genuinely exercised; after the rejection the same
    // token redeems successfully.
    // -----------------------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] rejected candidate ''{0}''/''{1}'' leaves the token usable")
    @MethodSource("provideRejectedCandidatesForTokenUsability")
    public void aRejectedCandidateLeavesTheTokenUsable(String rejectedNewPassword, String rejectedConfirm) {
        setup();

        stubResetSettings("true", "true");
        stubRealCrypto();

        ResetTokenService realResetTokenService = new ResetTokenService();
        ReflectionTestUtils.setField(realResetTokenService, "signingSecret", SECRET_VALUE);
        ReflectionTestUtils.setField(realResetTokenService, "userRepo", userRepo);
        ReflectionTestUtils.setField(realResetTokenService, "availabilityService", availabilityService);
        ReflectionTestUtils.setField(passwordController, "resetTokenService", realResetTokenService);

        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        lenient().when(userRepo.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = realResetTokenService.construct(user1);

        // The rejected submission is turned away and leaves nothing for the token's binding to react to.
        ApiResponse rejectedResponse = passwordController.redeemReset(redeemRequest(token, rejectedNewPassword, rejectedConfirm));

        assertEquals(ApiStatus.INVALID, rejectedResponse.getMeta().getStatus());
        assertEquals(FAKE_HASH, user1.getPassword(), "a rejected candidate must leave the stored password unchanged");

        verify(userRepo, never()).update(any(User.class));

        // The same token still redeems successfully afterward: the earlier rejection left it intact.
        ApiResponse successResponse = passwordController.redeemReset(redeemRequest(token, NEW_PASSWORD, NEW_PASSWORD));

        assertEquals(ApiStatus.SUCCESS, successResponse.getMeta().getStatus(), "the token must still be usable after an earlier rejected candidate");
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, user1.getPassword()), "the stored password must now verify against the submitted new password");

        verify(userRepo).update(user1);
        verify(credentialNotificationService).notifyPasswordChanged(user1);
    }

    private static Stream<Arguments> provideRejectedCandidatesForTokenUsability() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of("   ", "   "),
            Arguments.of("short", "short"),
            Arguments.of(NEW_PASSWORD, "newpasswordx"));
    }

    // -----------------------------------------------------------------------------------------
    // A successful redemption carries no payload, so no token or credential rides along and the
    // caller is left to sign in (unlike AuthController.login, whose payload is a JWT).
    // -----------------------------------------------------------------------------------------

    @Test
    public void testRedeemResetSuccessCarriesNoTokenAndNoCredential() throws Exception {
        stubResetSettings("true", "true");
        stubRealCrypto();

        user1.setNetid(null);

        when(resetTokenService.verify(TOKEN)).thenReturn(ResetTokenVerification.valid(RESET_EMAIL, Instant.now()));
        when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        when(userRepo.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, NEW_PASSWORD, NEW_PASSWORD));

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        // No payload rides along at all, so there is nothing a client could read as a token or a
        // credential.
        assertTrue(response.getPayload() == null || response.getPayload().isEmpty(), "a successful redemption must carry no payload, so neither a token nor a credential can ride along");

        assertNoSecretLeaked(response, new ArrayList<>(), "successful redemption", TOKEN, NEW_PASSWORD);
    }

    // -----------------------------------------------------------------------------------------
    // Redemption is gated on auth.pass.enabled, auth.pass.reset.enabled and eligibility together;
    // the token verifies throughout, so only those gates are under test here.
    // -----------------------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideRedeemEnforcementCases")
    public void redeemEnforcesItsGatesIndependentlyOfTheClient(String passwordLoginRaw, String resetRaw, String[] eligibilityShape, String label) {
        setup();

        boolean available = resolvesTrue(passwordLoginRaw) && resolvesTrue(resetRaw);
        boolean eligible = eligibilityShape[1] == null && eligibilityShape[0] != null && !eligibilityShape[0].trim().isEmpty();
        boolean succeeds = available && eligible;

        stubResetSettings(passwordLoginRaw, resetRaw);
        stubRealCrypto();

        user1.setPassword(eligibilityShape[0]);
        user1.setNetid(eligibilityShape[1]);

        String unchanged = user1.getPassword();

        lenient().when(resetTokenService.verify(TOKEN)).thenReturn(ResetTokenVerification.valid(RESET_EMAIL, Instant.now()));
        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        lenient().when(userRepo.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse response = passwordController.redeemReset(redeemRequest(TOKEN, NEW_PASSWORD, NEW_PASSWORD));

        if (succeeds) {
            assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), label);

            verify(userRepo).update(user1);
            verify(credentialNotificationService).notifyPasswordChanged(user1);
        } else {
            assertEquals(ApiStatus.INVALID, response.getMeta().getStatus(), label);
            assertEquals(unchanged, user1.getPassword(), "the stored password must be left unchanged: " + label);

            verify(userRepo, never()).update(any(User.class));
            verifyNoInteractions(credentialNotificationService);
        }
    }

    /**
     * Cases as (passwordLoginRaw, resetRaw, eligibilityShape, label): availability varied alone
     * against an eligible shape, eligibility varied alone against an available pair, and every
     * combination where both an availability pair and an eligibility shape fail together.
     */
    private static Stream<Arguments> provideRedeemEnforcementCases() {
        List<Arguments> cases = new ArrayList<>();

        for (String[] availability : AVAILABILITY_PAIRS) {
            cases.add(Arguments.of(availability[0], availability[1], new String[] { HASHED_STORED_PASSWORD, null }, "availability " + availability[0] + "/" + availability[1]));
        }

        for (String[] eligibility : ELIGIBILITY_SHAPES) {
            cases.add(Arguments.of("true", "true", eligibility, "eligibility " + eligibility[0] + "/" + eligibility[1]));
        }

        for (String[] availability : AVAILABILITY_PAIRS) {
            if (resolvesTrue(availability[0]) && resolvesTrue(availability[1])) {
                continue;
            }

            for (String[] eligibility : ELIGIBILITY_SHAPES) {
                if (eligibility[1] == null && HASHED_STORED_PASSWORD.equals(eligibility[0])) {
                    continue;
                }

                cases.add(Arguments.of(availability[0], availability[1], eligibility, "both gates failing " + availability[0] + "/" + availability[1] + " " + eligibility[0] + "/" + eligibility[1]));
            }
        }

        return cases.stream();
    }

    // -----------------------------------------------------------------------------------------
    // Failure message partition: every non-expiry failure (malformed, tampered, unknown account,
    // stale after a password change, backward clock) reports the identical generic message, and
    // a genuine expiry reports a different one. Overall status is INVALID for all of them, so only
    // the message distinguishes. The real ResetTokenService produces each cause.
    // -----------------------------------------------------------------------------------------

    private static final String GENERIC_INVALID_MESSAGE = "This password reset link is no longer valid.";

    @Test
    public void testRedeemResetReportsTheSameGenericMessageForEveryNonExpiryCause() {
        stubResetSettings("true", "true");

        ResetTokenService realResetTokenService = new ResetTokenService();
        ReflectionTestUtils.setField(realResetTokenService, "signingSecret", SECRET_VALUE);
        ReflectionTestUtils.setField(realResetTokenService, "userRepo", userRepo);
        ReflectionTestUtils.setField(realResetTokenService, "availabilityService", availabilityService);

        // A fixed base clock keeps every offset below deterministic. A system clock let a token
        // built at an hour offset land on either side of the whole-hour expiry boundary depending
        // on the wall-clock moment the test ran, which made this test intermittently fail.
        Clock baseClock = Clock.fixed(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);
        ReflectionTestUtils.setField(realResetTokenService, "clock", baseClock);
        ReflectionTestUtils.setField(passwordController, "resetTokenService", realResetTokenService);

        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);

        List<String> genericMessages = new ArrayList<>();

        // Cause 1: a malformed token, carrying no "." separator at all.
        genericMessages.add(redeemAndGetMessage("not-a-valid-token"));

        // Cause 2: a tampered signature.
        String validToken = realResetTokenService.construct(user1);
        genericMessages.add(redeemAndGetMessage(tamper(validToken)));

        // Cause 3: an unknown account - no User resolves for the token's email.
        User ghost = new User("nobody@institution.edu", "firstname3", "lastname3", Role.ROLE_STUDENT);
        ghost.setPassword(FAKE_HASH);
        lenient().when(userRepo.findByEmail("nobody@institution.edu")).thenReturn(null);
        String ghostToken = realResetTokenService.construct(ghost);
        genericMessages.add(redeemAndGetMessage(ghostToken));

        // Cause 4: a token invalidated by an unrelated password change; BCrypt re-salts on every
        // encode, so even an equal-value change would invalidate it.
        String staleToken = realResetTokenService.construct(user1);
        String originalPassword = user1.getPassword();
        user1.setPassword(new BCryptPasswordEncoder().encode(CURRENT_PASSWORD));
        genericMessages.add(redeemAndGetMessage(staleToken));
        user1.setPassword(originalPassword);

        // Cause 5: a backward clock step, so the elapsed time since issuance is negative.
        ReflectionTestUtils.setField(realResetTokenService, "clock", Clock.offset(baseClock, Duration.ofHours(1)));
        String futureToken = realResetTokenService.construct(user1);
        ReflectionTestUtils.setField(realResetTokenService, "clock", baseClock);
        genericMessages.add(redeemAndGetMessage(futureToken));

        for (String message : genericMessages) {
            assertEquals(GENERIC_INVALID_MESSAGE, message, "every non-expiry cause must report the identical generic message");
        }

        // A genuine expiry reports a different, expiry-specific message.
        ReflectionTestUtils.setField(realResetTokenService, "clock", Clock.offset(baseClock, Duration.ofHours(-25)));
        String expiringToken = realResetTokenService.construct(user1);
        ReflectionTestUtils.setField(realResetTokenService, "clock", baseClock);

        String expiredMessage = redeemAndGetMessage(expiringToken);

        assertFalse(GENERIC_INVALID_MESSAGE.equals(expiredMessage), "an expired token must not report the generic message");
        assertTrue(expiredMessage.toLowerCase().contains("expired"), "an expired token must report the expiry-specific message");
    }

    /** Redeems with a fixed, valid-shaped candidate so only the token's own verification governs
     * the outcome, and returns the message so the caller can compare it against other causes. */
    private String redeemAndGetMessage(String token) {
        ApiResponse response = passwordController.redeemReset(redeemRequest(token, NEW_PASSWORD, NEW_PASSWORD));

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());

        return response.getMeta().getMessage();
    }

    /** Flips the token's final character to a different, still-valid base64url character. */
    private static String tamper(String token) {
        char last = token.charAt(token.length() - 1);
        char replacement = last == 'A' ? 'B' : 'A';

        return token.substring(0, token.length() - 1) + replacement;
    }

    // -----------------------------------------------------------------------------------------
    // Leak containment: no response or log line from redemption may contain the email, the token,
    // or the stored hash (before or after), whatever the outcome. Reuses the conjunction cases.
    // -----------------------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] leak containment {6}")
    @MethodSource("provideRedemptionConjunctionCases")
    public void redeemResetLeaksNoSecretUnderAnyOutcome(String passwordLoginRaw, String resetRaw, ResetTokenVerification.Status tokenStatus, String[] eligibilityShape, String newPassword, String confirm, String label) throws Exception {
        setup();

        stubResetSettings(passwordLoginRaw, resetRaw);
        stubRealCrypto();

        user1.setPassword(eligibilityShape[0]);
        user1.setNetid(eligibilityShape[1]);

        String storedBefore = user1.getPassword();

        ResetTokenVerification verification;
        if (tokenStatus == ResetTokenVerification.Status.VALID) {
            verification = ResetTokenVerification.valid(RESET_EMAIL, Instant.now());
        } else if (tokenStatus == ResetTokenVerification.Status.EXPIRED) {
            verification = ResetTokenVerification.expired();
        } else {
            verification = ResetTokenVerification.invalid();
        }

        lenient().when(resetTokenService.verify(TOKEN)).thenReturn(verification);
        lenient().when(userRepo.findByEmail(RESET_EMAIL)).thenReturn(user1);
        lenient().when(userRepo.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<String> log = new ArrayList<>();
        ApiResponse response = capturingLog(log, () -> passwordController.redeemReset(redeemRequest(TOKEN, newPassword, confirm)));

        assertNoSecretLeaked(response, log, label, TOKEN, RESET_EMAIL, storedBefore, user1.getPassword(), newPassword, confirm);
    }

}
