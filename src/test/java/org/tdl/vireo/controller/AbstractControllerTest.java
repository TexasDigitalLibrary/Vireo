package org.tdl.vireo.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import org.tdl.vireo.mock.MockData;

import org.tdl.vireo.Application;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.util.AuthUtility;

import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.mapping.RestRequestMappingHandler;
import edu.tamu.framework.mapping.WebSocketRequestMappingHandler;
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
