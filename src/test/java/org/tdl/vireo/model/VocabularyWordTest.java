package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.dao.DataIntegrityViolationException;

public class VocabularyWordTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, vocabularyWordRepo.count(), "VocabularyWord repo was not empty!");
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
    }

    @Override
    public void testCreate() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        assertEquals(1, vocabularyWordRepo.count(), "VocabularyWord Repo did not save the vocab word!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_WORD, vocabularyWord.getName(), "VocabularyWord Repo did not save the correct vocab word!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_DEFINITION, vocabularyWord.getDefinition(), "VocabularyWord Repo did not save the correct vocab definition!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_IDENTIFIER, vocabularyWord.getIdentifier(), "VocabularyWord Repo did not save the correct vocab identifier!");
    }

    @Override
    public void testDuplication() {
        vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        try {
            vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals(1, vocabularyWordRepo.count(), "The repository persisted duplicate vocabulary words with the same name and controlled vocabulary!");
    }

    @Override
    public void testDelete() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        vocabularyWordRepo.delete(vocabularyWord);
        assertEquals(0, vocabularyWordRepo.count(), "Vocabulary word did not delete!");
    }

    @Override
    public void testCascade() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        vocabularyWordRepo.delete(vocabularyWord);
        assertEquals(0, vocabularyWordRepo.count(), "Vocabulary word did not delete!");
        assertEquals(1, controlledVocabularyRepo.count(), "The controlled vocabulary was deleted!");
    }

    @AfterEach
    public void cleanUp() {
        controlledVocabularyRepo.findAll().forEach(cv -> {
            controlledVocabularyRepo.delete(cv);
        });
        vocabularyWordRepo.findAll().forEach(vw -> {
            vocabularyWordRepo.delete(vw);
        });
    }

}
