package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class EmbargoTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, embargoRepo.count(), "Embargo repo was not empty!");
    }

    @Override
    @Test
    public void testCreate() {
        Embargo testEmbargo = embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals(1, embargoRepo.count(), "Embargo Repo did not save the embargo!");
        assertEquals(TEST_EMBARGO_NAME, testEmbargo.getName(), "Embargo Repo did not save the correct embargo name!");
        assertEquals(TEST_EMBARGO_DESCRIPTION, testEmbargo.getDescription(), "Embargo Repo did not save the correct embargo description!");
        assertEquals(TEST_EMBARGO_DURATION, testEmbargo.getDuration(), "Embargo Repo did not save the correct embargo duration!");
    }

    @Test
    public void testUpdate() {
        Embargo testEmbargo = embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals(1, embargoRepo.count(), "Embargo Repo did not save the embargo!");

        testEmbargo.setSystemRequired(!testEmbargo.getSystemRequired());
        testEmbargo.setName("Updated Name");
        testEmbargo.setDescription("Updated Description");
        testEmbargo.setDuration(99);
        testEmbargo.setGuarantor(TEST_EMBARGO_TYPE_GUARANTOR == EmbargoGuarantor.DEFAULT? EmbargoGuarantor.PROQUEST : EmbargoGuarantor.PROQUEST);
        testEmbargo.setPosition(9000L);

        Embargo updatedEmbargo = embargoRepo.update(testEmbargo);
        assertEquals(testEmbargo.getSystemRequired(), updatedEmbargo.getSystemRequired(), "Embargo Repo did not update the embargo SystemRequired property!");
        assertEquals(testEmbargo.getName(), updatedEmbargo.getName(), "Embargo Repo did not update the embargo Name property!");
        assertEquals(testEmbargo.getDescription(), updatedEmbargo.getDescription(), "Embargo Repo did not update the embargo Description property!");
        assertEquals(testEmbargo.getDuration(), updatedEmbargo.getDuration(), "Embargo Repo did not update the embargo Duration property!");
        assertEquals(testEmbargo.getGuarantor(), updatedEmbargo.getGuarantor(), "Embargo Repo did not update the embargo Guarantor property!");
        assertEquals(testEmbargo.getPosition(), updatedEmbargo.getPosition(), "Embargo Repo did not update the embargo Position property!");
    }

    @Override
    @Test
    public void testDuplication() {
        embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals(1, embargoRepo.count(), "The repository didn't persist embargo!");
        try {
            embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals(1, embargoRepo.count(), "The repository didn't persist embargo!");
    }

    @Override
    @Test
    public void testDelete() {
        Embargo testEmbargo = embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        embargoRepo.delete(testEmbargo);
        assertEquals(0, embargoRepo.count(), "Embargo did not delete!");
    }

    @Override
    @Test
    public void testCascade() {
        // nothing to cascade
    }

    @AfterEach
    public void cleanUp() {
        embargoRepo.deleteAll();
    }

}
