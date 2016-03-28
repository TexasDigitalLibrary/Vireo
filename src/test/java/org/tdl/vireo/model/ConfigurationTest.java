package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.tdl.vireo.config.constant.ConfigurationName;

public class ConfigurationTest extends AbstractEntityTest {
    
    @Override
    public void testCreate() {
        // set vireo.install.dir
        Configuration installPath = configurationRepo.createOrUpdate(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR,"application");
        assertEquals("The install path configuration name was wrong!", TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, ConfigurationName.SUBMISSIONS_OPEN);
        assertEquals("The install path configuration value was wrong!", TEST_VIREO_INSTALL_DIR, installPath.getValue());
        assertEquals("The configuration was not saved!", 1, configurationRepo.count());
    }

    @Override
    public void testDuplication() {
        // configurations should just override their values if attempted to be
        // created again (a copy should not be made)
        configurationRepo.createOrUpdate(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR,"application");
        Configuration changedConfig = configurationRepo.createOrUpdate(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR_CHANGED,"application");
        assertEquals("The configuration was duplicated!", 1, configurationRepo.count());
        assertEquals("The configuration was not changed!", TEST_VIREO_INSTALL_DIR_CHANGED, changedConfig.getValue());
    }

    @Override
    public void testDelete() {
        Configuration configToDelete = configurationRepo.createOrUpdate(TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY, TEST_VIREO_INSTALL_DIR,"application");
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
