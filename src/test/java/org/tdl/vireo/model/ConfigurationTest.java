package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.config.constant.ConfigurationName;

public class ConfigurationTest extends AbstractEntityTest {

    @Override
    @Test
    public void testCreate() {
        // set vireo.install.dir
        ManagedConfiguration installPath = configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR, "application");
        assertEquals(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, ConfigurationName.SUBMISSIONS_OPEN, "The install path configuration name was wrong!");
        assertEquals(TEST_VIREO_INSTALL_DIR, installPath.getValue(), "The install path configuration value was wrong!");
        assertEquals(1, configurationRepo.count(), "The configuration was not saved!");
    }

    @Override
    @Test
    public void testDuplication() {
        configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR, "application");
        try {
            configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR_CHANGED, "application");
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }

        assertEquals(1, configurationRepo.count(), "The configuration was duplicated!");
    }

    @Override
    @Test
    public void testDelete() {
        ManagedConfiguration configToDelete = configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR, "application");
        configurationRepo.delete(configToDelete);
        assertEquals(0, configurationRepo.count(), "The configuration was not deleted!");
    }

    @Override
    @Test
    public void testCascade() {
    }

    @AfterEach
    public void cleanUp() {
        configurationRepo.deleteAll();
    }
}
