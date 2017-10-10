package org.tdl.vireo.integration;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.tamu.framework.model.Credentials;

public class UserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterRepo;

    @Override
    public void setup() {

        systemDataLoader.loadSystemDefaults();

        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });

        userRepo.deleteAll();

        userRepo.create(TEST_USER2_EMAIL, TEST_USER2.getFirstName(), TEST_USER2.getLastName(), AppRole.ADMINISTRATOR);
        userRepo.create(TEST_USER3_EMAIL, TEST_USER3.getFirstName(), TEST_USER3.getLastName(), AppRole.MANAGER);
        userRepo.create(TEST_USER4_EMAIL, TEST_USER4.getFirstName(), TEST_USER4.getLastName(), AppRole.STUDENT);

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        brokerChannelInterceptor = new MockChannelInterceptor();

        brokerChannel.addInterceptor(brokerChannelInterceptor);

        StompConnect();
    }

    @Test
    public void testUserCredentialsOverStomp() throws InterruptedException, IOException {
        String responseJson = StompRequest("/user/credentials", "");

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        @SuppressWarnings("unchecked")
        Credentials shib = new Credentials((Map<String, String>) payload.get("Credentials"));

        assertEquals("Daniels", shib.getLastName());
        assertEquals("Jack", shib.getFirstName());
        assertEquals("aggieJack", shib.getNetid());
        assertEquals("123456789", shib.getUin());
        assertEquals("aggieJack@tamu.edu", shib.getEmail());
        assertEquals("ADMINISTRATOR", shib.getRole());
    }

    @Test
    public void testUserCredentialsOverRest() throws Exception {
        mockMvc.perform(get("/user/credentials").contentType(MediaType.APPLICATION_JSON).header("jwt", jwtString)).andExpect(status().isOk()).andExpect(jsonPath("$.meta.status").value("SUCCESS")).andExpect(jsonPath("$.payload.Credentials.firstName").value("Jack")).andExpect(jsonPath("$.payload.Credentials.lastName").value("Daniels")).andExpect(jsonPath("$.payload.Credentials.netid").value("aggieJack")).andExpect(jsonPath("$.payload.Credentials.uin").value("123456789")).andExpect(jsonPath("$.payload.Credentials.email").value("aggieJack@tamu.edu")).andExpect(jsonPath("$.payload.Credentials.role").value("ADMINISTRATOR")).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAllUser() throws Exception {

        String responseJson = StompRequest("/user/all", "");

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listMap = (List<Map<String, Object>>) payload.get("ArrayList<User>");

        assertEquals(3, listMap.size());

        for (Map<String, Object> map : listMap) {
            String email = (String) map.get("email");
            switch (email) {
            case TEST_USER2_EMAIL: {
                assertEquals(TEST_USER2.getFirstName(), (String) map.get("firstName"));
                assertEquals(TEST_USER2.getLastName(), (String) map.get("lastName"));
                assertEquals(TEST_USER2.getRole().toString(), (String) map.get("role"));
            }
                ;
                break;
            case TEST_USER3_EMAIL: {
                assertEquals(TEST_USER3.getFirstName(), (String) map.get("firstName"));
                assertEquals(TEST_USER3.getLastName(), (String) map.get("lastName"));
                assertEquals(TEST_USER3.getRole().toString(), (String) map.get("role"));
            }
                ;
                break;
            case TEST_USER4_EMAIL: {
                assertEquals(TEST_USER4.getFirstName(), (String) map.get("firstName"));
                assertEquals(TEST_USER4.getLastName(), (String) map.get("lastName"));
                assertEquals(TEST_USER4.getRole().toString(), (String) map.get("role"));
            }
                ;
                break;
            }
        }
    }

    @Test
    public void testUpdateRole() throws Exception {

        User userToUpdate = userRepo.create(TEST_USER_EMAIL, TEST_USER.getFirstName(), TEST_USER.getLastName(), AppRole.STUDENT);

        userToUpdate.setRole(TEST_USER_ROLE_UPDATE);

        String responseJson = StompRequest("/user/update", userToUpdate);

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");

        assertEquals("SUCCESS", meta.get("status"));

        User testUser = userRepo.findByEmail(TEST_USER_EMAIL);

        assertEquals(TEST_USER_FIRST_NAME, testUser.getFirstName());
        assertEquals(TEST_USER_LAST_NAME, testUser.getLastName());
        assertEquals(TEST_USER_EMAIL, testUser.getEmail());
        assertEquals(TEST_USER_ROLE_UPDATE, testUser.getRole());
    }

    @Override
    public void cleanup() {
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        userRepo.deleteAll();
    }

}
