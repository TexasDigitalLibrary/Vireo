package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class CustomActionDefinitionTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, customActionDefinitionRepo.count(), "The CustomActionDefinition repository is not empty!");
    }

    @Override
    @Test
    public void testCreate() {
        CustomActionDefinition testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        assertEquals(1, customActionDefinitionRepo.count(), "The custom action definition repository is not empty");
        assertEquals(TEST_CUSTOM_ACTION_DEFINITION_LABEL, testCustomActionDefinition.getLabel(), "Saved Custom Action definition does not contain correct label");
        assertEquals(TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT, testCustomActionDefinition.isStudentVisible(), "Saved Custom Action definition does not contain correct studentVisibility flag");
    }

    @Override
    @Test
    public void testDuplication() {
        customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);

        try {
            customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }

        assertEquals(1, customActionDefinitionRepo.count(), "Custom Action Definition entry was duplicated");
    }

    @Override
    @Test
    public void testDelete() {
        CustomActionDefinition customActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        customActionDefinitionRepo.delete(customActionDefinition);
        assertEquals(0, customActionDefinitionRepo.count(), "Custom Action Definition was not deleted from the repository");
    }

    @Override
    @Test
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        customActionValueRepo.deleteAll();
        customActionDefinitionRepo.deleteAll();
    }
}
