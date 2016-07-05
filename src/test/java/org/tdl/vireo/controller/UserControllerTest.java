package org.tdl.vireo.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.util.Arrays;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.enums.ApiResponseType;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.validation.ModelBindingResult;

public class UserControllerTest extends AbstractControllerTest {

	@Mock
    private UserRepo userRepo;
	
	@Mock
	private ValidationService validationService;
	
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
    
    public User validateUpdateRole(User user) {
        User possiblyExistingUser = findByEmail(user.getEmail());
        if (possiblyExistingUser == null) {
            user.getBindingResult().addError(new ObjectError("user", "cannot update a role on a nonexistent user!"));
        } else {
            possiblyExistingUser.setBindingResult(user.getBindingResult());
            possiblyExistingUser.setRole(user.getRole());
            user = possiblyExistingUser;
        }
        
        return user;
    }
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        TEST_USER.setId(0L);
        TEST_USER.setBindingResult(new ModelBindingResult(TEST_USER, User.class.getName()));
        TEST_USER2.setId(1L);
        TEST_USER.setBindingResult(new ModelBindingResult(TEST_USER2, User.class.getName()));
        TEST_USER3.setId(2L);
        TEST_USER.setBindingResult(new ModelBindingResult(TEST_USER3, User.class.getName()));
        TEST_USER4.setId(3L);
        TEST_USER.setBindingResult(new ModelBindingResult(TEST_USER4, User.class.getName()));
    	
    	mockUsers = Arrays.asList(new User[] {TEST_USER, TEST_USER2, TEST_USER3, TEST_USER4});
    	
    	ReflectionTestUtils.setField(authUtility, SECRET_PROPERTY_NAME, SECRET_VALUE);
    	
    	ReflectionTestUtils.setField(jwtUtility, JWT_SECRET_KEY_PROPERTY_NAME, JWT_SECRET_KEY_VALUE);
    	    	
    	ReflectionTestUtils.setField(jwtUtility, JWT_EXPIRATION_PROPERTY_NAME, JWT_EXPIRATION_VALUE);

    	TEST_CREDENTIALS.setFirstName(TEST_USER_FIRST_NAME);
    	TEST_CREDENTIALS.setLastName(TEST_USER_LAST_NAME);
    	TEST_CREDENTIALS.setEmail(TEST_USER_EMAIL);
    	TEST_CREDENTIALS.setRole(TEST_USER_ROLE.toString());
        
        Mockito.when(userRepo.findAll()).thenReturn(mockUsers);

        Mockito.when(userRepo.create(any(String.class), any(String.class), any(String.class), any(AppRole.class))).then(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                return userRepo.save(new User((String) invocation.getArguments()[0], 
                							  (String) invocation.getArguments()[1], 
                							  (String) invocation.getArguments()[2], 
                							  (AppRole) invocation.getArguments()[3]));
            }}
        );
                
        Mockito.when(userRepo.findByEmail(any(String.class))).then(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {            	            	
                return findByEmail((String) invocation.getArguments()[0]);
            }}
        );
        
        Mockito.when(userRepo.save(any(User.class))).then(new Answer<User>() {
               @Override
               public User answer(InvocationOnMock invocation) throws Throwable {
                   return updateUser((User) invocation.getArguments()[0]);
               }}
        );
                
        Mockito.when(validationService.buildResponse(any(User.class))).then(new Answer<ApiResponse>() {
            @Override
            public ApiResponse answer(InvocationOnMock invocation) throws Throwable {
                return new ApiResponse(ApiResponseType.SUCCESS);
            }}
        );
    }

    @Test
    @Order(value = 1)
    public void testUserCredentials() {        
    	ApiResponse response = userController.credentials(TEST_CREDENTIALS);
    	
    	Credentials credentials = (Credentials) response.getPayload().get("Credentials");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	assertEquals(TEST_USER_LAST_NAME, credentials.getLastName());
        assertEquals(TEST_USER_FIRST_NAME, credentials.getFirstName());
        assertEquals(TEST_USER_EMAIL, credentials.getEmail());
        assertEquals(TEST_USER_ROLE, AppRole.valueOf(credentials.getRole()));
    }

    @Test
    @Order(value = 2)
    public void testAllUser() {
    	
    	ApiResponse response = userController.allUsers();

    	@SuppressWarnings("unchecked")
        List<User> allUsers = (List<User>) ((Map<String, Object>) response.getPayload().get("HashMap")).get("list");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(4, allUsers.size());
    	
    	for(User user : allUsers) {
    		switch(user.getEmail()) {
	    		case TEST_USER_EMAIL: {
					assertEquals(TEST_USER_FIRST_NAME, user.getFirstName());
					assertEquals(TEST_USER_LAST_NAME, user.getLastName());					 
					assertEquals(TEST_USER_ROLE, user.getRole());
				}; break;
    			case TEST_USER2_EMAIL: {
    				assertEquals(TEST_USER2.getFirstName(), user.getFirstName());
    				assertEquals(TEST_USER2.getLastName(), user.getLastName());
    				assertEquals(TEST_USER2.getRole(), user.getRole());
    			}; break;
    			case TEST_USER3_EMAIL: {
    				assertEquals(TEST_USER3.getFirstName(), user.getFirstName());
    				assertEquals(TEST_USER3.getLastName(), user.getLastName());					 
    				assertEquals(TEST_USER3.getRole(), user.getRole());
    			}; break;
    			case TEST_USER4_EMAIL: {
    				assertEquals(TEST_USER4.getFirstName(), user.getFirstName());
    				assertEquals(TEST_USER4.getLastName(), user.getLastName());
    				assertEquals(TEST_USER4.getRole(), user.getRole());
    			}; break;
    		}
    	}
    }
	 
    @Test
    @Order(value = 3)
    public void testUpdateRole() throws Exception {
        TEST_USER.setRole(TEST_USER_ROLE_UPDATE);
    	
    	ApiResponse response = userController.updateRole(TEST_USER);
    	
    	User user = (User) response.getPayload().get("User");
    	
    	assertEquals(ApiResponseType.SUCCESS, response.getMeta().getType());
    	
    	assertEquals(TEST_USER_FIRST_NAME, user.getFirstName());
    	assertEquals(TEST_USER_LAST_NAME, user.getLastName());
    	assertEquals(TEST_USER_EMAIL, user.getEmail());
    	assertEquals(TEST_USER_ROLE_UPDATE, user.getRole());
    }
	 	 
}
