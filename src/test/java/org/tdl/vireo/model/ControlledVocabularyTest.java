package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

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
    @SuppressWarnings("unchecked")    
    public void testCreate() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        assertEquals("The repository did not save the entity!", 1, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_NAME, controlledVocabulary.getName());
        assertEquals("Saved entity did not contain the language!", language, controlledVocabulary.getLanguage());
        
        
        
        
        embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION);
        Embargo embargo = embargoRepo.create(TEST_EMBARGO_NAME_2, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION);
        embargo.setGuarantor(EmbargoGuarantor.PROQUEST);
        embargoRepo.save(embargo);
        
        
        ControlledVocabulary entityControlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO_GUARANTOR, TEST_CONTROLLED_VOCABULARY_EMBARGO, language);
        assertEquals("The repository did not save the entity!", 2, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_EMBARGO_GUARANTOR, entityControlledVocabulary.getName());
        assertEquals("Saved entity did not contain the entity name!", TEST_CONTROLLED_VOCABULARY_EMBARGO, entityControlledVocabulary.getEntityName());
        assertEquals("Saved entity did not contain the language!", language, entityControlledVocabulary.getLanguage());
        assertEquals("Saved entity did not contain the is entity!", true, entityControlledVocabulary.isEntityProperty());
        
        List<EmbargoGuarantor> guarantors = null;
        try {
            guarantors = (List<EmbargoGuarantor>) entityControlledVocabularyService.getControlledVocabulary(TEST_CONTROLLED_VOCABULARY_EMBARGO, TEST_CONTROLLED_VOCABULARY_EMBARGO_GUARANTOR);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assertEquals("Number of guarantors does not match!", guarantors.size(), entityControlledVocabulary.getValues().size());
        
        Set<String> controlledVocabularyValues = entityControlledVocabulary.getValues();
        
        for(EmbargoGuarantor gaurantor : guarantors) {           
            assertEquals("Guarantors does not contain entityControlledVocabulary value!", true, controlledVocabularyValues.contains(gaurantor.name()));
        };
        
    }

    @Override
    public void testDuplication() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        try {
            controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        
        assertEquals("The repository duplicated entity!", 1, controlledVocabularyRepo.count());
        controlledVocabulary.addValue(TEST_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        
        try {
            controlledVocabulary.addValue(TEST_CONTROLLED_VOCABULARY_VALUE);
            controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        
        assertEquals("Values duplicated on a controlled vocabulary!", 1, controlledVocabulary.getValues().size());
    }

    @Override
    public void testDelete() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals("The entity was not deleted!", 0, controlledVocabularyRepo.count());
    }

    @Override
    public void testCascade() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        controlledVocabulary.addValue(TEST_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary.addValue(TEST_SEVERABLE_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("Saved entity did not have the correct number of values!", 2, controlledVocabulary.getValues().size());

        // test detach value
        controlledVocabulary.removeValue(TEST_SEVERABLE_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("Did not detach value!", 1, controlledVocabulary.getValues().size());

        // test delete controlled vocabulary
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals("The entity was not deleted!", 0, controlledVocabularyRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
    }

    @After
    public void cleanUp() {
        controlledVocabularyRepo.deleteAll();
        languageRepo.deleteAll();
        embargoRepo.deleteAll();
    }

}
