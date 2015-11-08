package org.tdl.vireo.integration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*; 
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
import com.fasterxml.jackson.databind.JsonNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.tamu.framework.model.Credentials;

import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.util.AuthUtility;

import org.junit.runner.RunWith;

import edu.tamu.framework.mapping.RestRequestMappingHandler;

public class UserIntegrationTest extends AbstractIntegrationTest {

	@Autowired
    private UserRepo userRepo;
		
    @Autowired
    private AuthUtility authUtility;

    
    private final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";
        
    private final static String TEST_USER_EMAIL       = "testUser@email.com";
    private final static String TEST_USER_FIRST_NAME  = "Test";
    private final static String TEST_USER_LAST_NAME   = "User";
    private final static String TEST_USER_PASSWORD    = "abc123";
    private final static String TEST_USER_CONFIRM     = "abc123";
    private final static String TEST_USER_ROLE        = "ROLE_USER";
    private final static String TEST_USER_ROLE_UPDATE = "ROLE_ADMIN";
    
    private User TEST_USER = new User(TEST_USER_EMAIL, TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME, Role.USER);    
   
    private final static String aggieJackEmail = "aggieJack@tamu.edu";
    private final static String aggieJillEmail = "aggieJill@tamu.edu";
    private final static String jimInnyEmail = "jimInny@tdl.org";
    
    @Before
    public void setup() {
    			
    	userRepo.create(aggieJackEmail, "Jack", "Daniels", Role.ADMINISTRATOR);
    	userRepo.create(aggieJillEmail, "Jill", "Daniels", Role.MANAGER);
    	userRepo.create(jimInnyEmail, "Jim", "Inny", Role.USER);
        
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                        
        brokerChannelInterceptor = new MockChannelInterceptor();
        
        brokerChannel.addInterceptor(brokerChannelInterceptor);
        
        StompConnect();
    }

    @Test
    @Order(value = 1)
    public void testUserCredentialsOverStomp() throws InterruptedException, IOException {        
    	String responseJson = StompRequest("/user/credentials", null);
        
    	Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        Credentials shib = new Credentials((Map<String, String>) payload.get("Credentials"));
        
        assertEquals("Daniels", shib.getLastName());
        assertEquals("Jack", shib.getFirstName());
        assertEquals("aggieJack", shib.getNetid());
        assertEquals("123456789", shib.getUin());
        assertEquals("aggieJack@tamu.edu", shib.getEmail());
        assertEquals("ROLE_ADMIN", shib.getRole());
    }
    
