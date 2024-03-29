package org.tdl.vireo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.ControlledVocabularyCache;

@ActiveProfiles(value = { "test", "isolated-test" })
@SpringBootTest(classes = { Application.class })
@ContextConfiguration
public class ControlledVocabularyCachingServiceTest {

    @Value("${app.cvcache.duration}")
    private Long duration;

    private final static String TEST_CONTROLLED_VOCABULARY_NAME = "Test";

    private Long TEST_TIME_STAMP;

    @Autowired
    private ControlledVocabularyCachingService controlledVocabularyCachingService;

    @BeforeEach
    public void setup() {
        TEST_TIME_STAMP = new Date().getTime();
    }

    @Test
    public void testAddControlledVocabularyCache() {
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals(true, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
    }

    @Test
    public void testRemoveControlledVocabularyCache() {
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        controlledVocabularyCachingService.removeControlledVocabularyCache(TEST_CONTROLLED_VOCABULARY_NAME);
        assertNull(controlledVocabularyCachingService.getControlledVocabularyCache(TEST_CONTROLLED_VOCABULARY_NAME));
    }

    @Test
    public void testGetControlledVocabularyCache() {
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        ControlledVocabularyCache newControlledVocabularyCache = controlledVocabularyCachingService.getControlledVocabularyCache(TEST_CONTROLLED_VOCABULARY_NAME);
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME, newControlledVocabularyCache.getControlledVocabularyName());
        assertEquals(TEST_TIME_STAMP, newControlledVocabularyCache.getTimestamp());
    }

    @Test
    public void testDoesControlledVocabularyExist() {
        assertEquals(false, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
        controlledVocabularyCachingService.addControlledVocabularyCache(new ControlledVocabularyCache(TEST_TIME_STAMP, TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals(true, controlledVocabularyCachingService.doesControlledVocabularyExist(TEST_CONTROLLED_VOCABULARY_NAME));
    }

    @Test
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

    @AfterEach
    public void cleanup() {
        controlledVocabularyCachingService.clearCache();
    }

}
