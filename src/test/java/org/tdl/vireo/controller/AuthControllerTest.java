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

    // Property 11: registration retrofit preserves outcomes. Additive; existing tests above are unchanged.

    private static final long RETROFIT_GENERATOR_SEED = 20250101L;

    private static final int RETROFIT_RANDOM_CASE_COUNT = 48;

    private static final String REGISTRATION_SUCCESS_MESSAGE = "Registration was successfull. Please login.";

    /** Values crossed with themselves: missing, whitespace-only, the trimmed length 5/6 boundary, and untrimmed pass-through. */
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

    /** Full cross product of the values plus fixed-seed random pairs (equal, perturbed, mixed, null confirm). */
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

    /** Alters one character so the result never equals the input. */
    private static String perturbRetrofitValue(String value, Random random) {
        if (value == null || value.length() == 0) {
            return "x";
        }

        char[] characters = value.toCharArray();

        int position = random.nextInt(characters.length);

        characters[position] = characters[position] == 'x' ? 'y' : 'x';

        return new String(characters);
    }

    /** Transcription of the pre-retrofit inline rule; deliberately not a call to PasswordValidator, so it is an independent oracle. */
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

    /** The retrofitted handler gives the same status and message as the pre-retrofit rule, and an accepted password reaches the encoder untrimmed. */
    @ParameterizedTest(name = "[{index}] userPassword=''{0}'' confirm=''{1}''")
    @MethodSource("provideRetrofitPasswordPairs")
    public void testRegistrationRetrofitPreservesOutcomes(String password, String confirm) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String description = "userPassword '" + password + "' with confirm '" + confirm + "'";

        String expectedMessage = preRetrofitRejectionMessage(password, confirm);

        PasswordValidator.Result result = PasswordValidator.validate(password, confirm);

        // Validator verdict matches the pre-retrofit rule.
        assertEquals(
            Boolean.valueOf(expectedMessage == null),
            Boolean.valueOf(PasswordValidator.Result.ACCEPTED.equals(result)),
            "Shared validator verdict for " + description);

        // Same message; messageFor returns null for ACCEPTED.
        assertEquals(expectedMessage, PasswordValidator.messageFor(result), "Shared validator message for " + description);

        // Name gate is not under test, so satisfy it; the token lets an accepted candidate proceed.
        data.put("firstName", "firstName");
        data.put("lastName", "lastName");
        data.put("token", "token");
        data.put("userPassword", password);
        data.put("confirm", confirm);

        if (expectedMessage != null) {
            ApiResponse response = authController.registration(data, parameters);

            assertEquals(ApiStatus.ERROR, response.getMeta().getStatus(), "Status for rejected " + description);
            assertEquals(expectedMessage, response.getMeta().getMessage(), "Message for rejected " + description);

            // Rejection happens before token validation.
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

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), "Status for accepted " + description);
        assertEquals(REGISTRATION_SUCCESS_MESSAGE, response.getMeta().getMessage(), "Message for accepted " + description);
        assertEquals(user, response.getPayload().get("User"), "Account returned for accepted " + description);

        // Reaches the encoder as submitted, padding included.
        verify(cryptoService).encodePassword(password);
    }
}
