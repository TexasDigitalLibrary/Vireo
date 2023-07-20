package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.model.GraduationMonth;

public class GraduationMonthRepoTest extends AbstractRepoTest {

    @Override
    @Test
    public void testCreate() {
        GraduationMonth graduationMonth = graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        assertEquals(1, graduationMonthRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_GRADUATION_MONTH, graduationMonth.getMonth(), "Saved entity did not contain the value!");
    }

    @Override
    @Test
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
    @Test
    public void testDelete() {
        GraduationMonth graduationMonth = graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        graduationMonthRepo.delete(graduationMonth);
        assertEquals(0, graduationMonthRepo.count(), "The entity was not deleted!");
    }

    @Override
    @Test
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        graduationMonthRepo.deleteAll();
    }

}
