package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;

public class ConfigurationTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        // set vireo.install.dir
        Configuration installPath = configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR);
        assertEquals("The install path configuration name was wrong!", TEST_VIREO_CONFIG_INSTALL_DIR_KEY, installPath.getName());
        assertEquals("The install path configuration value was wrong!", TEST_VIREO_INSTALL_DIR, installPath.getValue());
        assertEquals("The configuration was not saved!", 1, configurationRepo.count());
    }

    @Override
    public void testDuplication() {
        // configurations should just override their values if attempted to be
        // created again (a copy should not be made)
        configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR);
        Configuration changedConfig = configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR_CHANGED);
        assertEquals("The configuration was duplicated!", 1, configurationRepo.count());
        assertEquals("The configuration was not changed!", TEST_VIREO_INSTALL_DIR_CHANGED, changedConfig.getValue());
    }

    @Override
    public void testDelete() {
        Configuration configToDelete = configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR);
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
