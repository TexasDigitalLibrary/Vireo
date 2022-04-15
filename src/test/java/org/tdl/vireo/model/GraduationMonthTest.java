package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.springframework.dao.DataIntegrityViolationException;

public class GraduationMonthTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        GraduationMonth graduationMonth = graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        assertEquals(1, graduationMonthRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_GRADUATION_MONTH, graduationMonth.getMonth(), "Saved entity did not contain the value!");
    }

    @Override
    public void testDuplication() {
        graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        try {
            graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals(1, graduationMonthRepo.count(), "The repository duplicated entity!");
    }

    @Override
    public void testDelete() {
        GraduationMonth graduationMonth = graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        graduationMonthRepo.delete(graduationMonth);
        assertEquals(0, graduationMonthRepo.count(), "The entity was not deleted!");
    }

    @Override
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        graduationMonthRepo.deleteAll();
    }

}
