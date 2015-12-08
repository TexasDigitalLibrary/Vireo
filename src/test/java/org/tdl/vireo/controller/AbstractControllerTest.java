package org.tdl.vireo.controller;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.runner.OrderedRunner;
import org.tdl.vireo.service.MockEmailServiceImpl;
import org.tdl.vireo.util.AuthUtility;
import org.tdl.vireo.util.TemplateUtility;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.Credentials;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@ActiveProfiles({"test"})
public abstract class AbstractControllerTest extends MockData {
	
	protected static final String SECRET_PROPERTY_NAME = "secret";
	protected static final String SECRET_VALUE = "verysecretsecret";
	
	protected static final String EXPIRATION_PROPERTY_NAME = "expiration";
	protected static final Long EXPIRATION_VALUE = 120000L;
	
	protected static final String EMAIL_HOST_PROPERTY_NAME = "host";
	protected static final String EMAIL_HOST_VALUE = "relay.tamu.edu";
	
	@Spy
	protected ObjectMapper objectMapper;
	
	@Spy
	protected BCryptPasswordEncoder passwordEncoder;
	
	@Mock
    private SimpMessagingTemplate simpMessagingTemplate;
	
	@Spy @InjectMocks
	protected AuthUtility authUtility;
	
	@Autowired @Spy
    protected MockEmailServiceImpl emailService;

	@Spy @InjectMocks
    protected TemplateUtility templateUtility;
    	
	protected Credentials TEST_CREDENTIALS = new Credentials();
    
}
