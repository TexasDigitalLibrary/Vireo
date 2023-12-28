package org.tdl.vireo.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.Application;

@ActiveProfiles(value = { "test", "isolated-test" })
@SpringBootTest(classes = { Application.class })
public class OrderedEntityServiceTest {

    @BeforeEach
    public void setup() {

    }

    @Test
    public void testFindByOrder() {

    }

    @Test
    public void testSwap() {

    }

    @Test
    public void testDelete() {

    }

    @Test
    public void testReorder() {

    }

    @Test
    public void testRemove() {

    }

    @AfterEach
    public void cleanup() {

    }

}
