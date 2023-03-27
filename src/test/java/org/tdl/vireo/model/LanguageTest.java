package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.model.repo.LanguageRepo;

public class LanguageTest extends AbstractEntityTest {

    @Autowired
    private LanguageRepo languageRepo;

    @Override
    @Test
    public void testCreate() {
        Language language = languageRepo.create(TEST_LANGUAGE_NAME);
        assertEquals(1, languageRepo.count(), "The entity was not created!");
        assertEquals(TEST_LANGUAGE_NAME, language.getName(), "The entity did not have the correct name!");
    }

    @Override
    @Test
    public void testDuplication() {
        languageRepo.create(TEST_LANGUAGE_NAME);
        try {
            languageRepo.create(TEST_LANGUAGE_NAME);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals(1, languageRepo.count(), "The repository duplicated entity!");
    }

    @Override
    @Test
    public void testDelete() {
        Language language = languageRepo.create(TEST_LANGUAGE_NAME);
        languageRepo.delete(language);
        assertEquals(0, languageRepo.count(), "The entity was not deleted!");
    }

    @Override
    @Test
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        languageRepo.deleteAll();
    }

}
