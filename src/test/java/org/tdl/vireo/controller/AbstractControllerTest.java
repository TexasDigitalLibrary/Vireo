package org.tdl.vireo.controller;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.runner.OrderedRunner;
import org.tdl.vireo.util.TemplateUtility;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.util.AuthUtility;
import edu.tamu.framework.util.JwtUtility;
import edu.tamu.framework.util.MockEmailUtility;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@ActiveProfiles({"test"})
public abstract class AbstractControllerTest extends MockData {
	
	protected static final String SECRET_PROPERTY_NAME = "secret";
	protected static final String SECRET_VALUE = "verysecretsecret";
	
	protected static final String JWT_SECRET_KEY_PROPERTY_NAME = "secret_key";
	protected static final String JWT_SECRET_KEY_VALUE = "verysecretsecret";
	
	protected static final String JWT_EXPIRATION_PROPERTY_NAME = "expiration";
	protected static final Long JWT_EXPIRATION_VALUE = 120000L;
	
	protected static final String SHIB_KEYS_PROPERTY_NAME = "shibKeys";
    protected static final String[] SHIB_KEYS = new String[] { "netid", "uin", "lastName", "firstName", "email"};
	
	protected static final String EMAIL_HOST_PROPERTY_NAME = "host";
	protected static final String EMAIL_HOST_VALUE = "relay.tamu.edu";
	
	@Spy
	protected ObjectMapper objectMapper;
	
	@Spy
	protected BCryptPasswordEncoder passwordEncoder;
	
	@Mock
    private SimpMessagingTemplate simpMessagingTemplate;
	
	@Mock
    private Environment env;
	
	@Spy @InjectMocks
	protected AuthUtility authUtility;
	
	@Spy @InjectMocks
	protected JwtUtility jwtUtility;
	
	@Autowired @Spy
	@Qualifier("mockEmailUtility")
    protected MockEmailUtility emailUtility;

	@Spy @InjectMocks
    protected TemplateUtility templateUtility;
    	
	protected Credentials TEST_CREDENTIALS = new Credentials();
    
}
