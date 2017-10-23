package org.tdl.vireo.controller;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.utility.TemplateUtility;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.service.CryptoService;
import edu.tamu.weaver.email.service.MockEmailService;
import edu.tamu.weaver.token.service.TokenService;
import edu.tamu.weaver.utility.HttpUtility;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public abstract class AbstractControllerTest extends MockData {

    protected static final String SECRET_PROPERTY_NAME = "secret";
    protected static final String SECRET_VALUE = "verysecretsecret";

    protected static final String JWT_SECRET_KEY_PROPERTY_NAME = "secret";
    protected static final String JWT_SECRET_KEY_VALUE = "verysecretsecret";
    
    protected static final String JWT_ISSUER_KEY_PROPERTY_NAME = "issuer";
    protected static final String JWT_ISSUER_KEY_VALUE = "localhost";

    protected static final String JWT_DURATION_PROPERTY_NAME = "duration";
    protected static final int JWT_DURATION_VALUE = 2;

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

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private Environment env;

    @Mock
    protected MockEmailService mockEmailService;

    @Spy
    @InjectMocks
    protected HttpUtility httpUtility;

    @Spy
    @InjectMocks
    protected CryptoService cryptoService;

    @Spy
    @InjectMocks
    protected TokenService tokenService;

    @Spy
    @InjectMocks
    protected TemplateUtility templateUtility;

    protected Credentials TEST_CREDENTIALS = new Credentials();

}
