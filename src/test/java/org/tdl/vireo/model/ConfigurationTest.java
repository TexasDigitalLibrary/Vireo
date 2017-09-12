package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.config.constant.ConfigurationName;

public class ConfigurationTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        // set vireo.install.dir
        ManagedConfiguration installPath = configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR, "application");
        assertEquals("The install path configuration name was wrong!", TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, ConfigurationName.SUBMISSIONS_OPEN);
        assertEquals("The install path configuration value was wrong!", TEST_VIREO_INSTALL_DIR, installPath.getValue());
        assertEquals("The configuration was not saved!", 1, configurationRepo.count());
    }

    @Override
    public void testDuplication() {
        configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR, "application");
        try {
            configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR_CHANGED, "application");
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }

        assertEquals("The configuration was duplicated!", 1, configurationRepo.count());
    }

    @Override
    public void testDelete() {
        ManagedConfiguration configToDelete = configurationRepo.create(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR, "application");
        configurationRepo.delete(configToDelete);
        assertEquals("The configuration was not deleted!", 0, configurationRepo.count());
    }

    @Override
    public void testCascade() {
    }

    @After
    public void cleanUp() {
        configurationRepo.deleteAll();
    }
}
