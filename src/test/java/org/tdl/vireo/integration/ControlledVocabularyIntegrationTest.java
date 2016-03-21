package org.tdl.vireo.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ControlledVocabularyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ControlledVocabularyCachingService controlledVocabularyCachingService;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    private LanguageRepo languageRepo;
    
    @Override
    public void setup() {
        
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME1, languageRepo.create(TEST_LANGUAGE_NAME1));
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME2, languageRepo.create(TEST_LANGUAGE_NAME2));
        controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME3, languageRepo.create(TEST_LANGUAGE_NAME3));
        
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                        
        brokerChannelInterceptor = new MockChannelInterceptor();
        
        brokerChannel.addInterceptor(brokerChannelInterceptor);
        
        StompConnect();
    }
   
    public void addVocabularyWords() {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1);
        
        VocabularyWord word1 = vocabularyWordRepo.create(TEST_VOCABULARY_WORD_NAME1, TEST_VOCABULARY_WORD_DEFINITION1, TEST_VOCABULARY_WORD_DEFINITION1);
        VocabularyWord word2 = vocabularyWordRepo.create(TEST_VOCABULARY_WORD_NAME2, TEST_VOCABULARY_WORD_DEFINITION2, TEST_VOCABULARY_WORD_DEFINITION2);
        VocabularyWord word3 = vocabularyWordRepo.create(TEST_VOCABULARY_WORD_NAME3, TEST_VOCABULARY_WORD_DEFINITION3, TEST_VOCABULARY_WORD_DEFINITION3);
        
        word1.setControlledVocabulary(controlledVocabulary);
        vocabularyWordRepo.save(word1);
        word2.setControlledVocabulary(controlledVocabulary);
        vocabularyWordRepo.save(word2);
        word3.setControlledVocabulary(controlledVocabulary);
        vocabularyWordRepo.save(word3);
        
        controlledVocabulary.addValue(word1);
        controlledVocabulary.addValue(word2);
        controlledVocabulary.addValue(word3);
        
        controlledVocabularyRepo.save(controlledVocabulary);
    }

    @Test
    @Order(value = 1)
    public void testGetAllControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        String responseJson = StompRequest("/settings/controlled-vocabulary/all", null);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        @SuppressWarnings("unchecked")
        Map<String, List<ControlledVocabulary>> allControlledVocabulariesMap = (Map<String, List<ControlledVocabulary>>) payload.get("HashMap");
        
        List<ControlledVocabulary> allControlledVocabularies = allControlledVocabulariesMap.get("list");
                
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME1, objectMapper.convertValue(allControlledVocabularies.get(0), ControlledVocabulary.class).getName());
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME2, objectMapper.convertValue(allControlledVocabularies.get(1), ControlledVocabulary.class).getName());
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME3, objectMapper.convertValue(allControlledVocabularies.get(2), ControlledVocabulary.class).getName());
    }

    @Test
    @Order(value = 2)
    public void testGetControlledVocabularyByName() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        String responseJson = StompRequest("/settings/controlled-vocabulary/" + TEST_CONTROLLED_VOCABULARY_NAME1, null);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");
        
        ControlledVocabulary controlledVocabulary = objectMapper.convertValue(payload.get("ControlledVocabulary"), ControlledVocabulary.class);
        
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME1, controlledVocabulary.getName());
        assertEquals(TEST_LANGUAGE_NAME1, controlledVocabulary.getLanguage().getName());        
    }

    @Test
    @Order(value = 3)
    public void testCreateControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        
        String TEST_CONTROLLED_VOCABULARY_NAME4 = "TestCV4";
        String TEST_LANGUAGE_NAME4 = "German";
        
        languageRepo.create(TEST_LANGUAGE_NAME4);
        
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("name", TEST_CONTROLLED_VOCABULARY_NAME4);
        
        Map<String, String> langaugeMap = new HashMap<String, String>();
        
        langaugeMap.put("name", TEST_LANGUAGE_NAME4);
        
        dataMap.put("language", langaugeMap);
        
        String responseJson = StompRequest("/settings/controlled-vocabulary/create", dataMap);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");
        
        assertEquals("SUCCESS", meta.get("type"));
        
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME4);
        
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME4, controlledVocabulary.getName());
        assertEquals(TEST_LANGUAGE_NAME4, controlledVocabulary.getLanguage().getName()); 
    }

    @Test
    @Order(value = 4)
    public void testUpdateControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        
        String TEST_CONTROLLED_VOCABULARY_NAME4 = "TestCV4";

        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1);
        
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("id", controlledVocabulary.getId().toString());
        
        dataMap.put("name", TEST_CONTROLLED_VOCABULARY_NAME4);
                
        String responseJson = StompRequest("/settings/controlled-vocabulary/update", dataMap);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");
        
        assertEquals("SUCCESS", meta.get("type"));
        
        controlledVocabulary = controlledVocabularyRepo.findOne(controlledVocabulary.getId());
        
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME4, controlledVocabulary.getName());
        
    }

    @Test
    @Order(value = 5)
    public void testRemoveControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1);
        
        String responseJson = StompRequest("/settings/controlled-vocabulary/remove/" + controlledVocabulary.getOrder(), null);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");
        
        assertEquals("SUCCESS", meta.get("type"));
        
        assertNull(controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1));
    }

    @Test
    @Order(value = 6)
    public void testReorderControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {

        ControlledVocabulary controlledVocabulary1 = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1);
        ControlledVocabulary controlledVocabulary2 = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME2);
        
        Integer order1 = controlledVocabulary1.getOrder();
        Integer order2 = controlledVocabulary2.getOrder();
        
        String responseJson = StompRequest("/settings/controlled-vocabulary/reorder/" + order1 + "/" + order2, null);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");
        
        assertEquals("SUCCESS", meta.get("type"));
        
        assertEquals(order1, controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME2).getOrder());
        assertEquals(order2, controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1).getOrder());
    }

    @Test
    @Order(value = 7)
    public void testSortControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        String responseJson = StompRequest("/settings/controlled-vocabulary/sort/name", null);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");
        
        assertEquals("SUCCESS", meta.get("type"));
        
        assertEquals(Integer.valueOf(1), controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME3).getOrder());
        assertEquals(Integer.valueOf(2), controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME2).getOrder());
        assertEquals(Integer.valueOf(3), controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1).getOrder());
    }

    @Test
    @Order(value = 8)
    public void testExportControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        
        addVocabularyWords();
        
        String responseJson = StompRequest("/settings/controlled-vocabulary/export/" + TEST_CONTROLLED_VOCABULARY_NAME1, null);
        
        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");
        
        assertEquals("SUCCESS", meta.get("type"));        
        
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");
        
        System.out.println(payload);
        //TODO: compare against expected values
    }

    @Test
    @Order(value = 9)
    public void testImportControlledVocabularyStatus() {

    }

    @Test
    @Order(value = 10)
    public void testCancelImportControlledVocabulary() {

    }

    @Test
    @Order(value = 11)
    public void testCompareControlledVocabulary() {

    }

    @Test
    @Order(value = 12)
    public void testImportControlledVocabulary() {

    }

    @Test
    @Order(value = 13)
    public void testInputStreamToRows() {

    }

    @Override
    public void cleanup() {        
        vocabularyWordRepo.findAll().forEach(word -> {
            ControlledVocabulary cv = word.getControlledVocabulary();
            cv.removeValue(word);
            controlledVocabularyRepo.save(cv);
            word.setControlledVocabulary(null);
            vocabularyWordRepo.save(word);
        });        
        vocabularyWordRepo.deleteAll();
        controlledVocabularyRepo.deleteAll();        
        languageRepo.deleteAll();       
    }

}
