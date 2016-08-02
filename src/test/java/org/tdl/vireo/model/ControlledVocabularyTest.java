package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.enums.EmbargoGuarantor;

public class ControlledVocabularyTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        language = languageRepo.create(TEST_LANGUAGE);
    }

    @Override
    public void testCreate() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        assertEquals("The repository did not save the entity!", 1, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_NAME, controlledVocabulary.getName());
        assertEquals("Saved entity did not contain the language!", language, controlledVocabulary.getLanguage());
        
        // add some embargos to test create entity controlled vocabulary
        embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        Embargo embargo = embargoRepo.create(TEST_EMBARGO_NAME_2, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        embargo.setGuarantor(EmbargoGuarantor.PROQUEST);
        embargoRepo.save(embargo);
        
        ControlledVocabulary entityControlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO_GUARANTOR, TEST_CONTROLLED_VOCABULARY_EMBARGO, language);
        assertEquals("The repository did not save the entity!", 2, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_EMBARGO_GUARANTOR, entityControlledVocabulary.getName());
        assertEquals("Saved entity did not contain the entity name!", TEST_CONTROLLED_VOCABULARY_EMBARGO, entityControlledVocabulary.getEntityName());
        assertEquals("Saved entity did not contain the language!", language, entityControlledVocabulary.getLanguage());
        assertEquals("Saved entity did not contain the is entity!", true, entityControlledVocabulary.getIsEntityProperty());
        
        List<VocabularyWord> guarantors = null;
        try {
            guarantors = entityControlledVocabularyService.getControlledVocabulary(TEST_CONTROLLED_VOCABULARY_EMBARGO, TEST_CONTROLLED_VOCABULARY_EMBARGO_GUARANTOR);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assertEquals("Number of guarantors does not match!", guarantors.size(), entityControlledVocabulary.getDictionary().size());
        
        List<VocabularyWord> entityControlledVocabularyValues = entityControlledVocabulary.getDictionary();
        
        for(VocabularyWord gaurantor : guarantors) {           
            assertEquals("Guarantors does not contain entityControlledVocabulary value!", true, entityControlledVocabularyValues.contains(gaurantor));
        };
        
    }

    @Override
    public void testDuplication() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        try {
            controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        
        assertEquals("The repository duplicated entity!", 1, controlledVocabularyRepo.count());
        
        vocabularyWord = vocabularyWordRepo.create(TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        
        controlledVocabulary.addValue(vocabularyWord);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        
        try {
            controlledVocabulary.addValue(vocabularyWord);
            controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        
        assertEquals("Values duplicated on a controlled vocabulary!", 1, controlledVocabulary.getDictionary().size());
    }

    @Override
    public void testDelete() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals("The entity was not deleted!", 0, controlledVocabularyRepo.count());
    }

    @Override
    public void testCascade() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        
        vocabularyWord = vocabularyWordRepo.create(TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);
        
        controlledVocabulary.addValue(vocabularyWord);
        
        VocabularyWord severableVocabularyWord = vocabularyWordRepo.create(TEST_SEVERABLE_CONTROLLED_VOCABULARY_WORD, TEST_SEVERABLE_CONTROLLED_VOCABULARY_DEFINITION, TEST_SEVERABLE_CONTROLLED_VOCABULARY_IDENTIFIER);
                
        controlledVocabulary.addValue(severableVocabularyWord);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("Saved entity did not have the correct number of values!", 2, controlledVocabulary.getDictionary().size());

        // test remove value
        controlledVocabulary.removeValue(severableVocabularyWord);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("Did not remove value!", 1, controlledVocabulary.getDictionary().size());

        // test delete controlled vocabulary
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals("The entity was not deleted!", 0, controlledVocabularyRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
    }

    @After
    public void cleanUp() {
        controlledVocabularyRepo.deleteAll();
        vocabularyWordRepo.deleteAll();
        languageRepo.deleteAll();
        embargoRepo.deleteAll();
    }

}
