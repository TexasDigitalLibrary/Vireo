package org.tdl.vireo.controller;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.util.AuthUtility;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.util.EmailUtility;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractControllerTest extends MockData {
	
	protected static final String SECRET_PROPERTY_NAME = "secret";
	protected static final String SECRET_VALUE = "verysecretsecret";
	
	protected static final String EXPIRATION_PROPERTY_NAME = "expiration";
	protected static final Long EXPIRATION_VALUE = 120000L;
	
	protected static final String EMAIL_HOST_PROPERTY_NAME = "emailHost";
	protected static final String EMAIL_HOST_VALUE = "relay.tamu.edu";
	
	@Spy
	protected ObjectMapper objectMapper;
	
	@Spy
	protected BCryptPasswordEncoder passwordEncoder;
	
	@Mock
    private SimpMessagingTemplate simpMessagingTemplate;
	
	@Spy @InjectMocks
	protected AuthUtility authUtility;
	
    @Spy
    protected EmailUtility emailUtility;
    	
	protected Credentials TEST_CREDENTIALS = new Credentials();
    
}
