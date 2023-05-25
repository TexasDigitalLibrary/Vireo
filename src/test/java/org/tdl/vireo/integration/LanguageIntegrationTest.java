package org.tdl.vireo.integration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.repo.LanguageRepo;

public class LanguageIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private LanguageRepo languageRepo;

    @BeforeEach
    public void setup() {

        systemDataLoader.loadSystemDefaults();

        languageRepo.create(TEST_LANGUAGE_NAME1);
        languageRepo.create(TEST_LANGUAGE_NAME2);
        languageRepo.create(TEST_LANGUAGE_NAME3);

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    }

    @Test
    public void testGetAllLanguages() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        // TODO
    }

}
