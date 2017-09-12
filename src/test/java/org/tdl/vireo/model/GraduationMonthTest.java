package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class GraduationMonthTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        GraduationMonth graduationMonth = graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        assertEquals("The repository did not save the entity!", 1, graduationMonthRepo.count());
        assertEquals("Saved entity did not contain the value!", TEST_GRADUATION_MONTH, graduationMonth.getMonth());
    }

    @Override
    public void testDuplication() {
        graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        try {
            graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals("The repository duplicated entity!", 1, graduationMonthRepo.count());
    }

    @Override
    public void testDelete() {
        GraduationMonth graduationMonth = graduationMonthRepo.create(TEST_GRADUATION_MONTH);
        graduationMonthRepo.delete(graduationMonth);
        assertEquals("The entity was not deleted!", 0, graduationMonthRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        graduationMonthRepo.deleteAll();
    }

}
