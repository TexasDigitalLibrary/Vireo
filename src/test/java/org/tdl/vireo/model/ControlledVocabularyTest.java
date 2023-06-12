package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ControlledVocabularyTest extends AbstractEntityTest {

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testCreate() throws ClassNotFoundException {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        assertEquals(1, controlledVocabularyRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME, controlledVocabulary.getName(), "Saved entity did not contain the name!");

        // add some embargos to test create entity controlled vocabulary
        embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        Embargo embargo = embargoRepo.create(TEST_EMBARGO_NAME_2, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        embargo.setGuarantor(EmbargoGuarantor.PROQUEST);
        embargoRepo.save(embargo);

        ControlledVocabulary entityControlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO, true);
        assertEquals(2, controlledVocabularyRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_CONTROLLED_VOCABULARY_EMBARGO, entityControlledVocabulary.getName(), "Saved entity did not contain the name!");
        assertEquals(true, entityControlledVocabulary.getIsEntityProperty(), "Saved entity did not contain the is entity!");

        List<VocabularyWord> guarantors = entityControlledVocabularyService.getControlledVocabularyWords(TEST_CONTROLLED_VOCABULARY_EMBARGO);

        assertEquals(guarantors.size(), entityControlledVocabulary.getDictionary().size(), "Number of guarantors does not match!");

        List<VocabularyWord> entityControlledVocabularyValues = entityControlledVocabulary.getDictionary();

        for (VocabularyWord gaurantor : guarantors) {
            assertEquals(true, entityControlledVocabularyValues.contains(gaurantor), "Guarantors does not contain entityControlledVocabulary value!");
        }

    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testDuplication() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        try {
            controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }

        assertEquals(1, controlledVocabularyRepo.count(), "The repository duplicated entity!");

        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);

        assertEquals(1, controlledVocabulary.getDictionary().size(), "Values duplicated on a controlled vocabulary!");
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testDelete() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals(0, controlledVocabularyRepo.count(), "The entity was not deleted!");
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testCascade() {
        controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);

        vocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_CONTROLLED_VOCABULARY_WORD, TEST_CONTROLLED_VOCABULARY_DEFINITION, TEST_CONTROLLED_VOCABULARY_IDENTIFIER);

        controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME);

        VocabularyWord severableVocabularyWord = vocabularyWordRepo.create(controlledVocabulary, TEST_SEVERABLE_CONTROLLED_VOCABULARY_WORD, TEST_SEVERABLE_CONTROLLED_VOCABULARY_DEFINITION, TEST_SEVERABLE_CONTROLLED_VOCABULARY_IDENTIFIER);

        controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME);

        assertEquals(2, controlledVocabulary.getDictionary().size(), "Saved entity did not have the correct number of values!");

        // test remove value
        controlledVocabulary.removeValue(severableVocabularyWord);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals(1, controlledVocabulary.getDictionary().size(), "Did not remove value!");

        // test delete controlled vocabulary
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals(0, vocabularyWordRepo.count(), "Vocabulary word was orphaned!");
        assertEquals(0, controlledVocabularyRepo.count(), "The entity was not deleted!");
    }

}
