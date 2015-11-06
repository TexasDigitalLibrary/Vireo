package org.tdl.vireo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.mapping.RestRequestMappingHandler;

public class UserControllerTest extends AbstractControllerTest {
    
    @Mock
    private UserRepo userRepo;
    
    List<User> mockAppUsers = new ArrayList<User>();
    
    private final static String aggieJackEmail = "aggieJack@tamu.edu";
    private final static String aggieJillEmail = "aggieJill@tamu.edu";
    private final static String jimInnyEmail = "jimInny@tdl.org";
    
    private User aggieJack = new User(aggieJackEmail, "Jack", "Daniels", Role.ADMINISTRATOR);
    private User aggieJill = new User(aggieJillEmail, "Jill", "Daniels", Role.MANAGER);
    private User jimInny = new User(jimInnyEmail, "Jim", "Inny", Role.USER);
    
    public User findByEmail(String email) {
        for(User appUser : mockAppUsers) {
            if(appUser.getEmail().equals(email)) {
                return appUser;
            }
        }
        return null;
    }
    
    public User updateAppUser(User updatedAppUser) {
        for(User appUser : mockAppUsers) {
            if(appUser.getEmail().equals(updatedAppUser.getEmail())) {
                appUser = updatedAppUser;
                return appUser;
            }
        }
        return null;
    }
    
    @Before
    public void setup() {
        mockAppUsers.add(aggieJack);
        mockAppUsers.add(aggieJill);
        mockAppUsers.add(jimInny);
        
        MockitoAnnotations.initMocks(this);
        
        Mockito.when(userRepo.findByEmail(aggieJackEmail)).thenReturn(findByEmail(aggieJackEmail));
        
        Mockito.when(userRepo.findAll()).thenReturn(mockAppUsers);
        
        Mockito.when(userRepo.save(aggieJack)).then(new Answer<Object>() {
               @Override
               public Object answer(InvocationOnMock invocation) throws Throwable {                    
                   return updateAppUser(aggieJack);
               }}
        );
        
//        Properties properties = new Properties();
//        
//        Map<String, Object> urlMap = new HashMap<String, Object>();
//        
//        if(restRequestMappingHandler != null) {
//            System.out.println("\n\nCONTEXT CONTAINS REST REQUEST MAPPING HANDLER!!\n\n");
//        }
//        
//        System.out.println("\n\n" + restRequestMappingHandler.getHandlerMethods() + "\n\n");
//        
//        restRequestMappingHandler.getHandlerMethods().keySet().forEach(i -> {
//            System.out.println("\n" + i.getPatternsCondition().getPatterns() + "\n");
//            urlMap.put((String) i.getPatternsCondition().getPatterns().toArray()[0], RestRequestMappingHandler.class);
//            
//            properties.setProperty((String) i.getPatternsCondition().getPatterns().toArray()[0], "userController");
//        });
//        
//        System.out.println("\n\n" + restRequestMappingHandler.getOrder() + "\n\n");
//        
//        if(webSocketRequestMappingHandler != null) {
//            System.out.println("\n\nCONTEXT CONTAINS WEB SOCKET REQUEST MAPPING HANDLER!!\n\n");
//        }
//        
//        System.out.println("\n\n" + webSocketRequestMappingHandler.getHandlerMethods() + "\n\n");
//                
//        webSocketRequestMappingHandler.getHandlerMethods().keySet().forEach(i -> {
//            System.out.println("\n" + i.getDestinationConditions().getPatterns() + "\n");
//        });
//        
//        if(simpleUrlHandlerMapping != null) {
//            System.out.println("\n\nCONTEXT CONTAINS SIMPLE URL HANDLER MAPPING!!\n\n");
//        }
//        
//        
//        System.out.println("\n\n" + urlMap + "\n\n");
//        
//        
//        
//        System.out.println("\n\n" + properties + "\n\n");
//        
//        simpleUrlHandlerMapping.setMappings(properties);
//        
//        simpleUrlHandlerMapping.setUrlMap(urlMap);
//        
//        simpleUrlHandlerMapping.setLazyInitHandlers(true);
//        
//        simpleUrlHandlerMapping.initApplicationContext();
//        
//        System.out.println("\n\n" + simpleUrlHandlerMapping.getUrlMap() + "\n\n");
//        
//        System.out.println("\n\n" + simpleUrlHandlerMapping.getHandlerMap() + "\n\n");
//        
//        System.out.println("\n\n" + simpleUrlHandlerMapping.getOrder() + "\n\n");
        
        if(context.containsBean("restRequestMappingHandler")) {
            System.out.println("\n\nCONTEXT CONTAINS REST REQUEST MAPPING HANDLER!!\n\n");
        }
        
        RestRequestMappingHandler restHandler = context.getBean(RestRequestMappingHandler.class);
                
        System.out.println("\n\nREST REQUEST MAPPING HANDLER ORDER: " + restHandler.getOrder() + "\n\n");
        
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                
        brokerChannelInterceptor = new MockChannelInterceptor();
        brokerChannel.addInterceptor(brokerChannelInterceptor);
        
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);
        
        headers.setSubscriptionId("0");
        headers.setDestination("/connect");
        
        headers.setNativeHeader("id", "0");
        headers.setNativeHeader("jwt", jwtString);
                
        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
        
        clientInboundChannel.send(message);
    }
    
    @Test   
    @Order(value = 1)
    public void testCredentialsOverHttp() throws Exception {
        
        MockHttpServletRequestBuilder getRequest = get("/user/credentials");
        
        getRequest.contentType(MediaType.APPLICATION_JSON);
        getRequest.header("jwt", jwtString);
        
        mockMvc.perform(getRequest)
           //.andExpect(status().isOk())
           //.andExpect(jsonPath("$.payload.Credentials.lastName").value("Daniels"))
           //.andExpect(jsonPath("$.payload.Credentials.firstName").value("Jack"))
           //.andExpect(jsonPath("$.payload.Credentials.email").value("aggieJack@tamu.edu"))
           //.andExpect(jsonPath("$.payload.Credentials.role").value("ROLE_ADMIN"))
           .andDo(MockMvcResultHandlers.print());
    }
    /*
    @Test
    @Order(value = 2)
    public void testCredentials() throws Exception {
        
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        
        headers.setDestination("/ws/user/credentials");
        headers.setSessionId("1");
        
        headers.setNativeHeader("id", "1");
        headers.setNativeHeader("jwt", jwtString);
        
        headers.setSessionAttributes(new HashMap<String, Object>());
        
        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
        
        brokerChannelInterceptor.setIncludedDestinations("/queue/user/**");
        
        clientInboundChannel.send(message);

        Message<?> reply = brokerChannelInterceptor.awaitMessage(5);
        
        assertNotNull(reply);
                
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        
        assertEquals("1", replyHeaders.getSessionId());         
        assertEquals("/queue/user/credentials-user1", replyHeaders.getDestination());
           
        
        String responseJson = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
        
        @SuppressWarnings("unchecked")
        Map<String, Object> contentObj = (Map<String, Object>) responseObject.get("content");
        
        @SuppressWarnings("unchecked")
        Credentials shib = new Credentials((Map<String, String>) contentObj.get("Credentials"));
        
        assertEquals("Daniels", shib.getLastName());
        assertEquals("Jack", shib.getFirstName());
        assertEquals("aggieJack", shib.getNetid());
        assertEquals("123456789", shib.getUin());
        assertEquals("aggieJack@tamu.edu", shib.getEmail());
                
    }
    */
}
