package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;

public class VocabularyWordTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("VocabularyWord repo was not empty!", 0, vocabularyWordRepo.count());
    }

    @Override
    public void testCreate() {
        VocabularyWord testVocabularyWord = vocabularyWordRepo.create(TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        assertEquals("VocabularyWord Repo did not save the vocab word!", 1, vocabularyWordRepo.count());
        assertEquals("VocabularyWord Repo did not save the correct vocab word!", TEST_CONTROLLED_VOCABULARY_WORD, testVocabularyWord.getName());
        assertEquals("VocabularyWord Repo did not save the correct vocab definition!", TEST_CONTROLLED_VOCABULARY_DEFINITION, testVocabularyWord.getDefinition());
        assertEquals("VocabularyWord Repo did not save the correct vocab identifier!", TEST_CONTROLLED_VOCABULARY_IDENTIFIER, testVocabularyWord.getIdentifier());
    }
    
    @Override
    public void testDuplication() {
        vocabularyWordRepo.create(TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        vocabularyWordRepo.create(TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER); 
        assertEquals("The repository did persist duplicate vocabulary words!", 2, vocabularyWordRepo.count());         
    }

    @Override
    public void testDelete() {
        VocabularyWord testVocabularyWord = vocabularyWordRepo.create(TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER); 
        vocabularyWordRepo.delete(testVocabularyWord);
        assertEquals("VocabularyWord did not delete!", 0, vocabularyWordRepo.count());
    }

    @Override
    public void testCascade() {
        language = languageRepo.create(TEST_LANGUAGE);
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        
        vocabularyWord = vocabularyWordRepo.create(TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        
        controlledVocabulary.addValue(vocabularyWord);
        
        vocabularyWordRepo.delete(vocabularyWord);
        
        assertEquals("The controlled vocabulary was deleted!", 1, controlledVocabularyRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
    }

    @After
    public void cleanUp() {
        controlledVocabularyRepo.deleteAll();
        vocabularyWordRepo.deleteAll();
    }
    
}