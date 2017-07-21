package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class DegreeTest extends AbstractEntityTest {

    @Before
    public void setup() {
        degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
    }

    @Override
    public void testCreate() {
        Degree degree = degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        assertEquals("The repository did not save the entity!", 1, degreeRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_DEGREE_NAME, degree.getName());
        assertEquals("Saved entity did not contain the degree level!", degreeLevel.getName(), degree.getLevel().getName());
    }

    @Override
    public void testDuplication() {
        degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        try {
            degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals("The repository duplicated entity!", 1, degreeRepo.count());
    }

    @Override
    public void testDelete() {
        Degree degree = degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        degreeRepo.delete(degree);
        assertEquals("The entity was not deleted!", 0, degreeRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        degreeRepo.deleteAll();
        degreeLevelRepo.deleteAll();
    }

}
