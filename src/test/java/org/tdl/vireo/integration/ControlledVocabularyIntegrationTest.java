package org.tdl.vireo.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ControlledVocabularyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Override
    public void setup() {

        controlledVocabularyRepo.deleteAll();

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

        VocabularyWord word1 = vocabularyWordRepo.create(TEST_VOCABULARY_WORD_NAME1, TEST_VOCABULARY_WORD_DEFINITION1, TEST_VOCABULARY_WORD_IDENTIFIER1);
        VocabularyWord word2 = vocabularyWordRepo.create(TEST_VOCABULARY_WORD_NAME2, TEST_VOCABULARY_WORD_DEFINITION2, TEST_VOCABULARY_WORD_IDENTIFIER2);
        VocabularyWord word3 = vocabularyWordRepo.create(TEST_VOCABULARY_WORD_NAME3, TEST_VOCABULARY_WORD_DEFINITION3, TEST_VOCABULARY_WORD_IDENTIFIER3);

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
    public void testGetAllControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        String responseJson = StompRequest("/settings/controlled-vocabulary/all", "");

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        @SuppressWarnings("unchecked")
        List<ControlledVocabulary> allControlledVocabularies = (List<ControlledVocabulary>) payload.get("ArrayList<ControlledVocabulary>");

        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME1, objectMapper.convertValue(allControlledVocabularies.get(0), ControlledVocabulary.class).getName());
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME2, objectMapper.convertValue(allControlledVocabularies.get(1), ControlledVocabulary.class).getName());
        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME3, objectMapper.convertValue(allControlledVocabularies.get(2), ControlledVocabulary.class).getName());
    }

    @Test
    public void testGetControlledVocabularyByName() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        String responseJson = StompRequest("/settings/controlled-vocabulary/" + TEST_CONTROLLED_VOCABULARY_NAME1, "");

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        ControlledVocabulary controlledVocabulary = objectMapper.convertValue(payload.get("ControlledVocabulary"), ControlledVocabulary.class);

        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME1, controlledVocabulary.getName());
        assertEquals(TEST_LANGUAGE_NAME1, controlledVocabulary.getLanguage().getName());
    }

    @Test
    public void testCreateControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {

        String TEST_CONTROLLED_VOCABULARY_NAME4 = "TestCV4";
        String TEST_LANGUAGE_NAME4 = "German";

        Language language = languageRepo.create(TEST_LANGUAGE_NAME4);
        ControlledVocabulary testCV4 = new ControlledVocabulary(TEST_CONTROLLED_VOCABULARY_NAME4, language);

        String responseJson = StompRequest("/settings/controlled-vocabulary/create", testCV4);

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");
        
        System.out.println("\n\n\n" + meta + "\n\n\n");

        assertEquals("SUCCESS", meta.get("type"));

        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME4);

        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME4, controlledVocabulary.getName());
        assertEquals(TEST_LANGUAGE_NAME4, controlledVocabulary.getLanguage().getName());
    }

    @Test
    public void testUpdateControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {

        String TEST_CONTROLLED_VOCABULARY_NAME4 = "TestCV4";

        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1);
        controlledVocabulary.setName(TEST_CONTROLLED_VOCABULARY_NAME4);

        String responseJson = StompRequest("/settings/controlled-vocabulary/update", controlledVocabulary);

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");

        assertEquals("SUCCESS", meta.get("type"));

        controlledVocabulary = controlledVocabularyRepo.findOne(controlledVocabulary.getId());

        assertEquals(TEST_CONTROLLED_VOCABULARY_NAME4, controlledVocabulary.getName());

    }

    @Test
    public void testRemoveControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {

        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1);

        String responseJson = StompRequest("/settings/controlled-vocabulary/remove", controlledVocabulary);

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");

        assertEquals("SUCCESS", meta.get("type"));

        assertNull(controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1));
    }

    @Test
    public void testReorderControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {

        ControlledVocabulary controlledVocabulary1 = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1);
        ControlledVocabulary controlledVocabulary2 = controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME2);

        Long order1 = controlledVocabulary1.getPosition();
        Long order2 = controlledVocabulary2.getPosition();

        String responseJson = StompRequest("/settings/controlled-vocabulary/reorder/" + order1 + "/" + order2, "");

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");

        assertEquals("SUCCESS", meta.get("type"));

        assertEquals(order1, controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME2).getPosition());
        assertEquals(order2, controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1).getPosition());
    }

    @Test
    public void testSortControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        String responseJson = StompRequest("/settings/controlled-vocabulary/sort/name", "");

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");

        assertEquals("SUCCESS", meta.get("type"));

        assertEquals(Long.valueOf(1), controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME3).getPosition());
        assertEquals(Long.valueOf(2), controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME2).getPosition());
        assertEquals(Long.valueOf(3), controlledVocabularyRepo.findByName(TEST_CONTROLLED_VOCABULARY_NAME1).getPosition());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExportControlledVocabulary() throws InterruptedException, JsonParseException, JsonMappingException, IOException {

        addVocabularyWords();

        String responseJson = StompRequest("/settings/controlled-vocabulary/export/" + TEST_CONTROLLED_VOCABULARY_NAME1, "");

        Map<String, Object> responseObject = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
        });

        Map<String, String> meta = (Map<String, String>) responseObject.get("meta");

        assertEquals("SUCCESS", meta.get("type"));

        Map<String, Object> payload = (Map<String, Object>) responseObject.get("payload");

        List<List<String>> rows = (List<List<String>>) ((Map<String, Object>) payload.get("HashMap")).get("rows");

        List<String> row = rows.get(0);

        assertEquals(row.get(0), TEST_VOCABULARY_WORD_NAME1);
        assertEquals(row.get(1), TEST_VOCABULARY_WORD_DEFINITION1);
        assertEquals(row.get(2), TEST_VOCABULARY_WORD_IDENTIFIER1);

        row = rows.get(1);

        assertEquals(row.get(0), TEST_VOCABULARY_WORD_NAME2);
        assertEquals(row.get(1), TEST_VOCABULARY_WORD_DEFINITION2);
        assertEquals(row.get(2), TEST_VOCABULARY_WORD_IDENTIFIER2);

        row = rows.get(2);

        assertEquals(row.get(0), TEST_VOCABULARY_WORD_NAME3);
        assertEquals(row.get(1), TEST_VOCABULARY_WORD_DEFINITION3);
        assertEquals(row.get(2), TEST_VOCABULARY_WORD_IDENTIFIER3);
    }

    @Test
    public void testImportControlledVocabularyStatus() {

    }

    @Test
    public void testCancelImportControlledVocabulary() {

    }

    @Test
    public void testCompareControlledVocabulary() {

    }

    @Test
    public void testImportControlledVocabulary() {

    }

    @Test
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
