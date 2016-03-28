package org.tdl.vireo.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class LanguageIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private LanguageRepo languageRepo;
        
    @Override
    public void setup() {
        
        languageRepo.create(TEST_LANGUAGE_NAME1);
        languageRepo.create(TEST_LANGUAGE_NAME2);
        languageRepo.create(TEST_LANGUAGE_NAME3);
        
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                        
        brokerChannelInterceptor = new MockChannelInterceptor();
        
        brokerChannel.addInterceptor(brokerChannelInterceptor);
        
        StompConnect();

    }

    @Test
    @Order(value = 1)
    public void testGetAllLanguages() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        String responseJson = StompRequest("/settings/languages/all", null);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        @SuppressWarnings("unchecked")
        Map<String, List<Language>> allLanguagesMap = (Map<String, List<Language>>) payload.get("HashMap");
        
        List<Language> allLanguages = allLanguagesMap.get("list");
                
        assertEquals(TEST_LANGUAGE_NAME1, objectMapper.convertValue(allLanguages.get(0), Language.class).getName());
        assertEquals(TEST_LANGUAGE_NAME2, objectMapper.convertValue(allLanguages.get(1), Language.class).getName());
        assertEquals(TEST_LANGUAGE_NAME3, objectMapper.convertValue(allLanguages.get(2), Language.class).getName());
    }

    @Override
    public void cleanup() {
        languageRepo.deleteAll();
    }

}
