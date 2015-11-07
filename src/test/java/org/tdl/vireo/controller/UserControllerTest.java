package org.tdl.vireo.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.tamu.framework.model.Credentials;

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
        
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                
        brokerChannelInterceptor = new MockChannelInterceptor();
        
        brokerChannel.addInterceptor(brokerChannelInterceptor);
        
        StompConnect();
    }

    @Test
    @Order(value = 1)
    public void testUserCredentials() throws InterruptedException, IOException {
        
    	String responseJson = StompRequest("/user/credentials");
        
    	Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
                
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");
        
        @SuppressWarnings("unchecked")
        Credentials shib = new Credentials((Map<String, String>) payload.get("Credentials"));
        
        assertEquals("Daniels", shib.getLastName());
        assertEquals("Jack", shib.getFirstName());
        assertEquals("aggieJack", shib.getNetid());
        assertEquals("123456789", shib.getUin());
        assertEquals("aggieJack@tamu.edu", shib.getEmail());                
    }

}
