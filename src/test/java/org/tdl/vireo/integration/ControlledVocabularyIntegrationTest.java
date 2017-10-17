package org.tdl.vireo.integration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;

public class ControlledVocabularyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Override
    public void setup() {

        systemDataLoader.loadSystemDefaults();

        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME1, languageRepo.create(TEST_LANGUAGE_NAME1));
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME2, languageRepo.create(TEST_LANGUAGE_NAME2));
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME3, languageRepo.create(TEST_LANGUAGE_NAME3));

    }

    @Test
    public void testGetAllControlledVocabulary() {

    }

    @Test
    public void testGetControlledVocabularyByName() {

    }

    @Test
    public void testCreateControlledVocabulary() {

    }

    @Test
    public void testUpdateControlledVocabulary() {

    }

    @Test
    public void testRemoveControlledVocabulary() {

    }

    @Test
    public void testReorderControlledVocabulary() {

    }

    @Test
    public void testSortControlledVocabulary() {

    }

    @Test
    public void testExportControlledVocabulary() {

    }

    @Test
    public void testImportControlledVocabularyStatus() {

    }

    @Test
    public void testCancelImportControlledVocabulary() {

    }

    @Test
    public void testCompareControlledVocabulary() {

    }

    @Test
    public void testImportControlledVocabulary() {

    }

    @Test
    public void testInputStreamToRows() {

    }

    @Override
    public void cleanup() {
        controlledVocabularyRepo.findAll().forEach(cv -> {
            controlledVocabularyRepo.delete(cv);
        });
        vocabularyWordRepo.findAll().forEach(vw -> {
            vocabularyWordRepo.delete(vw);
        });
        languageRepo.deleteAll();
    }

}
