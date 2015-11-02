package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.runner.OrderedRunner;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CustomActionDefinitionTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The CustomActionDefinition repository is not empty!", 0, customActionDefinitionRepo.count());
    }

    @Override
    public void testCreate() {
        CustomActionDefinition testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        assertEquals("The custom action definition repository is not empty", 1, customActionDefinitionRepo.count());
        assertEquals("Saved Custom Action definition does not contain correct label", TEST_CUSTOM_ACTION_DEFINITION_LABEL, testCustomActionDefinition.getLabel());
        assertEquals("Saved Custom Action definition does not contain correct studentVisibility flag", TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT, testCustomActionDefinition.isStudentVisible());
    }

    @Override
    public void testDuplication() {
        customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        
        try {
            customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        
        assertEquals("Custom Action Definition entry was duplicated", 1, customActionDefinitionRepo.count());
    }

    @Override
    public void testDelete() {
        CustomActionDefinition customActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
        customActionDefinitionRepo.delete(customActionDefinition);
        assertEquals("Custom Action Definition was not deleted from the repository", 0, customActionDefinitionRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        customActionDefinitionRepo.deleteAll();
    }
}
