package org.tdl.vireo.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles({"test"})
public class EntityCVServiceTest {

    @Before
    public void setup() {

    }

    @Test
    @Order(value = 1)
    public void testAddEntityToWhitelist() {

    }

    @Test
    @Order(value = 2)
    public void testRemoveEntityFromWhitelist() {

    }

    @Test
    @Order(value = 3)
    public void testAddEntityPropertyToWhitelist() {

    }

    @Test
    @Order(value = 4)
    public void testRemoveEntityPropertyFromWhitelist() {

    }

    @Test
    @Order(value = 5)
    public void testGetControlledVocabulary() {

    }

    @Test
    @Order(value = 6)
    public void testGetEntityNames() {

    }

    @Test
    @Order(value = 7)
    public void testGetAllEntityPropertyNames() {

    }

    @Test
    @Order(value = 8)
    public void testGetPropertyNames() {

    }

    @Test
    @Order(value = 9)
    public void testGetWhitelist() {

    }

    @After
    public void cleanup() {

    }

}
