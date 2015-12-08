package org.tdl.vireo.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.enums.ApiResponseType;
import edu.tamu.framework.model.ApiResponse;

@ActiveProfiles({"test"})
public class AuthControllerTest extends AbstractControllerTest {
    
    public static final String REGISTRATION_TEMPLATE = "SYSTEM New User Registration";

	@Mock
    private UserRepo userRepo;
	
	@Mock
    private EmailTemplateRepo emailTemplateRepo;
	
	@InjectMocks
    private AuthController authController;
	
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
        MockitoAnnotations.initMocks(this);
    	
    	mockUsers = Arrays.asList(new User[] {TEST_USER, TEST_USER2, TEST_USER3, TEST_USER4});
    	
    	ReflectionTestUtils.setField(authUtility, SECRET_PROPERTY_NAME, SECRET_VALUE);
    	
    	ReflectionTestUtils.setField(authUtility, EXPIRATION_PROPERTY_NAME, EXPIRATION_VALUE);
    	
    	//ReflectionTestUtils.setField(emailService, EMAIL_HOST_PROPERTY_NAME, EMAIL_HOST_VALUE);

    	TEST_CREDENTIALS.setFirstName(TEST_USER_FIRST_NAME);
    	TEST_CREDENTIALS.setLastName(TEST_USER_LAST_NAME);
    	TEST_CREDENTIALS.setEmail(TEST_USER_EMAIL);
    	TEST_CREDENTIALS.setRole(TEST_USER_ROLE);
        
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
        
        Mockito.when(emailTemplateRepo.findByNameOverride(REGISTRATION_TEMPLATE)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new EmailTemplate(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
            }}
        );
        
    }
    
    @Test
    @Order(value = 1)
    @SuppressWarnings("unchecked")
    public void testRegisterEmail() {    	
    	Map<String, String[]> parameters = new HashMap<String, String[]>();
    	
    	parameters.put("email", new String[] {TEST_EMAIL});
    	
    	ApiResponse response = authController.registration(null, parameters);
    	 
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(TEST_EMAIL, ((String[]) ((Map<String, String[]>) response.getPayload().get("HashMap")).get("email"))[0]);
    }
    
    @Test
    @Order(value = 2)
    public void testRegister() throws Exception {
    	String token = authUtility.generateToken(TEST_USER_EMAIL, EMAIL_VERIFICATION_TYPE);    	
    	Map<String, String> data = new HashMap<String, String>();    	
    	data.put("token", token);
    	data.put("email", TEST_USER_EMAIL);
    	data.put("firstName", TEST_USER_FIRST_NAME);
    	data.put("lastName", TEST_USER_LAST_NAME);
    	data.put("password", TEST_USER_PASSWORD);
    	data.put("confirm", TEST_USER_CONFIRM);
    	
    	ApiResponse response = authController.registration(objectMapper.convertValue(data, JsonNode.class).toString(), new HashMap<String, String[]>());
   	 
    	User user = (User) response.getPayload().get("User");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(TEST_USER_FIRST_NAME, user.getFirstName());
    	assertEquals(TEST_USER_LAST_NAME, user.getLastName());
    	assertEquals(TEST_USER_EMAIL, user.getEmail());
    	assertEquals(TEST_USER_ROLE, user.getRole());
    }
    
    @Test
    @Order(value = 3)
    public void testLogin() throws Exception {

    	testRegister();
    	
    	Map<String, String> data = new HashMap<String, String>();
    	data.put("email", TEST_USER_EMAIL);
    	data.put("password", TEST_USER_PASSWORD);
    	
    	ApiResponse response = authController.login(objectMapper.convertValue(data, JsonNode.class).toString());
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    }
    	 	 
}
