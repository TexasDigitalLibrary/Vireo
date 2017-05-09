package org.tdl.vireo.controller;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.util.TemplateUtility;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.util.AuthUtility;
import edu.tamu.framework.util.HttpUtility;
import edu.tamu.framework.util.JwtUtility;
import edu.tamu.framework.util.MockEmailUtility;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public abstract class AbstractControllerTest extends MockData {

    protected static final String SECRET_PROPERTY_NAME = "secret";
    protected static final String SECRET_VALUE = "verysecretsecret";

    protected static final String JWT_SECRET_KEY_PROPERTY_NAME = "secret_key";
    protected static final String JWT_SECRET_KEY_VALUE = "verysecretsecret";

    protected static final String JWT_EXPIRATION_PROPERTY_NAME = "expiration";
    protected static final Long JWT_EXPIRATION_VALUE = 120000L;

    protected static final String SHIB_KEYS_PROPERTY_NAME = "shibKeys";
    protected static final String[] SHIB_KEYS = new String[] { "netid", "uin", "lastName", "firstName", "email" };

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
    @Qualifier("mockEmailUtility")
    protected MockEmailUtility emailUtility;

    @Spy
    @InjectMocks
    protected HttpUtility httpUtility;

    @Spy
    @InjectMocks
    protected AuthUtility authUtility;

    @Spy
    @InjectMocks
    protected JwtUtility jwtUtility;

    @Spy
    @InjectMocks
    protected TemplateUtility templateUtility;

    protected Credentials TEST_CREDENTIALS = new Credentials();

}
