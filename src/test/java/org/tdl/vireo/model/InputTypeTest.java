package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;

public class InputTypeTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        InputType inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
        assertEquals("The repository did not save the entity!", 1, inputTypeRepo.count());
        assertEquals("Saved entity did not contain the value!", TEST_FIELD_PROFILE_INPUT_TEXT_NAME, inputType.getName());
    }

    @Override
    public void testDelete() {
        InputType inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
        inputTypeRepo.delete(inputType);
        assertEquals("The entity was not deleted!", 0, inputTypeRepo.count());
    }

    @Override
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
    public void testCascade() {
        // nothing to cascade
    }

    @After
    public void cleanUp() {
        inputTypeRepo.deleteAll();
    }

}
