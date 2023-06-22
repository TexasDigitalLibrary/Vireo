package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class VocabularyWordTest extends AbstractEntityTest {

    @BeforeEach
    public void setup() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testCreate() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        assertEquals(1, vocabularyWordRepo.count(), "VocabularyWord Repo did not save the vocab word!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_WORD, vocabularyWord.getName(), "VocabularyWord Repo did not save the correct vocab word!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_DEFINITION, vocabularyWord.getDefinition(), "VocabularyWord Repo did not save the correct vocab definition!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_IDENTIFIER, vocabularyWord.getIdentifier(), "VocabularyWord Repo did not save the correct vocab identifier!");
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testDuplication() {
        vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        });
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testDelete() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        vocabularyWordRepo.delete(vocabularyWord);
        assertEquals(0, vocabularyWordRepo.count(), "Vocabulary word did not delete!");
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testCascade() {
        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        vocabularyWordRepo.delete(vocabularyWord);
        assertEquals(0, vocabularyWordRepo.count(), "Vocabulary word did not delete!");
        assertEquals(1, controlledVocabularyRepo.count(), "The controlled vocabulary was deleted!");
    }

}
