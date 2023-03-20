package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
public class DegreeTest extends AbstractEntityTest {

    @BeforeEach
    public void setup() {
        degreeLevel = degreeLevelRepo.create(TEST_DEGREE_LEVEL);
    }

    @Override
    @Test
    public void testCreate() {
        Degree degree = degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        assertEquals(1, degreeRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_DEGREE_NAME, degree.getName(), "Saved entity did not contain the name!");
        assertEquals(degreeLevel.getName(), degree.getLevel().getName(), "Saved entity did not contain the degree level!");
    }

    @Override
    @Test
    public void testDuplication() {
        degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        try {
            degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals(1, degreeRepo.count(), "The repository duplicated entity!");
    }

    @Override
    @Test
    public void testDelete() {
        Degree degree = degreeRepo.create(TEST_DEGREE_NAME, degreeLevel);
        degreeRepo.delete(degree);
        assertEquals(0, degreeRepo.count(), "The entity was not deleted!");
    }

    @Override
    @Test
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        degreeRepo.deleteAll();
        degreeLevelRepo.deleteAll();
    }

}
