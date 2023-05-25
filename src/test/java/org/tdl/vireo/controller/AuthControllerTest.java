package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.auth.controller.AuthController;
import org.tdl.vireo.auth.service.VireoUserCredentialsService;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.VireoEmailSender;

@Transactional(propagation = Propagation.NEVER)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest extends AbstractControllerTest {

    public static final String REGISTRATION_TEMPLATE = "SYSTEM New User Registration";

    public static Long emailTemplatePosition = 0L;

    @MockBean
    private VireoEmailSender mockEmailSender;

    @Mock
    private UserRepo userRepo;

    @Mock
    private EmailTemplateRepo emailTemplateRepo;

    @Mock
    private VireoUserCredentialsService vireoUserCredentialsService;

    @InjectMocks
    private AuthController authController;

    private static List<User> mockUsers;

    @BeforeEach
    public void setup() throws MessagingException {
        mockUsers = Arrays.asList(new User[] { TEST_USER, TEST_USER2, TEST_USER3, TEST_USER4 });

        ReflectionTestUtils.setField(httpUtility, HTTP_DEFAULT_TIMEOUT_NAME, HTTP_DEFAULT_TIMEOUT_VALUE);
        ReflectionTestUtils.setField(cryptoService, SECRET_PROPERTY_NAME, SECRET_VALUE);

        TEST_CREDENTIALS.setFirstName(TEST_USER_FIRST_NAME);
        TEST_CREDENTIALS.setLastName(TEST_USER_LAST_NAME);
        TEST_CREDENTIALS.setEmail(TEST_USER_EMAIL);
        TEST_CREDENTIALS.setRole(TEST_USER_ROLE.toString());

        Mockito.lenient().when(userRepo.findAll()).thenReturn(mockUsers);

        ReflectionTestUtils.setField(authController, "url", "localhost:9000");

        Mockito.lenient().when(vireoUserCredentialsService.createUserFromRegistration(any(String.class), any(String.class), any(String.class), any(String.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return userRepo.save(new User((String) invocation.getArguments()[0], (String) invocation.getArguments()[1], (String) invocation.getArguments()[2], (String) invocation.getArguments()[3], TEST_USER_ROLE));
            }
        });

        Mockito.lenient().when(userRepo.findByEmail(any(String.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return findByEmail((String) invocation.getArguments()[0]);
            }
        });

        Mockito.lenient().when(userRepo.save(any(User.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return updateUser((User) invocation.getArguments()[0]);
            }
        });

        Mockito.lenient().when(emailTemplateRepo.findByNameOverride(REGISTRATION_TEMPLATE)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                EmailTemplate emailTemplate = new EmailTemplate(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
                emailTemplate.setPosition(emailTemplatePosition++);
                return emailTemplate;
            }
        });

        doCallRealMethod().when(mockEmailSender).sendEmail(any(String.class), any(String.class), any(String.class));
        doCallRealMethod().when(mockEmailSender).sendEmail(any(String[].class), any(String.class), any(String.class));
        doCallRealMethod().when(mockEmailSender).sendEmail(any(String.class), any(String[].class), any(String.class), any(String.class));

        doNothing().when(mockEmailSender).send(any(MimeMessage.class));
        doNothing().when(mockEmailSender).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterEmail() {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("email", TEST_EMAIL);

        ApiResponse response = authController.registration(null, parameters);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        assertEquals(TEST_EMAIL, ((String) ((Map<String, String>) response.getPayload().get("HashMap")).get("email")));
    }

    @Test
    public void testRegister() throws Exception {
        String token = cryptoService.generateGenericToken(TEST_USER_EMAIL, EMAIL_VERIFICATION_TYPE);
        Map<String, String> data = new HashMap<String, String>();
        data.put("token", token);
        data.put("email", TEST_USER_EMAIL);
        data.put("firstName", TEST_USER_FIRST_NAME);
        data.put("lastName", TEST_USER_LAST_NAME);
        data.put("userPassword", TEST_USER_PASSWORD);
        data.put("confirm", TEST_USER_CONFIRM);

        ApiResponse response = authController.registration(data, new HashMap<String, String>());

        User user = (User) response.getPayload().get("User");

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        assertEquals(TEST_USER_FIRST_NAME, user.getFirstName());
        assertEquals(TEST_USER_LAST_NAME, user.getLastName());
        assertEquals(TEST_USER_EMAIL, user.getEmail());
        assertEquals(TEST_USER_ROLE, user.getRole());
    }

    @Test
    public void testLogin() throws Exception {

        testRegister();

        Map<String, String> data = new HashMap<>();
        data.put("email", TEST_USER_EMAIL);
        data.put("userPassword", TEST_USER_PASSWORD);

        Map<String, Object> claims = new HashMap<>();
        claims.put("lastName", TEST_USER_LAST_NAME);
        claims.put("firstName", TEST_USER_FIRST_NAME);
        claims.put("netid", null);
        claims.put("uin", TEST_USER_EMAIL);
        claims.put("email", TEST_USER_EMAIL);

        when(tokenService.createToken(TEST_USER_EMAIL, claims)).thenReturn("jwt");

        ApiResponse response = authController.login(data);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    private User findByEmail(String email) {
        for (User user : mockUsers) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    private User updateUser(User updatedUser) {
        for (User user : mockUsers) {
            if (user.getEmail().equals(updatedUser.getEmail())) {
                user.setEmail(updatedUser.getEmail());
                user.setFirstName(updatedUser.getFirstName());
                user.setLastName(updatedUser.getLastName());
                user.setPassword(updatedUser.getPassword());
                user.setRole(updatedUser.getRole());
                return user;
            }
        }
        return null;
    }

}
