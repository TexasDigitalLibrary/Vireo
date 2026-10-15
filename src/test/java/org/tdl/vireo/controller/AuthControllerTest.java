package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.auth.controller.AuthController;
import org.tdl.vireo.auth.service.VireoUserCredentialsService;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.VireoEmailSender;
import org.tdl.vireo.utility.PasswordValidator;

@ActiveProfiles(value = { "test", "isolated-test" })
public class AuthControllerTest extends AbstractControllerTest {

    @Mock
    private VireoEmailSender emailSender;

    @Mock
    private UserRepo userRepo;

    @Mock
    private EmailTemplateRepo emailTemplateRepo;

    @Mock
    private VireoUserCredentialsService vireoUserCredentialsService;

    @InjectMocks
    private AuthController authController;

    private Map<String, String> parameters;
    private Map<String, String> data;
    private User user;
    private EmailTemplate emailTemplate;

    @BeforeEach
    public void setup() throws MessagingException {
        parameters = new HashMap<String, String>();
        data = new HashMap<String, String>();

        user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setPassword("password");

        emailTemplate = new EmailTemplate();
        emailTemplate.setId(1L);
        emailTemplate.setSubject("subject");
        emailTemplate.setMessage("message");
    }

    @Test
    public void testRegistrationWithoutFirstName() {
        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testRegistrationWithoutLastName() {
        data.put("firstName", "firstName");

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testRegistrationWithoutPassword() {
        data.put("firstName", "firstName");
        data.put("lastName", "lastName");

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testRegistrationWithoutConfirm() {
        data.put("firstName", "firstName");
        data.put("lastName", "lastName");
        data.put("userPassword", "userPassword");

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testRegistrationWithShortPassword() {
        data.put("firstName", "firstName");
        data.put("lastName", "lastName");
        data.put("userPassword", "12345");
        data.put("confirm", data.get("userPassword"));

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideValidateGenericTokenExceptions")
    public void testRegistrationValidateGenericTokenThrowsException(Class<? extends Throwable> exception) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        setupDataMap();

        when(cryptoService.validateGenericToken(anyString(), anyString())).thenThrow(exception);

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testRegistrationWhenTokenIsExpired() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // Warning the tokenDaysOld calculation is unsafe and can fail when the token time is less than the current time due to the subtraction.
        String[] content = { "" + Instant.now().plusSeconds(86400 * 3).toEpochMilli(), "e@mail.com" };

        setupDataMap();

        when(cryptoService.validateGenericToken(anyString(), anyString())).thenReturn(content);

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testRegistration() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // Warning the tokenDaysOld calculation is unsafe and can fail when the token time is less than the current time due to the subtraction.
        String[] content = { "" + Instant.now().toEpochMilli(), "e@mail.com" };

        setupDataMap();

        when(cryptoService.validateGenericToken(anyString(), anyString())).thenReturn(content);
        when(cryptoService.encodePassword(anyString())).thenReturn("encoded");
        when(vireoUserCredentialsService.createUserFromRegistration(anyString(), anyString(), anyString(), anyString())).thenReturn(user);

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        User got = (User) response.getPayload().get("User");

        assertEquals(got, user, "Did not get the correct user returned.");
    }

    @Test
    public void testRegistrationWithEmailAlreadyExists() {
        setupDataMap();
        parameters.put("email", TEST_EMAIL);

        when(userRepo.findByEmail(anyString())).thenReturn(user);

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideValidateGenericTokenExceptions")
    public void testRegistrationWithEmailGenerateTokenThrowsException(Class<? extends Throwable> exception) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        setupDataMap();
        parameters.put("email", TEST_EMAIL);

        ReflectionTestUtils.setField(authController, "url", "http://localhost");

        when(userRepo.findByEmail(anyString())).thenReturn(null);
        when(emailTemplateRepo.findByNameOverride(anyString())).thenReturn(emailTemplate);
        when(cryptoService.generateGenericToken(anyString(), anyString())).thenThrow(exception);

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testRegistrationWithEmailSendEmailThrowsException() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, MessagingException {
        setupDataMap();
        parameters.put("email", TEST_EMAIL);

        ReflectionTestUtils.setField(authController, "url", "http://localhost");

        when(userRepo.findByEmail(anyString())).thenReturn(null);
        when(emailTemplateRepo.findByNameOverride(anyString())).thenReturn(emailTemplate);
        when(cryptoService.generateGenericToken(anyString(), anyString())).thenReturn("token");
        when(templateUtility.templateParameters(anyString(), any(String[][].class))).thenReturn("content");
        doThrow(MessagingException.class).when(emailSender).sendEmail(anyString(), anyString(), anyString());

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegistrationWithEmail() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, MessagingException {
        setupDataMap();
        parameters.put("email", TEST_EMAIL);

        ReflectionTestUtils.setField(authController, "url", "http://localhost");

        when(userRepo.findByEmail(anyString())).thenReturn(null);
        when(emailTemplateRepo.findByNameOverride(anyString())).thenReturn(emailTemplate);
        when(cryptoService.generateGenericToken(anyString(), anyString())).thenReturn("token");
        when(templateUtility.templateParameters(anyString(), any(String[][].class))).thenReturn("content");
        doNothing().when(emailSender).sendEmail(anyString(), anyString(), anyString());

        ApiResponse response = authController.registration(data, parameters);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Map<String, String> got = (HashMap<String, String>) response.getPayload().get("HashMap");

        assertEquals(got, parameters, "Did not get the parameters returned.");
    }

    @Test
    public void testLoginWhenUserIsNotFound() {
        setupDataMap();

        when(userRepo.findByEmail(anyString())).thenReturn(null);

        ApiResponse response = authController.login(data);

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
    }

    @Test
    public void testLoginWhenPasswordIsInvalid() {
        setupDataMap();

        when(userRepo.findByEmail(anyString())).thenReturn(user);
        when(cryptoService.validatePassword(anyString(), any())).thenReturn(false);

        ApiResponse response = authController.login(data);

        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideValidateGenericTokenExceptions")
    public void testLoginWhenTokenServiceThrowsException(Class<? extends Throwable> exception) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        setupDataMap();

        when(userRepo.findByEmail(anyString())).thenReturn(user);
        when(cryptoService.validatePassword(anyString(), any())).thenReturn(true);
        when(tokenService.createToken(anyString(), anyMap())).thenThrow(exception);

        ApiResponse response = authController.login(data);

        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
    }

    @Test
    public void testLogin() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String token = "token";

        setupDataMap();

        when(userRepo.findByEmail(anyString())).thenReturn(user);
        when(cryptoService.validatePassword(anyString(), any())).thenReturn(true);
        when(tokenService.createToken(anyString(), anyMap())).thenReturn(token);

        ApiResponse response = authController.login(data);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        String got = (String) response.getPayload().get("String");

        assertEquals(got, token, "Did not get the token returned.");
    }

    protected static Stream<Arguments> provideValidateGenericTokenExceptions() {
        return Stream.of(
            Arguments.of(InvalidKeyException.class),
            Arguments.of(NoSuchPaddingException.class),
            Arguments.of(NoSuchAlgorithmException.class),
            Arguments.of(IllegalBlockSizeException.class),
            Arguments.of(BadPaddingException.class)
        );
    }

    private void setupDataMap() {
        data.put("email", TEST_EMAIL);
        data.put("token", "token");
        data.put("firstName", "firstName");
        data.put("lastName", "lastName");
        data.put("userPassword", "userPassword");
        data.put("confirm", data.get("userPassword"));
    }

    /* ------------------------------------------------------------------------------------------
     * Property 11: Registration retrofit preserves outcomes.
     *
     * Validates: Requirements 3.6, 3.7, 3.9
     *
     * Everything below is additive. No method above this point is edited, reordered, or
     * reformatted: the unchanged methods are the example-based regression evidence for the
     * retrofit, and this property generalises the same claim over generated input.
     * ------------------------------------------------------------------------------------------ */

    private static final long RETROFIT_GENERATOR_SEED = 20250101L;

    private static final int RETROFIT_RANDOM_CASE_COUNT = 48;

    private static final String REGISTRATION_SUCCESS_MESSAGE = "Registration was successfull. Please login.";

    /**
     * The candidate and confirmation values crossed with themselves to form the enumerated cases.
     *
     * The list is chosen so the cross product contains every shape that can tell the three
     * rejection causes apart, in the order {@code AuthController.registration} evaluated them
     * before the retrofit:
     *
     * - Absent and whitespace-only values, which must be reported as a missing password even when
     *   the confirmation differs — that is what pins missing ahead of mismatch.
     * - The trimmed length 5 and trimmed length 6 boundary, explicitly, on both sides, across
     *   letters and digits: {@code "abcde"} against {@code "abcdef"} and {@code "12345"} against
     *   {@code "123456"}.
     * - {@code "12345"} and {@code "54321"}, the ordering case: the two differ and are both too
     *   short, so a rule that checked length before the confirmation would report the wrong cause
     *   while still rejecting. Status alone cannot catch that, which is why the message is asserted
     *   too.
     * - {@code "abcde "}, whose raw length is 6 and trimmed length is 5, so the length check has to
     *   be applied to the trimmed value; and {@code " abcdef "}, which is accepted on its trimmed
     *   length and must reach the encoder untrimmed.
     */
    private static final List<String> RETROFIT_PASSWORD_VALUES = Arrays.asList(
        // Absent and whitespace-only.
        null,
        "",
        "   ",
        "\t",

        // The rejected side of the boundary, and the ordering case.
        "12345",
        "54321",
        "abcde",

        // Trimmed length 5 carried inside a raw length 6 value.
        "abcde ",

        // The accepted side of the boundary, across character classes.
        "123456",
        "abcdef",
        "ABCDEF",
        " abcdef ",

        // Comfortably above the boundary.
        "correct horse battery staple",
        "\u00e9bcdef"
    );

    /**
     * Each case is one candidate password and confirmation pair handed to
     * {@code AuthController.registration}.
     *
     * Every enumerated value is crossed with every enumerated value, which yields both the
     * accepting and the rejecting side of the claim, then a fixed-seed {@link Random} adds further
     * pairs drawn from the same pool — a quarter of them equal pairs, so the accepting path keeps
     * being exercised, and a quarter of them single-character perturbations, which is the near-miss
     * shape that separates a raw-string comparison from a looser one.
     *
     * The enumerated cross product alone is 225 cases, comfortably past the hundred-case floor, and
     * no property-based testing library is used.
     *
     * @return The generated cases.
     */
    protected static Stream<Arguments> provideRetrofitPasswordPairs() {
        List<Arguments> cases = new ArrayList<Arguments>();

        for (String password : RETROFIT_PASSWORD_VALUES) {
            for (String confirm : RETROFIT_PASSWORD_VALUES) {
                cases.add(Arguments.of(password, confirm));
            }
        }

        Random random = new Random(RETROFIT_GENERATOR_SEED);

        for (int i = 0; i < RETROFIT_RANDOM_CASE_COUNT; i++) {
            String password = RETROFIT_PASSWORD_VALUES.get(random.nextInt(RETROFIT_PASSWORD_VALUES.size()));

            String confirm;

            switch (random.nextInt(4)) {
                case 0:
                    confirm = password;
                    break;
                case 1:
                    confirm = perturbRetrofitValue(password, random);
                    break;
                case 2:
                    confirm = RETROFIT_PASSWORD_VALUES.get(random.nextInt(RETROFIT_PASSWORD_VALUES.size()));
                    break;
                default:
                    confirm = null;
                    break;
            }

            cases.add(Arguments.of(password, confirm));
        }

        return cases.stream();
    }

    /**
     * Alter one character of the supplied value, so the result differs from the input in exactly
     * one position. A null or empty input becomes a short non-empty value, which still differs.
     *
     * @param value  The value to perturb.
     * @param random The fixed-seed source.
     *
     * @return The perturbed value, which never equals the input.
     */
    private static String perturbRetrofitValue(String value, Random random) {
        if (value == null || value.length() == 0) {
            return "x";
        }

        char[] characters = value.toCharArray();

        int position = random.nextInt(characters.length);

        characters[position] = characters[position] == 'x' ? 'y' : 'x';

        return new String(characters);
    }

    /**
     * The password rule {@code AuthController.registration} carried inline before the retrofit,
     * transcribed from the pre-retrofit source: missing first, then mismatch, then too short, with
     * the message text of each branch verbatim.
     *
     * This is deliberately a transcription rather than a call to {@link PasswordValidator}: the
     * claim is that the retrofitted handler agrees with the rule it replaced, and an oracle that
     * called the replacement would assert nothing.
     *
     * @param password The candidate password, which may be null.
     * @param confirm  The confirmation value, which may be null.
     *
     * @return The pre-retrofit rejection message, or null where the pre-retrofit rule accepted.
     */
    private static String preRetrofitRejectionMessage(String password, String confirm) {
        if (password == null || password.trim().length() == 0) {
            return "Registration requires a password!";
        }

        if (!password.equals(confirm)) {
            return "The passwords do not match!";
        }

        if (password.trim().length() < 6) {
            return "Password must be greater than 6 characters!";
        }

        return null;
    }

    /**
     * Property 11: Registration retrofit preserves outcomes.
     *
     * Validates: Requirements 3.6, 3.7, 3.9
     *
     * For any pair of candidate password and confirmation values, {@code AuthController.registration}
     * after the retrofit returns the same accept-or-reject outcome, with the same message, that the
     * pre-retrofit inline rule produced.
     *
     * Three things are asserted on every case:
     *
     * 1. Requirement 3.6: the decision the handler acts on is the shared validator's. The
     *    validator's verdict and message are checked against the transcribed pre-retrofit rule, so
     *    the two rules cannot drift apart silently.
     * 2. Requirement 3.9: the handler's observable outcome — the {@code ApiStatus} and the response
     *    message — matches what the pre-retrofit rule produced. Both halves matter: the three
     *    rejection causes share the {@code ERROR} status, so status alone would not notice a change
     *    in the evaluation order, and the message is what distinguishes them.
     * 3. Requirement 3.7: an accepted candidate reaches the encoder untrimmed, and a rejected one
     *    reaches nothing at all — no token is validated, no password is encoded, and no Account is
     *    created.
     *
     * @param password The candidate password under test.
     * @param confirm  The confirmation value under test.
     */
    @ParameterizedTest(name = "[{index}] userPassword=''{0}'' confirm=''{1}''")
    @MethodSource("provideRetrofitPasswordPairs")
    public void testRegistrationRetrofitPreservesOutcomes(String password, String confirm) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String description = "userPassword '" + password + "' with confirm '" + confirm + "'";

        String expectedMessage = preRetrofitRejectionMessage(password, confirm);

        PasswordValidator.Result result = PasswordValidator.validate(password, confirm);

        // Requirement 3.6: the shared validator reaches the same accept-or-reject verdict the
        // pre-retrofit inline rule reached.
        assertEquals(
            Boolean.valueOf(expectedMessage == null),
            Boolean.valueOf(PasswordValidator.Result.ACCEPTED.equals(result)),
            "Shared validator verdict for " + description);

        // Requirement 3.6: and names the same cause, in the same words. messageFor returns null for
        // ACCEPTED, which is the accepting case of the oracle.
        assertEquals(expectedMessage, PasswordValidator.messageFor(result), "Shared validator message for " + description);

        // The firstName and lastName gate sits ahead of the password gate and is not under test, so
        // it is satisfied on every case; the token is supplied so an accepted candidate can proceed.
        data.put("firstName", "firstName");
        data.put("lastName", "lastName");
        data.put("token", "token");
        data.put("userPassword", password);
        data.put("confirm", confirm);

        if (expectedMessage != null) {
            ApiResponse response = authController.registration(data, parameters);

            // Requirement 3.9: the rejected outcome is preserved, status and message together.
            assertEquals(ApiStatus.ERROR, response.getMeta().getStatus(), "Status for rejected " + description);
            assertEquals(expectedMessage, response.getMeta().getMessage(), "Message for rejected " + description);

            // Requirement 3.7: the rejection happens at the password gate, ahead of the token
            // validation, so nothing downstream of that gate runs.
            verifyNoInteractions(cryptoService);
            verify(vireoUserCredentialsService, never()).createUserFromRegistration(any(), any(), any(), any());

            return;
        }

        // Warning the tokenDaysOld calculation is unsafe and can fail when the token time is less than the current time due to the subtraction.
        String[] content = { "" + Instant.now().toEpochMilli(), "e@mail.com" };

        when(cryptoService.validateGenericToken(anyString(), anyString())).thenReturn(content);
        when(cryptoService.encodePassword(anyString())).thenReturn("encoded");
        when(vireoUserCredentialsService.createUserFromRegistration(anyString(), anyString(), anyString(), anyString())).thenReturn(user);

        ApiResponse response = authController.registration(data, parameters);

        // Requirement 3.9: the accepted outcome is preserved, status and message together.
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), "Status for accepted " + description);
        assertEquals(REGISTRATION_SUCCESS_MESSAGE, response.getMeta().getMessage(), "Message for accepted " + description);
        assertEquals(user, response.getPayload().get("User"), "Account returned for accepted " + description);

        // Requirement 3.7: the accepted candidate reaches the encoder as submitted, padding and
        // all, even though the length check that accepted it was applied to the trimmed value.
        verify(cryptoService).encodePassword(password);
    }
}
