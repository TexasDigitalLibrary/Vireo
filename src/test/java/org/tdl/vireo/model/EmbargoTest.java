package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class EmbargoTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("Embargo repo was not empty!", 0, embargoRepo.count());
    }

    @Override
    public void testCreate() {
        Embargo testEmbargo = embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals("Embargo Repo did not save the embargo!", 1, embargoRepo.count());
        assertEquals("Embargo Repo did not save the correct embargo name!", TEST_EMBARGO_NAME, testEmbargo.getName());
        assertEquals("Embargo Repo did not save the correct embargo description!", TEST_EMBARGO_DESCRIPTION, testEmbargo.getDescription());
        assertEquals("Embargo Repo did not save the correct embargo duration!", TEST_EMBARGO_DURATION, testEmbargo.getDuration());
    }

    public void testUpdate() {
        Embargo testEmbargo = embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals("Embargo Repo did not save the embargo!", 1, embargoRepo.count());

        testEmbargo.setSystemRequired(!testEmbargo.getSystemRequired());
        testEmbargo.setName("Updated Name");
        testEmbargo.setDescription("Updated Description");
        testEmbargo.setDuration(99);
        testEmbargo.setGuarantor(TEST_EMBARGO_TYPE_GUARANTOR == EmbargoGuarantor.DEFAULT? EmbargoGuarantor.PROQUEST : EmbargoGuarantor.PROQUEST);
        testEmbargo.setPosition(9000L);

        Embargo updatedEmbargo = embargoRepo.update(testEmbargo);
        assertEquals("Embargo Repo did not update the embargo SystemRequired property!", testEmbargo.getSystemRequired(), updatedEmbargo.getSystemRequired());
        assertEquals("Embargo Repo did not update the embargo Name property!", testEmbargo.getName(), updatedEmbargo.getName());
        assertEquals("Embargo Repo did not update the embargo Description property!", testEmbargo.getDescription(), updatedEmbargo.getDescription());
        assertEquals("Embargo Repo did not update the embargo Duration property!", testEmbargo.getDuration(), updatedEmbargo.getDuration());
        assertEquals("Embargo Repo did not update the embargo Guarantor property!", testEmbargo.getGuarantor(), updatedEmbargo.getGuarantor());
        assertEquals("Embargo Repo did not update the embargo Position property!", testEmbargo.getPosition(), updatedEmbargo.getPosition());
    }

    @Override
    public void testDuplication() {
        embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        assertEquals("The repository didn't persist embargo!", 1, embargoRepo.count());
        try {
            embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository didn't persist embargo!", 1, embargoRepo.count());
    }

    @Override
    public void testDelete() {
        Embargo testEmbargo = embargoRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION, TEST_EMBARGO_TYPE_GUARANTOR, TEST_EMBARGO_IS_ACTIVE);
        embargoRepo.delete(testEmbargo);
        assertEquals("Embargo did not delete!", 0, embargoRepo.count());
    }

    @Override
    public void testCascade() {
        // nothing to cascade
    }

    @After
    public void cleanUp() {
        embargoRepo.deleteAll();
    }

}
