package org.tdl.vireo.integration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;

public class ControlledVocabularyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ControlledVocabularyCachingService controlledVocabularyCachingService;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    private LanguageRepo languageRepo;
    
    @Override
    public void setup() {
        
    }

    @Test
    @Order(value = 1)
    public void testGetAllControlledVocabulary() {

    }

    @Test
    @Order(value = 2)
    public void testGetControlledVocabularyByName() {

    }

    @Test
    @Order(value = 3)
    public void testCreateControlledVocabulary() {

    }

    @Test
    @Order(value = 4)
    public void testUpdateControlledVocabulary() {

    }

    @Test
    @Order(value = 5)
    public void testRemoveControlledVocabulary() {

    }

    @Test
    @Order(value = 6)
    public void testReorderControlledVocabulary() {

    }

    @Test
    @Order(value = 7)
    public void testSortControlledVocabulary() {

    }

    @Test
    @Order(value = 8)
    public void testExportControlledVocabulary() {

    }

    @Test
    @Order(value = 9)
    public void testImportControlledVocabularyStatus() {

    }

    @Test
    @Order(value = 10)
    public void testCancelImportControlledVocabulary() {

    }

    @Test
    @Order(value = 11)
    public void testCompareControlledVocabulary() {

    }

    @Test
    @Order(value = 12)
    public void testImportControlledVocabulary() {

    }

    @Test
    @Order(value = 13)
    public void testInputStreamToRows() {

    }

    @Override
    public void cleanup() {

    }

}
