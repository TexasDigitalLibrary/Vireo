package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ConfigurationTest {

    private static final String TEST_VIREO_CONFIG_INSTALL_DIR_KEY = ConfigurationName.APPLICATION_INSTALL_DIRECTORY;
    private static final String TEST_VIREO_INSTALL_DIR = "./";
    private static final String TEST_VIREO_INSTALL_DIR_CHANGED = TEST_VIREO_INSTALL_DIR + "changed/";

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Test
    @Order(value = 1)
    public void testCreate() {
        // set vireo.install.dir
        Configuration installPath = configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR);
        assertEquals("The install path configuration name was wrong!", TEST_VIREO_CONFIG_INSTALL_DIR_KEY, installPath.getName());
        assertEquals("The install path configuration value was wrong!", TEST_VIREO_INSTALL_DIR, installPath.getValue());
        assertEquals("The configuration was not saved!", 1, configurationRepo.count());
    }

    @Test
    @Order(value = 2)
    public void testDuplicate() {
        // configurations should just override their values if attempted to be created again (a copy should not be made)
        configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR);
        Configuration changedConfig = configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR_CHANGED);
        assertEquals("The configuration was duplicated!", 1, configurationRepo.count());
        assertEquals("The configuration was not changed!", TEST_VIREO_INSTALL_DIR_CHANGED, changedConfig.getValue());
    }

    @Test
    @Order(value = 3)
    public void testDelete() {
        Configuration configToDelete = configurationRepo.create(TEST_VIREO_CONFIG_INSTALL_DIR_KEY, TEST_VIREO_INSTALL_DIR);
        configurationRepo.delete(configToDelete);
        assertEquals("The configuration was not deleted!", 0, configurationRepo.count());
    }

    @After
    public void cleanUp() {
        configurationRepo.deleteAll();
    }
}
