package org.tdl.vireo.integration;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.UserRepo;

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

        userRepo.create(TEST_USER2_EMAIL, TEST_USER2.getFirstName(), TEST_USER2.getLastName(), Role.ROLE_ADMIN);
        userRepo.create(TEST_USER3_EMAIL, TEST_USER3.getFirstName(), TEST_USER3.getLastName(), Role.ROLE_MANAGER);
        userRepo.create(TEST_USER4_EMAIL, TEST_USER4.getFirstName(), TEST_USER4.getLastName(), Role.ROLE_STUDENT);

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testUserCredentials() throws Exception {
        // TODO: fix
        // mockMvc.perform(get("/user/credentials").contentType(MediaType.APPLICATION_JSON).header("jwt", jwtString)).andExpect(status().isOk()).andExpect(jsonPath("$.meta.status").value("SUCCESS")).andExpect(jsonPath("$.payload.Credentials.firstName").value("Jack")).andExpect(jsonPath("$.payload.Credentials.lastName").value("Daniels")).andExpect(jsonPath("$.payload.Credentials.netid").value("aggieJack")).andExpect(jsonPath("$.payload.Credentials.uin").value("123456789")).andExpect(jsonPath("$.payload.Credentials.email").value("aggieJack@tamu.edu")).andExpect(jsonPath("$.payload.Credentials.role").value("ROLE_ADMIN")).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAllUser() throws Exception {
        // TODO
    }

    @Test
    public void testUpdateRole() throws Exception {
        // TODO
    }

    @Override
    public void cleanup() {
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        userRepo.deleteAll();
    }

}
