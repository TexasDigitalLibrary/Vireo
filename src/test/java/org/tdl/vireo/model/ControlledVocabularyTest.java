package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ControlledVocabularyTest {

    static final String TEST_CONTROLLED_VOCABULARY_NAME = "Test Vocabulary";

    static final String TEST_CONTROLLED_VOCABULARY_VALUE = "Test Vocabulary Value";
    static final String TEST_DETACHABLE_CONTROLLED_VOCABULARY_VALUE = "Test Detachable Vocabulary Value";

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Test
    @Order(value = 1)
    public void testCreate() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        controlledVocabulary.addValue(TEST_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("The repository did not save the entity!", 1, controlledVocabularyRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_CONTROLLED_VOCABULARY_NAME, controlledVocabulary.getName());
        assertEquals("Saved entity did not have the correct number of values!", 1, controlledVocabulary.getValues().size());
        assertEquals("Saved entity did not contain the correct value!", TEST_CONTROLLED_VOCABULARY_VALUE, controlledVocabulary.getValueByValue(TEST_CONTROLLED_VOCABULARY_VALUE));
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        try {
            controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        } catch (Exception e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, controlledVocabularyRepo.count());
        controlledVocabulary.addValue(TEST_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        try {
            controlledVocabulary.addValue(TEST_CONTROLLED_VOCABULARY_VALUE);
            controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        } catch (Exception e) {
            /* SUCCESS */ }
        assertEquals("Values duplicated on a controlled vocabulary!", 1, controlledVocabulary.getValues().size());
    }

    @Test
    @Order(value = 3)
    public void testFind() {
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME);
        assertEquals("Entity was not found!", TEST_CONTROLLED_VOCABULARY_NAME, controlledVocabulary.getName());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        controlledVocabularyRepo.delete(controlledVocabulary);
        assertEquals("The entity was not deleted!", 0, controlledVocabularyRepo.count());
    }

    @Test
    @Order(value = 5)
    public void testDetachValue() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        controlledVocabulary.addValue(TEST_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary.addValue(TEST_DETACHABLE_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("Saved entity did not have the correct number of values!", 2, controlledVocabulary.getValues().size());
        controlledVocabulary.removeValue(TEST_DETACHABLE_CONTROLLED_VOCABULARY_VALUE);
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        assertEquals("Did not detach value!", 1, controlledVocabulary.getValues().size());
    }

    @After
    public void cleanUp() {
        controlledVocabularyRepo.deleteAll();
    }

}
