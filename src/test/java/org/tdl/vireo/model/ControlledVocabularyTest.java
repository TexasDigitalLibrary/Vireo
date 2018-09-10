package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class ControlledVocabularyTest extends AbstractEntityTest {

    @Override
    public void testCreate() throws ClassNotFoundException {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        assertEquals("The repository did not save the entity!", 1, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_NAME, controlledVocabulary.getName());

        // add some embargos to test create entity controlled vocabulary
        embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        Embargo embargo = embargoRepo.create(TEST_EMBARGO_NAME_2, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        embargo.setGuarantor(EmbargoGuarantor.PROQUEST);
        embargoRepo.save(embargo);

        ControlledVocabulary entityControlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO, true);
        assertEquals("The repository did not save the entity!", 2, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_EMBARGO, entityControlledVocabulary.getName());
        assertEquals("Saved entity did not contain the is entity!", true, entityControlledVocabulary.getIsEntityProperty());

        List<VocabularyWord> guarantors = entityControlledVocabularyService.getControlledVocabularyWords(TEST_CONTROLLED_VOCABULARY_EMBARGO);

        assertEquals("Number of guarantors does not match!", guarantors.size(), entityControlledVocabulary.getDictionary().size());

        List<VocabularyWord> entityControlledVocabularyValues = entityControlledVocabulary.getDictionary();

        for (VocabularyWord gaurantor : guarantors) {
            assertEquals("Guarantors does not contain entityControlledVocabulary value!", true, entityControlledVocabularyValues.contains(gaurantor));
        }

    }

    @Override
    public void testDuplication() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        try {
            controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }

        assertEquals("The repository duplicated entity!", 1, controlledVocabularyRepo.count());

        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);

        assertEquals("Values duplicated on a controlled vocabulary!", 1, controlledVocabulary.getDictionary().size());
    }

    @Override
    public void testDelete() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals("The entity was not deleted!", 0, controlledVocabularyRepo.count());
    }

    @Override
    public void testCascade() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);

        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);

        controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME);

        VocabularyWord severableVocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_SEVERABLE_CONTROLLED_VOCABULARY_WORD, TEST_SEVERABLE_CONTROLLED_VOCABULARY_DEFINITION, TEST_SEVERABLE_CONTROLLED_VOCABULARY_IDENTIFIER);

        controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME);

        assertEquals("Saved entity did not have the correct number of values!", 2, controlledVocabulary.getDictionary().size());

        // test remove value
        controlledVocabulary.removeValue(severableVocabularyWord);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("Did not remove value!", 1, controlledVocabulary.getDictionary().size());

        // test delete controlled vocabulary
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals("Vocabulary word was orphaned!", 0, vocabularyWordRepo.count());
        assertEquals("The entity was not deleted!", 0, controlledVocabularyRepo.count());
    }

    @After
    public void cleanUp() {
        controlledVocabularyRepo.findAll().forEach(cv -> {
            controlledVocabularyRepo.delete(cv);
        });
        vocabularyWordRepo.findAll().forEach(vw -> {
            vocabularyWordRepo.delete(vw);
        });
        embargoRepo.deleteAll();
    }

}
