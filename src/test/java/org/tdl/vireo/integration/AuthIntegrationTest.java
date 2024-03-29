package org.tdl.vireo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.weaver.auth.service.CryptoService;

public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterRepo;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private CryptoService cryptoService;

    @BeforeEach
    public void setup() {

        assertEquals(0, userRepo.count());
        assertEquals(0, namedSearchFilterRepo.count());
        assertEquals(0, emailTemplateRepo.count());

        userRepo.create(TEST_USER2_EMAIL, TEST_USER2.getFirstName(), TEST_USER2.getLastName(), Role.ROLE_ADMIN);
        userRepo.create(TEST_USER3_EMAIL, TEST_USER3.getFirstName(), TEST_USER3.getLastName(), Role.ROLE_MANAGER);
        userRepo.create(TEST_USER4_EMAIL, TEST_USER4.getFirstName(), TEST_USER4.getLastName(), Role.ROLE_STUDENT);

        assertEquals(3, userRepo.count());
        assertEquals(2, namedSearchFilterRepo.count());

        emailTemplateRepo.create(TEST_REGISTRATION_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);

        assertEquals(1, emailTemplateRepo.count());

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testRegisterEmail() throws Exception {
        // TODO
    }

    @Test
    public void testRegister() throws Exception {
        String token = cryptoService.generateGenericToken(TEST_USER_EMAIL, EMAIL_VERIFICATION_TYPE);
        Map<String, String> data = new HashMap<String, String>();
        data.put("token", token);
        data.put("email", TEST_USER_EMAIL);
        data.put("firstName", TEST_USER_FIRST_NAME);
        data.put("lastName", TEST_USER_LAST_NAME);
        data.put("userPassword", TEST_USER_PASSWORD);
        data.put("confirm", TEST_USER_CONFIRM);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.convertValue(data, JsonNode.class).toString().getBytes("utf-8"))
            )
            .andExpect(status().isOk()).andExpect(jsonPath("$.meta.status").value("SUCCESS"))
            .andExpect(jsonPath("$.payload.User.email").value(TEST_USER_EMAIL))
            .andExpect(jsonPath("$.payload.User.firstName").value(TEST_USER_FIRST_NAME))
            .andExpect(jsonPath("$.payload.User.lastName").value(TEST_USER_LAST_NAME))
            .andExpect(jsonPath("$.payload.User.password").doesNotExist());
    }

    @Test
    public void testLogin() throws Exception {

        testRegister();

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", TEST_USER_EMAIL);
        data.put("userPassword", TEST_USER_PASSWORD);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.convertValue(data, JsonNode.class).toString().getBytes("utf-8"))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.status").value("SUCCESS"));
    }

}
