package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class LanguageTest extends AbstractEntityTest {

	private static final String TEST_LANGUAGE_NAME = "Test Language";

	@Autowired
	private LanguageRepo languageRepo;

	@Override
	public void testCreate() {
		Language language = languageRepo.create(TEST_LANGUAGE_NAME);
		assertEquals("The entity was not created!", 1, languageRepo.count());
		assertEquals("The entity did not have the correct name!", TEST_LANGUAGE_NAME, language.getName());
	}

	@Override
	public void testDuplication() {
		languageRepo.create(TEST_LANGUAGE_NAME);
		try {
			languageRepo.create(TEST_LANGUAGE_NAME);
		} catch (DataIntegrityViolationException e) {
			/* SUCCESS */ }
		assertEquals("The repository duplicated entity!", 1, languageRepo.count());
	}

	@Override
	public void testFind() {
		languageRepo.create(TEST_LANGUAGE_NAME);
		Language language = languageRepo.findByName(TEST_LANGUAGE_NAME);
		assertNotEquals("The entity was not found!", null, language);
		assertEquals("The entity found did not have the correct name!", TEST_LANGUAGE_NAME, language.getName());
	}

	@Override
	public void testDelete() {
		Language language = languageRepo.create(TEST_LANGUAGE_NAME);
		languageRepo.delete(language);
		assertEquals("The entity was not deleted!", 0, languageRepo.count());
	}

	@Override
	public void testCascade() {

	}

	@After
	public void cleanUp() {
		languageRepo.deleteAll();
	}

}
