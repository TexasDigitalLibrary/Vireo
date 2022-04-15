package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.springframework.dao.DataIntegrityViolationException;

public class DegreeLevelTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        DegreeLevel degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        assertEquals(1, degreeLevelRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_DEGREE_LEVEL, degreeLevel.getName(), "Saved entity did not contain the name!");
    }

    @Override
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
    public void testDelete() {
        DegreeLevel degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        degreeLevelRepo.delete(degreeLevel);
        assertEquals(0, degreeLevelRepo.count(), "The entity was not deleted!");
    }

    @Override
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        degreeLevelRepo.deleteAll();
    }

}
