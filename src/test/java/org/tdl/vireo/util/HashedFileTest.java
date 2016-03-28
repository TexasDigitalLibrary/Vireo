package org.tdl.vireo.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.loader.tools.FileUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.runner.OrderedRunner;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles({"test"})
public class HashedFileTest {

    private static final String TEST_VIREO_CONFIG_ATTACHMENTS_PATH_KEY = ConfigurationName.APPLICATION_ATTACHMENTS_PATH;
    private static final String TEST_VIREO_ATTACHMENTS_PATH = "attachments";
    private static File TEST_ATTACHMENT_STORE_PATH;
    private static final File TEST_FILE_TO_WRITE = new File("pom.xml");

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private HashedFile hashedFile;

    @Before
    public void setUp() {
        // set attachments.path
        configurationRepo.createOrUpdate(TEST_VIREO_CONFIG_ATTACHMENTS_PATH_KEY, TEST_VIREO_ATTACHMENTS_PATH,"application");
        Application.init(true);
        TEST_ATTACHMENT_STORE_PATH = new File(Application.BASE_PATH + TEST_VIREO_ATTACHMENTS_PATH);
    }

    @Test
    @Order(value = 1)
    public void testWriteAndGet() throws IOException, NoSuchAlgorithmException {
        // do the tests
        InputStream is = new FileInputStream(TEST_FILE_TO_WRITE);
        UUID uuid = hashedFile.write(is);
        File storedFile = hashedFile.getFile(uuid);
        // get the sha1 hashes
        String hashTEST = FileUtils.sha1Hash(TEST_FILE_TO_WRITE);
        String hashStored = FileUtils.sha1Hash(storedFile);
        // assert that the files are the same through their sha1 hashes
        assertEquals("The attachment file was not written and/or read back correctly!", hashTEST, hashStored);
    }

    @Test
    @Order(value = 2)
    public void getStore() throws IOException {
        File store = hashedFile.getStore();
        assertEquals("The attachment store path was incorrect!", TEST_ATTACHMENT_STORE_PATH, store);
    }

    @After
    public void cleanUp() throws IOException {
        configurationRepo.deleteAll();

        // delete recursively, files first (why it's sorted backwards: b->a instead of a->b)
        Files.walk(TEST_ATTACHMENT_STORE_PATH.toPath()).sorted((a, b) -> b.compareTo(a)).forEach(p -> {
            try {
                Files.delete(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
