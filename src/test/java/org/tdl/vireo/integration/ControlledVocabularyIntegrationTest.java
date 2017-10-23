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
        // TODO
    }

    @Test
    public void testGetControlledVocabularyByName() {
        // TODO
    }

    @Test
    public void testCreateControlledVocabulary() {
        // TODO
    }

    @Test
    public void testUpdateControlledVocabulary() {
        // TODO
    }

    @Test
    public void testRemoveControlledVocabulary() {
        // TODO
    }

    @Test
    public void testReorderControlledVocabulary() {
        // TODO
    }

    @Test
    public void testSortControlledVocabulary() {
        // TODO
    }

    @Test
    public void testExportControlledVocabulary() {
        // TODO
    }

    @Test
    public void testImportControlledVocabularyStatus() {
        // TODO
    }

    @Test
    public void testCancelImportControlledVocabulary() {
        // TODO
    }

    @Test
    public void testCompareControlledVocabulary() {
        // TODO
    }

    @Test
    public void testImportControlledVocabulary() {
        // TODO
    }

    @Test
    public void testInputStreamToRows() {
        // TODO
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
