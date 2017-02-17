package org.tdl.vireo.service;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.ControlledVocabularyCache;
import org.tdl.vireo.runner.OrderedRunner;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles({"test"})
public class ControlledVocabularyCachingServiceTest {

    @Value("${app.cvcache.duration}")
    private Long duration;

    private final static String TEST_CONTROLLED_VOCABULARY_NAME = "Test";

    private Long TEST_TIME_STAMP;

    @Autowired
    private ControlledVocabularyCachingService controlledVocabularyCachingService;

    @Before
    public void setup() {
        TEST_TIME_STAMP = new Date().getTime();
    }

    @Test
    @Order(value = 1)
    public void testAddControlledVocabularyCache() {
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals(true, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
    }

    @Test
    @Order(value = 2)
    public void testRemoveControlledVocabularyCache() {
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        controlledVocabularyCachingService.removeControlledVocabularyCache(TEST_CONTROLLED_VOCABULARY_NAME);
        assertNull(controlledVocabularyCachingService.getControlledVocabularyCache(TEST_CONTROLLED_VOCABULARY_NAME));
    }

    @Test
    @Order(value = 3)
    public void testGetControlledVocabularyCache() {
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        ControlledVocabularyCache newControlledVocabularyCache = controlledVocabularyCachingService.getControlledVocabularyCache(TEST_CONTROLLED_VOCABULARY_NAME);
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME, newControlledVocabularyCache.getControlledVocabularyName());
        assertEquals(TEST_TIME_STAMP, newControlledVocabularyCache.getTimestamp());
    }

    @Test
    @Order(value = 4)
    public void testDoesControlledVocabularyExist() {
        assertEquals(false, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals(true, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
    }

    @Test
    @Order(value = 5)
    public void testCleanCache() {
        TEST_TIME_STAMP = new Date().getTime() + duration;
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals(true, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
        controlledVocabularyCachingService.cleanCache();
        assertEquals(false, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));

        TEST_TIME_STAMP = new Date().getTime() - (duration * 2);
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals(true, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
        controlledVocabularyCachingService.cleanCache();
        assertEquals(true, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
    }

    @After
    public void cleanup() {
        controlledVocabularyCachingService.clearCache();
    }

}
