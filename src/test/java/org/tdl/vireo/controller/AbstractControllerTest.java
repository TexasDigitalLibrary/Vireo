package org.tdl.vireo.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.tdl.vireo.Application;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.runner.OrderedRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.mapping.RestRequestMappingHandler;
import edu.tamu.framework.mapping.WebSocketRequestMappingHandler;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
public abstract class AbstractControllerTest {
    
    protected static final String jwtString = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsYXN0TmFtZSI6IkphY2siLCJmaXJzdE5hbWUiOiJEYW5pZWxzIiwicm9sZSI6IlJPTEVfQURNSU4iLCJuZXRpZCI6ImFnZ2llSmFjayIsInVpbiI6IjEyMzQ1Njc4OSIsImV4cCI6IjQ2MDI0NDc2MzEwODUiLCJlbWFpbCI6ImFnZ2llSmFja0B0YW11LmVkdSJ9.eDv5jC71ywYPuwMNU-fIhMlMk2XQFJw6vVEqXpUPeRI";

    protected static final byte[] payload = new byte[] {};
    
    @Autowired 
    protected AbstractSubscribableChannel clientInboundChannel;

    @Autowired 
    protected AbstractSubscribableChannel brokerChannel;

    protected MockChannelInterceptor brokerChannelInterceptor;
    
    @Autowired
    protected WebApplicationContext context;
    
//    @Autowired
//    @Qualifier("resourceHandlerMapping")
//    protected SimpleUrlHandlerMapping simpleUrlHandlerMapping;
//    
//    @Autowired
//    protected RequestMappingHandlerAdapter requestMappingHandlerAdapter;
//    
//    @Autowired
//    protected RestRequestMappingHandler restRequestMappingHandler;
//    
//    @Autowired
//    protected WebSocketRequestMappingHandler webSocketRequestMappingHandler;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    protected MockMvc mockMvc;
    
    @Before
    public abstract void setup();
    
    
}
