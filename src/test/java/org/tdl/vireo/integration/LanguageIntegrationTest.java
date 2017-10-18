package org.tdl.vireo.integration;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class LanguageIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterRepo;

    @Override
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

    @Override
    public void cleanup() {
        languageRepo.deleteAll();
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        userRepo.deleteAll();
    }

}
