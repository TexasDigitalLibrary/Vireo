package org.tdl.vireo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.util.AuthUtility;

public class AuthIntegrationTest extends AbstractIntegrationTest {

	@Autowired
    private UserRepo userRepo;
	
	@Autowired
    private EmailTemplateRepo emailTemplateRepo;
		
    @Autowired
    private AuthUtility authUtility;
    
    @Override
    public void setup() {
        userRepo.deleteAll();
        
    	userRepo.create(TEST_USER2_EMAIL, TEST_USER2.getFirstName(), TEST_USER2.getLastName(), AppRole.ADMINISTRATOR);
    	userRepo.create(TEST_USER3_EMAIL, TEST_USER3.getFirstName(), TEST_USER3.getLastName(), AppRole.MANAGER);
    	userRepo.create(TEST_USER4_EMAIL, TEST_USER4.getFirstName(), TEST_USER4.getLastName(), AppRole.STUDENT);
    	
    	emailTemplateRepo.create(TEST_REGISTRATION_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                        
        brokerChannelInterceptor = new MockChannelInterceptor();
        
        brokerChannel.addInterceptor(brokerChannelInterceptor);
        
        StompConnect();
    }

    @Test
    @Order(value = 1)
    public void testRegisterEmail() throws Exception {    	    	
        mockMvc.perform(get("/auth/register")
        					.param("email", TEST_USER_EMAIL)
        					.contentType(MediaType.APPLICATION_JSON))           
           .andExpect(status().isOk())           
           .andExpect(jsonPath("$.meta.type").value("SUCCESS"))
           .andExpect(jsonPath("$.payload.UnmodifiableMap.email").value(TEST_USER_EMAIL))
           .andDo(MockMvcResultHandlers.print());
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
        mockMvc.perform(get("/auth/register")
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
    @Order(value = 3)
    public void testLogin() throws Exception {

    	testRegister();
    	
    	Map<String, String> data = new HashMap<String, String>();
    	data.put("email", TEST_USER_EMAIL);
    	data.put("password", TEST_USER_PASSWORD);
        mockMvc.perform(get("/auth/login")
        					.contentType(MediaType.APPLICATION_JSON)
        					.header("data", objectMapper.convertValue(data, JsonNode.class)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.meta.type").value("SUCCESS"))
           .andDo(MockMvcResultHandlers.print());
    }
    
    @Override
    public void cleanup() {
        userRepo.deleteAll();
        emailTemplateRepo.deleteAll();
    }
	 
}
