package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class ControlledVocabularyTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        language = languageRepo.create(TEST_LANGUAGE);
    }

    @Override
    public void testCreate() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        assertEquals("The repository did not save the entity!", 1, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_NAME, controlledVocabulary.getName());
        assertEquals("Saved entity did not contain the language!", language, controlledVocabulary.getLanguage());
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
    }

}
