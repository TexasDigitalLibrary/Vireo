package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class VocabularyWordTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("VocabularyWord repo was not empty!", 0, vocabularyWordRepo.count());
        language = languageRepo.create(TEST_LANGUAGE);
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
    }

    @Override
    public void testCreate() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        assertEquals("VocabularyWord Repo did not save the vocab word!", 1, vocabularyWordRepo.count());
        assertEquals("VocabularyWord Repo did not save the correct vocab word!", TEST_CONTROLLED_VOCABULARY_WORD, vocabularyWord.getName());
        assertEquals("VocabularyWord Repo did not save the correct vocab definition!", TEST_CONTROLLED_VOCABULARY_DEFINITION, vocabularyWord.getDefinition());
        assertEquals("VocabularyWord Repo did not save the correct vocab identifier!", TEST_CONTROLLED_VOCABULARY_IDENTIFIER, vocabularyWord.getIdentifier());
    }

    @Override
    public void testDuplication() {
        vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        try {
            vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository persisted duplicate vocabulary words with the same name and controlled vocabulary!", 1, vocabularyWordRepo.count());
    }

    @Override
    public void testDelete() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        vocabularyWordRepo.delete(vocabularyWord);
        assertEquals("Vocabulary word did not delete!", 0, vocabularyWordRepo.count());
    }

    @Override
    public void testCascade() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        vocabularyWordRepo.delete(vocabularyWord);
        assertEquals("Vocabulary word did not delete!", 0, vocabularyWordRepo.count());
        assertEquals("The controlled vocabulary was deleted!", 1, controlledVocabularyRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
    }

    @After
    public void cleanUp() {
        controlledVocabularyRepo.findAll().forEach(cv -> {
            controlledVocabularyRepo.delete(cv);
        });
        vocabularyWordRepo.findAll().forEach(vw -> {
            vocabularyWordRepo.delete(vw);
        });
        languageRepo.deleteAll();
    }

}
