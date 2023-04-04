package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class DegreeLevelTest extends AbstractEntityTest {

    @Override
    @Test
    public void testCreate() {
        DegreeLevel degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        assertEquals(1, degreeLevelRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_DEGREE_LEVEL, degreeLevel.getName(), "Saved entity did not contain the name!");
    }

    @Override
    @Test
    public void testDuplication() {
        degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        try {
            degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals(1, degreeLevelRepo.count(), "The repository duplicated entity!");
    }

    @Override
    @Test
    public void testDelete() {
        DegreeLevel degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        degreeLevelRepo.delete(degreeLevel);
        assertEquals(0, degreeLevelRepo.count(), "The entity was not deleted!");
    }

    @Override
    @Test
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        degreeLevelRepo.deleteAll();
    }

}
