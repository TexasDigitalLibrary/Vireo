package org.tdl.vireo.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.tdl.vireo.Application;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.runner.OrderedRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
public abstract class AbstractIntegrationTest extends MockData {
	
    protected static final String jwtString = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmaXJzdE5hbWUiOiJKYWNrIiwibGFzdE5hbWUiOiJEYW5pZWxzIiwicm9sZSI6IlJPTEVfQURNSU4iLCJuZXRpZCI6ImFnZ2llSmFjayIsInVpbiI6IjEyMzQ1Njc4OSIsImV4cCI6IjQ2MDI1NTQ0NTQ3NDciLCJlbWFpbCI6ImFnZ2llSmFja0B0YW11LmVkdSJ9.4lAD4I7UwPJYzh7lqExU_vOlPs172JxzeML6sl5IMvk";

    protected static final byte[] payload = new byte[] {};
        
    @Autowired 
    protected AbstractSubscribableChannel clientInboundChannel;

    @Autowired 
    protected AbstractSubscribableChannel brokerChannel;

    protected MockChannelInterceptor brokerChannelInterceptor;
    
    @Autowired
    protected WebApplicationContext context;
            
    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;
        
    @Before
    public abstract void setup();
    
    @After
    public abstract void cleanup();
    
    protected void StompConnect() {
    	StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);
        
        headers.setSubscriptionId("0");
        headers.setDestination("/connect");
        
        headers.setNativeHeader("id", "0");
        headers.setNativeHeader("jwt", jwtString);
                
        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
        
        clientInboundChannel.send(message);
    }
    
    public String StompRequest(String destination, Map<String, String> data) throws InterruptedException {
    	
    	String root = destination.split("/")[1];
    	
    	String sessionId = String.valueOf(Math.round(Math.random()*100000));
    	String id = String.valueOf(Math.round(Math.random()*100000));

    	StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        
        headers.setDestination("/ws" + destination);
        headers.setSessionId(sessionId);
        
        headers.setNativeHeader("id", id);
        headers.setNativeHeader("jwt", jwtString);
        
        if(data != null) {
        	headers.setNativeHeader("data", objectMapper.convertValue(data, JsonNode.class).toString());
        }
        
        headers.setSessionAttributes(new HashMap<String, Object>());
                
        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
        
        brokerChannelInterceptor.setIncludedDestinations("/queue/" + root + "/**");
        
        clientInboundChannel.send(message);

        Message<?> reply = brokerChannelInterceptor.awaitMessage(5);
        
        assertNotNull(reply);
                
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        
        assertEquals("/queue" + destination + "-user" + sessionId, replyHeaders.getDestination());
        
        return new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
    }
    
}
