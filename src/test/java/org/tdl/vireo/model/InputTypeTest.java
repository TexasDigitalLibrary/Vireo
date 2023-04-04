package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class InputTypeTest extends AbstractEntityTest {

    @Override
    @Test
    public void testCreate() {
        InputType inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
        assertEquals(1, inputTypeRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_FIELD_PROFILE_INPUT_TEXT_NAME, inputType.getName(), "Saved entity did not contain the value!");
    }

    @Override
    @Test
    public void testDelete() {
        InputType inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
        inputTypeRepo.delete(inputType);
        assertEquals(0, inputTypeRepo.count(), "The entity was not deleted!");
    }

    @Override
    @Test
    public void testDuplication() {
        inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
        try {
            inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
            assertTrue(false);
        } catch (Exception e) {
            // good
        }
    }

    @Override
    @Test
    public void testCascade() {
        // nothing to cascade
    }

    @AfterEach
    public void cleanUp() {
        inputTypeRepo.deleteAll();
    }

}
