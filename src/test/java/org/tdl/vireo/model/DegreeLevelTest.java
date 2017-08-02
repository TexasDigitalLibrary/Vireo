package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class DegreeLevelTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        DegreeLevel degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        assertEquals("The repository did not save the entity!", 1, degreeLevelRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_DEGREE_LEVEL, degreeLevel.getName());
    }

    @Override
    public void testDuplication() {
        degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        try {
            degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals("The repository duplicated entity!", 1, degreeLevelRepo.count());
    }

    @Override
    public void testDelete() {
        DegreeLevel degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
        degreeLevelRepo.delete(degreeLevel);
        assertEquals("The entity was not deleted!", 0, degreeLevelRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        degreeLevelRepo.deleteAll();
    }

}
