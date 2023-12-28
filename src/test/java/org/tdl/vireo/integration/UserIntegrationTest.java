package org.tdl.vireo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.UserRepo;

public class UserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterRepo;

    @BeforeEach
    public void setup() {
        assertEquals(0, userRepo.count());

        assertEquals(0, namedSearchFilterRepo.count());

        userRepo.create(TEST_USER2_EMAIL, TEST_USER2.getFirstName(), TEST_USER2.getLastName(), Role.ROLE_ADMIN);
        userRepo.create(TEST_USER3_EMAIL, TEST_USER3.getFirstName(), TEST_USER3.getLastName(), Role.ROLE_MANAGER);
        userRepo.create(TEST_USER4_EMAIL, TEST_USER4.getFirstName(), TEST_USER4.getLastName(), Role.ROLE_STUDENT);

        assertEquals(3, userRepo.count());

        assertEquals(2, namedSearchFilterRepo.count());

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testUserCredentials() throws Exception {
        // TODO
    }

    @Test
    public void testAllUser() throws Exception {
        // TODO
    }

    @Test
    public void testUpdateRole() throws Exception {
        // TODO
    }

}
