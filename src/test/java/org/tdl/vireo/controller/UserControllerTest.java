package org.tdl.vireo.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.io.IOException;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.test.util.ReflectionTestUtils;

import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.controller.UserController;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.util.AuthUtility;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.mapping.RestRequestMappingHandler;
import edu.tamu.framework.enums.ApiResponseType;
import edu.tamu.framework.util.EmailUtility;

public class UserControllerTest extends AbstractControllerTest {

	@Mock
    private UserRepo userRepo;
	
	@InjectMocks
    private UserController userController;

	
    private static List<User> mockUsers;
    
    public User findByEmail(String email) {    	
        for(User user : mockUsers) {
            if(user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
    
    public User updateUser(User updatedUser) {    	
        for(User user : mockUsers) {
            if(user.getEmail().equals(updatedUser.getEmail())) {
            	user.setEmail(updatedUser.getEmail());
            	user.setFirstName(updatedUser.getFirstName());
            	user.setLastName(updatedUser.getLastName());
            	user.setPassword(updatedUser.getPassword());
            	user.setRole(updatedUser.getRole());
                return user;
            }
        }
        return null;
    }
    
    @Before
    public void setup() {
    	
    	mockUsers = Arrays.asList(new User[] {TEST_USER, aggieJack, aggieJill, jimInny});
    	
    	ReflectionTestUtils.setField(authUtility, SECRET_PROPERTY_NAME, SECRET_VALUE);
    	
    	ReflectionTestUtils.setField(authUtility, EXPIRATION_PROPERTY_NAME, EXPIRATION_VALUE);
    	
    	ReflectionTestUtils.setField(emailUtility, EMAIL_HOST_PROPERTY_NAME, EMAIL_HOST_VALUE);

    	TEST_CREDENTIALS.setFirstName(TEST_USER_FIRST_NAME);
    	TEST_CREDENTIALS.setLastName(TEST_USER_LAST_NAME);
    	TEST_CREDENTIALS.setEmail(TEST_USER_EMAIL);
    	TEST_CREDENTIALS.setRole(TEST_USER_ROLE);
    	        
        MockitoAnnotations.initMocks(this);
        
        Mockito.when(userRepo.findAll()).thenReturn(mockUsers);
        
        Mockito.when(userRepo.create(any(String.class), any(String.class), any(String.class), any(Role.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return userRepo.save(new User((String) invocation.getArguments()[0], 
                							  (String) invocation.getArguments()[1], 
                							  (String) invocation.getArguments()[2], 
                							  (Role) invocation.getArguments()[3]));
            }}
        );
                
        Mockito.when(userRepo.findByEmail(any(String.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {            	            	
                return findByEmail((String) invocation.getArguments()[0]);
            }}
        );
        
        Mockito.when(userRepo.save(any(User.class))).then(new Answer<Object>() {
               @Override
               public Object answer(InvocationOnMock invocation) throws Throwable {
                   return updateUser((User) invocation.getArguments()[0]);
               }}
        );
    }

    @Test
    public void testUserCredentials() {        
    	ApiResponse response = userController.credentials(TEST_CREDENTIALS);
    	
    	Credentials credentials = (Credentials) response.getPayload().get("Credentials");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	assertEquals(TEST_USER_LAST_NAME, credentials.getLastName());
        assertEquals(TEST_USER_FIRST_NAME, credentials.getFirstName());
        assertEquals(TEST_USER_EMAIL, credentials.getEmail());
        assertEquals(TEST_USER_ROLE, credentials.getRole());
    }
        
    @Test
    public void testRegisterEmail() {    	
    	Map<String, String[]> parameters = new HashMap<String, String[]>();
    	
    	parameters.put("email", new String[] {TEST_EMAIL});
    	
    	ApiResponse response = userController.registration(null, parameters);
    	 
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(TEST_EMAIL, ((String[]) ((Map) response.getPayload().get("HashMap")).get("email"))[0]);
    }
    
    @Test
    public void testRegister() throws Exception {
    	String token = authUtility.generateToken(TEST_USER_EMAIL, EMAIL_VERIFICATION_TYPE);    	
    	Map<String, String> data = new HashMap<String, String>();    	
    	data.put("token", token);
    	data.put("email", TEST_USER_EMAIL);
    	data.put("firstName", TEST_USER_FIRST_NAME);
    	data.put("lastName", TEST_USER_LAST_NAME);
    	data.put("password", TEST_USER_PASSWORD);
    	data.put("confirm", TEST_USER_CONFIRM);
    	
    	ApiResponse response = userController.registration(objectMapper.convertValue(data, JsonNode.class).toString(), new HashMap<String, String[]>());
   	 
    	User user = (User) response.getPayload().get("User");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(TEST_USER_FIRST_NAME, user.getFirstName());
    	assertEquals(TEST_USER_LAST_NAME, user.getLastName());
    	assertEquals(TEST_USER_EMAIL, user.getEmail());
    	assertEquals(TEST_USER_ROLE, user.getRole());
    }
    
    @Test
    public void testLogin() throws Exception {

    	testRegister();
    	
    	Map<String, String> data = new HashMap<String, String>();
    	data.put("email", TEST_USER_EMAIL);
    	data.put("password", TEST_USER_PASSWORD);
    	
    	ApiResponse response = userController.login(objectMapper.convertValue(data, JsonNode.class).toString());
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    }
    
    @Test
    public void testAllUser() {
    	
    	ApiResponse response = userController.allUsers();

    	List<User> allUsers = (List<User>) ((Map) response.getPayload().get("HashMap")).get("list");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(4, allUsers.size());
    	
    	for(User user : allUsers) {
    		switch(user.getEmail()) {
	    		case TEST_USER_EMAIL: {
					assertEquals(TEST_USER_FIRST_NAME, user.getFirstName());
					assertEquals(TEST_USER_LAST_NAME, user.getLastName());					 
					assertEquals(TEST_USER_ROLE, user.getRole());
				}; break;
    			case aggieJackEmail: {
    				assertEquals("Jack", user.getFirstName());
    				assertEquals("Daniels", user.getLastName());
    				assertEquals("ROLE_ADMIN", user.getRole());
    			}; break;
    			case aggieJillEmail: {
    				assertEquals("Jill", user.getFirstName());
    				assertEquals("Daniels", user.getLastName());					 
    				assertEquals("ROLE_MANAGER", user.getRole());
    			}; break;
    			case jimInnyEmail: {
    				assertEquals("Jim", user.getFirstName());
    				assertEquals("Inny", user.getLastName());
    				assertEquals("ROLE_USER", user.getRole());
    			}; break;
    		}
    	}
    }
	 
    @Test
    public void testUpdateRole() throws Exception {
    	Map<String, String> data = new HashMap<String, String>();
    	data.put("email", TEST_USER_EMAIL);
    	data.put("role", TEST_USER_ROLE_UPDATE);
    	
    	ApiResponse response = userController.updateRole(objectMapper.convertValue(data, JsonNode.class).toString());
    	
    	User user = (User) response.getPayload().get("User");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(TEST_USER_FIRST_NAME, user.getFirstName());
    	assertEquals(TEST_USER_LAST_NAME, user.getLastName());
    	assertEquals(TEST_USER_EMAIL, user.getEmail());
    	assertEquals(TEST_USER_ROLE_UPDATE, user.getRole());
    }
	 	 
}