    @Test
    @Order(value = 2)
    public void testUserCredentialsOverRest() throws Exception {    	    	
        mockMvc.perform(get("/user/credentials")
        					.contentType(MediaType.APPLICATION_JSON)
        					.header("jwt", jwtString))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.meta.type").value("SUCCESS"))
           .andExpect(jsonPath("$.payload.Credentials.firstName").value("Jack"))
           .andExpect(jsonPath("$.payload.Credentials.lastName").value("Daniels"))
           .andExpect(jsonPath("$.payload.Credentials.netid").value("aggieJack"))
           .andExpect(jsonPath("$.payload.Credentials.uin").value("123456789"))
           .andExpect(jsonPath("$.payload.Credentials.email").value("aggieJack@tamu.edu"))           
           .andExpect(jsonPath("$.payload.Credentials.role").value("ROLE_ADMIN"))
           .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    @Order(value = 3)
    public void testRegisterEmail() throws Exception {    	    	
        mockMvc.perform(get("/user/register")
        					.param("email", TEST_USER_EMAIL)
        					.contentType(MediaType.APPLICATION_JSON))           
           .andExpect(status().isOk())           
           .andExpect(jsonPath("$.meta.type").value("SUCCESS"))
           .andExpect(jsonPath("$.payload.UnmodifiableMap.email").value(TEST_USER_EMAIL))
           .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    @Order(value = 4)
    public void testRegister() throws Exception {    	
    	String token = authUtility.generateToken(TEST_USER_EMAIL, EMAIL_VERIFICATION_TYPE);    	
    	Map<String, String> data = new HashMap<String, String>();    	
    	data.put("token", token);
    	data.put("email", TEST_USER_EMAIL);
    	data.put("firstName", TEST_USER_FIRST_NAME);
    	data.put("lastName", TEST_USER_LAST_NAME);
    	data.put("password", TEST_USER_PASSWORD);
    	data.put("confirm", TEST_USER_CONFIRM);    	
        mockMvc.perform(get("/user/register")
        					.contentType(MediaType.APPLICATION_JSON)
        					.header("data", objectMapper.convertValue(data, JsonNode.class)))
           .andExpect(status().isOk())           
           .andExpect(jsonPath("$.meta.type").value("SUCCESS"))
           .andExpect(jsonPath("$.payload.User.email").value(TEST_USER_EMAIL))
           .andExpect(jsonPath("$.payload.User.firstName").value(TEST_USER_FIRST_NAME))
           .andExpect(jsonPath("$.payload.User.lastName").value(TEST_USER_LAST_NAME))
           .andExpect(jsonPath("$.payload.User.password").doesNotExist())
           .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    @Order(value = 5)
    public void testLogin() throws Exception {

    	testRegister();
    	
    	Map<String, String> data = new HashMap<String, String>();
    	data.put("email", TEST_USER_EMAIL);
    	data.put("password", TEST_USER_PASSWORD);
        mockMvc.perform(get("/user/login")
        					.contentType(MediaType.APPLICATION_JSON)
        					.header("data", objectMapper.convertValue(data, JsonNode.class)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.meta.type").value("SUCCESS"))
           .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    @Order(value = 6)
    public void testAllUser() throws Exception {
		 	
    	String responseJson = StompRequest("/user/all", null);
    	 
    	Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
		 
    	Map<String, Object> contentObj = (Map<String, Object>) responseObject.get("payload");
		 
    	Map<String, Object> mapObj = (Map<String, Object>) contentObj.get("HashMap");
		 
    	List<Map<String, Object>> listMap =  (List<Map<String, Object>>) mapObj.get("list");

    	assertEquals(3, listMap.size());
    	
    	for(Map<String, Object> map : listMap) {
    		String email = (String) map.get("email");
    		switch(email) {	    		
    			case aggieJackEmail: {
    				assertEquals("Jack", (String) map.get("firstName"));
    				assertEquals("Daniels", (String) map.get("lastName"));					 
    				assertEquals("ROLE_ADMIN", (String) map.get("role"));
    			}; break;
    			case aggieJillEmail: {
    				assertEquals("Jill", (String) map.get("firstName"));
    				assertEquals("Daniels", (String) map.get("lastName"));					 
    				assertEquals("ROLE_MANAGER", (String) map.get("role"));
    			}; break;
    			case jimInnyEmail: {
    				assertEquals("Jim", (String) map.get("firstName"));
    				assertEquals("Inny", (String) map.get("lastName"));
    				assertEquals("ROLE_USER", (String) map.get("role"));
    			}; break;
    		}
    	}		 
	 }
	 
	 @Test
	 @Order(value = 7)
	 public void testUpdateRole() throws Exception {
		 
		 testRegister();
		 
		 Map<String, String> data = new HashMap<String, String>();
		 data.put("email", TEST_USER_EMAIL);
		 data.put("role", TEST_USER_ROLE_UPDATE);
	    	
		 String responseJson = StompRequest("/user/update-role", data);
		 		 
		 User testUser = userRepo.findByEmail(TEST_USER_EMAIL);
		 
		 assertEquals(TEST_USER_FIRST_NAME, testUser.getFirstName());
		 assertEquals(TEST_USER_LAST_NAME, testUser.getLastName());
		 assertEquals(TEST_USER_EMAIL, testUser.getEmail());
		 assertEquals(TEST_USER_ROLE_UPDATE, testUser.getRole());
	 }
	 
	 @After
	 public void cleanup() {
		 userRepo.deleteAll();
	 }
	 
}
