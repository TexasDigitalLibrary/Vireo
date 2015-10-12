package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CustomActionDefinitionTest {
	
	private static String TEST_CUSTOM_ACTION_DEFINITION_LABEL = "Test Custom Action Definition Label";
	private static Boolean TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT = true; 
	
	@Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;
	
	@Before
    public void setUp() {
		assertEquals("The CustomActionDefinition repository is not empty!", 0, customActionDefinitionRepo.count());
		
	}
	
	@Test
    @Order(value = 1)
    @Transactional
    public void testCreate() { 
		CustomActionDefinition testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
		assertEquals("The custom action definition repository is not empty",1, customActionDefinitionRepo.count());
		assertEquals("Saved Custom Action definition does not contain correct label", TEST_CUSTOM_ACTION_DEFINITION_LABEL, testCustomActionDefinition.getLabel());
		assertEquals("Saved Custom Action definition does not contain correct studentVisibility flag",TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT,testCustomActionDefinition.getIsStudentVisible());
	}
	
	@Test
	@Order(value = 2)
	public void testDuplication() {
		customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
		try{
			customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
		}catch(DataIntegrityViolationException e) {
			/*SUCCESS*/
		}
		assertEquals("Custom Action Definition entry was duplicated",1,customActionDefinitionRepo.count());
		
	}
	
	@Test
    @Order(value = 3)
    public void testFind() {
		customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
		CustomActionDefinition customActionDefinition = customActionDefinitionRepo.findByLabel(TEST_CUSTOM_ACTION_DEFINITION_LABEL);
		assertEquals("The found custom action definition does not contain the correct label",TEST_CUSTOM_ACTION_DEFINITION_LABEL,customActionDefinition.getLabel());
	}
	
	@Test
    @Order(value = 4)
    public void testDelete() {
		CustomActionDefinition customActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
		customActionDefinitionRepo.delete(customActionDefinition);
		assertEquals("Custom Action Definition was not deleted from the repository",0,customActionDefinitionRepo.count());
	}
	
	@Test
    @Order(value = 5)
    @Transactional
    public void testCascade() {
		//TODO - what could be tested here
	}
	
	@After
    public void cleanUp() {
		customActionDefinitionRepo.deleteAll();
	}
}
