package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class EntityCVWhitelistTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        // delete the default initially added to whitelist
        entityCVWhitelistRepo.deleteAll();
        assertEquals("EntityCVWhitelist repo was not empty!", 0, entityCVWhitelistRepo.count());
    }

    @Override
    public void testCreate() {
        EntityCVWhitelist entityCVWhitelist = entityCVWhitelistRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO);
        assertEquals("EntityCVWhitelist Repo did not save the entity cv whitelist!", 1, entityCVWhitelistRepo.count());
        assertEquals("EntityCVWhitelist Repo did not save the correct entity cv whitelist entity name!", TEST_CONTROLLED_VOCABULARY_EMBARGO, entityCVWhitelist.getEntityName());
    }

    @Override
    public void testDuplication() {
        entityCVWhitelistRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO);
        assertEquals("The repository didn't persist controller vocabulary whitelist!", 1, entityCVWhitelistRepo.count());
        try {
            entityCVWhitelistRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated entity cv whitelist!", 1, entityCVWhitelistRepo.count());
    }

    @Override
    public void testDelete() {
        EntityCVWhitelist entityCVWhitelist = entityCVWhitelistRepo.create(TEST_CONTROLLED_VOCABULARY_EMBARGO);
        entityCVWhitelistRepo.delete(entityCVWhitelist);
        assertEquals("EntityCVWhitelist did not delete!", 0, entityCVWhitelistRepo.count());
    }

    @Override
    public void testCascade() {
        // nothing to cascade
    }

    @After
    public void cleanUp() {
        entityCVWhitelistRepo.deleteAll();
    }

}