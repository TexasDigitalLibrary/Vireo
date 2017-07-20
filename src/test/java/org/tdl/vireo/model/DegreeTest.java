package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class DegreeTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        Degree degree = degreeRepo.create(TEST_DEGREE_NAME, TEST_DEGREE_PROQUEST_CODE);
        assertEquals("The repository did not save the entity!", 1, degreeRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_DEGREE_NAME, degree.getName());
        assertEquals("Saved entity did not contain the name!", TEST_DEGREE_PROQUEST_CODE, degree.getProquestCode());
    }

    @Override
    public void testDuplication() {
        degreeRepo.create(TEST_DEGREE_NAME, TEST_DEGREE_PROQUEST_CODE);
        try {
            degreeRepo.create(TEST_DEGREE_NAME, TEST_DEGREE_PROQUEST_CODE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals("The repository duplicated entity!", 1, degreeRepo.count());
    }

    @Override
    public void testDelete() {
        Degree degree = degreeRepo.create(TEST_DEGREE_NAME, TEST_DEGREE_PROQUEST_CODE);
        degreeRepo.delete(degree);
        assertEquals("The entity was not deleted!", 0, degreeRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        degreeRepo.deleteAll();
    }

}
