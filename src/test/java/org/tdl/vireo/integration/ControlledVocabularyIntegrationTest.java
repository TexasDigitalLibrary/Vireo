package org.tdl.vireo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;

public class ControlledVocabularyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @BeforeEach
    public void setup() {
        assertEquals(0, controlledVocabularyRepo.count());

        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME1);
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME2);
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME3);

        assertEquals(3, controlledVocabularyRepo.count());

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetAllControlledVocabulary() {
        // TODO
    }

    @Test
    public void testGetControlledVocabularyByName() {
        // TODO
    }

    @Test
    public void testCreateControlledVocabulary() {
        // TODO
    }

    @Test
    public void testUpdateControlledVocabulary() {
        // TODO
    }

    @Test
    public void testRemoveControlledVocabulary() {
        // TODO
    }

    @Test
    public void testReorderControlledVocabulary() {
        // TODO
    }

    @Test
    public void testSortControlledVocabulary() {
        // TODO
    }

    @Test
    public void testExportControlledVocabulary() {
        // TODO
    }

    @Test
    public void testImportControlledVocabularyStatus() {
        // TODO
    }

    @Test
    public void testCancelImportControlledVocabulary() {
        // TODO
    }

    @Test
    public void testCompareControlledVocabulary() {
        // TODO
    }

    @Test
    public void testImportControlledVocabulary() {
        // TODO
    }

    @Test
    public void testInputStreamToRows() {
        // TODO
    }

}
