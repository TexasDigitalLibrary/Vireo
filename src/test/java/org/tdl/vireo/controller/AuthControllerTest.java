package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
}
