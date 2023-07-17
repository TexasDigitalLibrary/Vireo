package org.tdl.vireo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.service.CryptoService;
import edu.tamu.weaver.email.service.MockEmailService;
import edu.tamu.weaver.token.service.TokenService;
import edu.tamu.weaver.utility.HttpUtility;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.utility.TemplateUtility;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public abstract class AbstractControllerTest extends MockData {

    protected static final String SECRET_PROPERTY_NAME = "secret";
    protected static final String SECRET_VALUE = "verysecretsecret";

    protected static final String AUTH_SECRET_KEY_PROPERTY_NAME = "secret";
    protected static final String AUTH_SECRET_KEY_VALUE = "verysecretsecret";

    protected static final String AUTH_ISSUER_KEY_PROPERTY_NAME = "issuer";
    protected static final String AUTH_ISSUER_KEY_VALUE = "localhost";

    protected static final String AUTH_DURATION_PROPERTY_NAME = "duration";
    protected static final int AUTH_DURATION_VALUE = 2;

    protected static final String AUTH_KEY_PROPERTY_NAME = "key";
    protected static final Key AUTH_KEY_VALUE = new SecretKeySpec(SECRET_VALUE.getBytes(), "AES");

    protected static final String SHIB_KEYS_PROPERTY_NAME = "shibKeys";
    protected static final String[] SHIB_KEYS = new String[] { "netid", "uin", "lastName", "firstName", "email" };

    protected static final String SHIB_SUBJECT_PROPERTY_NAME = "email";

    protected static final String EMAIL_HOST_PROPERTY_NAME = "host";
    protected static final String EMAIL_HOST_VALUE = "relay.tamu.edu";

    protected static final String HTTP_DEFAULT_TIMEOUT_NAME = "DEFAULT_TIMEOUT";
    protected static final int HTTP_DEFAULT_TIMEOUT_VALUE = 10000;

    @Spy
    protected ObjectMapper objectMapper;

    @Spy
    protected BCryptPasswordEncoder passwordEncoder;

    // Must use MockBean rather than Mock here because LookAndFeelController.executeLogoReset() calls simpMessagingTemplate.
    @MockBean
    protected SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    protected Environment env;

    @Mock
    protected TokenService tokenService;

    @Mock
    protected MockEmailService mockEmailService;

    @InjectMocks
    protected HttpUtility httpUtility;

    @Mock
    protected CryptoService cryptoService;

    @Mock
    protected TemplateUtility templateUtility;

    protected Credentials TEST_CREDENTIALS = new Credentials();

}
