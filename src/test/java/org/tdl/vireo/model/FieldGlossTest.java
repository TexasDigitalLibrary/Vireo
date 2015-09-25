package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Language;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FieldGlossTest {
	
	static final String TEST_FIELD_GLOSS_VALUE = "Test Field Gloss";
	static final Language TEST_FIELD_GLOSS_LANGUAGE = Language.ENGLISH;
	
	@Autowired
	private FieldGlossRepo fieldGlossRepo;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {

	}
	
	@Test
	@Order(value = 1)
	public void testCreate() {
		FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE);
		fieldGloss.setLanguage(TEST_FIELD_GLOSS_LANGUAGE);
		fieldGloss = fieldGlossRepo.save(fieldGloss);
		assertEquals("The repository did not save the entity!", 1, fieldGlossRepo.count());
		assertEquals("Saved entity did not contain the value!", TEST_FIELD_GLOSS_VALUE, fieldGloss.getValue());
		assertEquals("Saved entity did not contain the language!", TEST_FIELD_GLOSS_LANGUAGE, fieldGloss.getLanguage());
	}
	
	@Test
	@Order(value = 2)
	public void testDuplication() {
		fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE);
        try {
        	fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE);
        } catch (Exception e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldGlossRepo.count());
	}
	
	@Test
	@Order(value = 3)
	public void testFind() {
		fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE);
		FieldGloss fieldGloss = fieldGlossRepo.findByValue(TEST_FIELD_GLOSS_VALUE);
		assertNotEquals("Did not find entity!", null, fieldGloss);
		assertEquals("Saved entity did not contain the value!", TEST_FIELD_GLOSS_VALUE, fieldGloss.getValue());
	}
	
	@Test
	@Order(value = 4)
	public void testDelete() {
		FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE);
		fieldGlossRepo.delete(fieldGloss);
		assertEquals("Entity did not delete!", 0, fieldGlossRepo.count());
	}
		
	@After
	public void cleanUp() {
		fieldGlossRepo.deleteAll();
	}

}

