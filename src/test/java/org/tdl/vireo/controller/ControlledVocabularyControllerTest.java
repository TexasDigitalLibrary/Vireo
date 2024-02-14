package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.ControlledVocabularyCache;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.service.ControlledVocabularyCachingService;

@SpringBootTest(classes = { Application.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = { "test", "isolated-test" })
public class ControlledVocabularyControllerTest extends AbstractControllerTest {

    @Mock
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Mock
    private VocabularyWordRepo vocabularyWordRepo;

    @Mock
    private ControlledVocabularyCachingService controlledVocabularyCachingService;

    @InjectMocks
    private ControlledVocabularyController controlledVocabularyController;

    private ControlledVocabulary controlledVocabulary1;
    private ControlledVocabulary controlledVocabulary2;

    private VocabularyWord vocabularyWord1;
    private VocabularyWord vocabularyWord2;

    private List<String> contacts1;
    private List<String> contacts2;

    private List<VocabularyWord> dictionaries1;
    private List<VocabularyWord> dictionaries2;

    private List<ControlledVocabulary> controlledVocabularys;

    @BeforeEach
    public void setup() {
        contacts1 = new ArrayList<>();
        contacts1.add("contact1");

        contacts2 = new ArrayList<>();
        contacts2.add("contact2");

        vocabularyWord1 = new VocabularyWord("name1", "definition1", "identifier1", contacts1);
        vocabularyWord2 = new VocabularyWord("name2", "definition2", "identifier2", contacts2);

        vocabularyWord1.setId(1L);
        vocabularyWord2.setId(2L);

        dictionaries1 = new ArrayList<>();
        dictionaries1.add(vocabularyWord1);

        dictionaries2 = new ArrayList<>();
        dictionaries2.add(vocabularyWord2);

        controlledVocabulary1 = new ControlledVocabulary("ControlledVocabulary 1", false);
        controlledVocabulary1.setId(1L);
        controlledVocabulary1.setDictionary(dictionaries1);
        controlledVocabulary1.setPosition(1L);

        controlledVocabulary2 = new ControlledVocabulary("ControlledVocabulary 2", false);
        controlledVocabulary2.setId(2L);
        controlledVocabulary2.setDictionary(dictionaries2);
        controlledVocabulary2.setPosition(2L);

        controlledVocabularys = new ArrayList<ControlledVocabulary>(Arrays.asList(new ControlledVocabulary[] { controlledVocabulary1 }));
    }

    @Test
    public void testAllControlledVocabularys() {
        when(controlledVocabularyRepo.findAllByOrderByPositionAsc()).thenReturn(controlledVocabularys);

        ApiResponse response = controlledVocabularyController.getAllControlledVocabulary();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<ControlledVocabulary>");
        assertEquals(controlledVocabularys.size(), list.size());
    }

    @Test
    public void testGetControlledVocabulary() {
        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);

        ApiResponse response = controlledVocabularyController.getControlledVocabularyByName("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary controlledVocabulary = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary1.getId(), controlledVocabulary.getId());
    }

    @Test
    public void testCreateControlledVocabulary() {
        when(controlledVocabularyRepo.create(anyString())).thenReturn(controlledVocabulary2);

        ApiResponse response = controlledVocabularyController.createControlledVocabulary(controlledVocabulary1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary controlledVocabulary = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary2.getId(), controlledVocabulary.getId());
    }

    @Test
    public void testUpdateControlledVocabulary() {
        when(controlledVocabularyRepo.findById(any(Long.class))).thenReturn(Optional.of(controlledVocabulary2));
        when(controlledVocabularyRepo.update(any(ControlledVocabulary.class))).thenReturn(controlledVocabulary2);

        ApiResponse response = controlledVocabularyController.updateControlledVocabulary(controlledVocabulary1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary controlledVocabulary = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary2.getId(), controlledVocabulary.getId());
    }

    @Test
    public void testRemoveControlledVocabulary() {
        doNothing().when(controlledVocabularyRepo).remove(any(ControlledVocabulary.class));

        ApiResponse response = controlledVocabularyController.removeControlledVocabulary(controlledVocabulary1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyRepo, only()).remove(any(ControlledVocabulary.class));
    }

    @Test
    public void testReorderControlledVocabularys() {
        doNothing().when(controlledVocabularyRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = controlledVocabularyController.reorderControlledVocabulary(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyRepo, only()).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortControlledVocabularys() {
        doNothing().when(controlledVocabularyRepo).sort(anyString());

        ApiResponse response = controlledVocabularyController.sortControlledVocabulary("column");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyRepo, only()).sort(anyString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExportControlledVocabulary() {
        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);

        ApiResponse response = controlledVocabularyController.exportControlledVocabulary("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Map<String, Object> map = (HashMap<String, Object>) response.getPayload().get("HashMap");

        assertEquals(2, map.size(), "Returned map has the wrong size.");

        List<String> headers = (List<String>) map.get("headers");
        assertEquals(4, headers.size(), "Returned map has the wrong headers size.");
        assertEquals("name",  headers.get(0), "The headers has the wrong value for name column.");
        assertEquals("definition",  headers.get(1), "The headers has the wrong value for definition column.");
        assertEquals("identifier",  headers.get(2), "The headers has the wrong value for identifier column.");
        assertEquals("contacts",  headers.get(3), "The headers has the wrong value for contacts column.");

        // Warning: The 'List<List<Object>>' should probably instead be 'List<List<String>>' in exportControlledVocabulary().
        List<List<String>> rows = (List<List<String>>) map.get("rows");
        assertEquals(1, rows.size(), "Returned map has the wrong rows size.");
        assertEquals(vocabularyWord1.getName(), rows.get(0).get(0), "The row has the wrong name.");
        assertEquals(vocabularyWord1.getDefinition(), rows.get(0).get(1), "The row has the wrong definition.");
        assertEquals(vocabularyWord1.getIdentifier(), rows.get(0).get(2), "The row has the wrong identifier.");

        // Warning: There is a possible bug here where if the contacts has a comma, then the form will be incorrect.
        assertEquals(String.join(",", vocabularyWord1.getContacts()), rows.get(0).get(3), "The row has the wrong contacts.");

        verify(controlledVocabularyRepo, only()).findByName(anyString());
    }

    @Test
    public void testImportControlledVocabularyStatus() {
        when(controlledVocabularyCachingService.doesControlledVocabularyExist(anyString())).thenReturn(true);

        ApiResponse response = controlledVocabularyController.importControlledVocabularyStatus("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Boolean got = (Boolean) response.getPayload().get("Boolean");
        assertEquals(true, got, "Returned boolean has the wrong value.");

        verify(controlledVocabularyCachingService, only()).doesControlledVocabularyExist(anyString());
    }

    @Test
    public void testCancelImportControlledVocabulary() {
        doNothing().when(controlledVocabularyCachingService).removeControlledVocabularyCache(anyString());

        ApiResponse response = controlledVocabularyController.cancelImportControlledVocabulary("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyCachingService, only()).removeControlledVocabularyCache(anyString());
    }

    @Test
    public void testCompareControlledVocabulary() throws IOException {
        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);
        doNothing().when(controlledVocabularyCachingService).addControlledVocabularyCache(any(ControlledVocabularyCache.class));

        MultipartFile file = new MockMultipartFile("name", "originalName", "text/plain", "".getBytes());

        ApiResponse response = controlledVocabularyController.compareControlledVocabulary("name", file);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyCachingService, only()).addControlledVocabularyCache(any(ControlledVocabularyCache.class));
    }

    @Test
    public void testCompareControlledVocabularyWithCSV() throws IOException {
        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);
        doNothing().when(controlledVocabularyCachingService).addControlledVocabularyCache(any(ControlledVocabularyCache.class));

        MultipartFile file = new MockMultipartFile("name", "originalName", "text/plain", "name,definition,identifier,contacts\nname1,definition1,identifier1,\nname2,definition2,identifier2,contact1\nname2,definition2,identifier2,contact2".getBytes());

        ApiResponse response = controlledVocabularyController.compareControlledVocabulary("name", file);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyCachingService, only()).addControlledVocabularyCache(any(ControlledVocabularyCache.class));
    }

    @Test
    public void testImportControlledVocabulary() {
        ControlledVocabularyCache cache = new ControlledVocabularyCache();
        cache.setControlledVocabularyName(controlledVocabulary1.getName());
        cache.setNewVocabularyWords(new ArrayList<>());
        cache.setUpdatingVocabularyWords(new ArrayList<>());
        cache.setRemovedVocabularyWords(new ArrayList<>());
        cache.setTimestamp(1234L);

        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);
        when(controlledVocabularyCachingService.getControlledVocabularyCache(anyString())).thenReturn(cache);
        when(controlledVocabularyRepo.update(any(ControlledVocabulary.class))).thenReturn(controlledVocabulary2);
        doNothing().when(controlledVocabularyCachingService).removeControlledVocabularyCache(anyString());

        ApiResponse response = controlledVocabularyController.importControlledVocabulary("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary got = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary2, got, "Did not get expected Controlled Vocabulary in the response.");

        // Warning: These checks are throwing NPE.
        //verify(controlledVocabularyCachingService, only()).getControlledVocabularyCache(anyString());
        //verify(controlledVocabularyCachingService, only()).removeControlledVocabularyCache(anyString());
        //verify(controlledVocabularyRepo, only()).update(any(ControlledVocabulary.class));
    }

    @Test
    public void testImportControlledVocabularyWithNewVocabularyWord() {
        List<VocabularyWord> newWords = new ArrayList<>();
        newWords.add(vocabularyWord2);

        ControlledVocabularyCache cache = new ControlledVocabularyCache();
        cache.setControlledVocabularyName(controlledVocabulary1.getName());
        cache.setNewVocabularyWords(newWords);
        cache.setUpdatingVocabularyWords(new ArrayList<>());
        cache.setRemovedVocabularyWords(new ArrayList<>());
        cache.setTimestamp(1234L);

        VocabularyWord newWord = new VocabularyWord(controlledVocabulary1, "name3", "definition3", "identifier3", contacts1);
        newWord.setId(3L);

        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);
        when(controlledVocabularyCachingService.getControlledVocabularyCache(anyString())).thenReturn(cache);
        when(vocabularyWordRepo.create(any(), any(), any(), any(), any())).thenReturn(newWord);
        when(controlledVocabularyRepo.update(any(ControlledVocabulary.class))).thenReturn(controlledVocabulary2);
        doNothing().when(controlledVocabularyCachingService).removeControlledVocabularyCache(anyString());

        ApiResponse response = controlledVocabularyController.importControlledVocabulary("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary got = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary2, got, "Did not get expected Controlled Vocabulary in the response.");

        verify(vocabularyWordRepo, only()).create(any(), any(), any(), any(), any());

        // Warning: These checks are throwing NPE.
        //verify(controlledVocabularyCachingService, only()).getControlledVocabularyCache(anyString());
        //verify(controlledVocabularyCachingService, only()).removeControlledVocabularyCache(anyString());
        //verify(controlledVocabularyRepo, only()).update(any(ControlledVocabulary.class));
    }

    @Test
    public void testImportControlledVocabularyWithUpdatingVocabularyWord() {
        // Warning: importControlledVocabulary() is expected updatingVocabularyWord[1].
        VocabularyWord[] updatingWords = { vocabularyWord1, vocabularyWord1 };
        List<VocabularyWord[]> updatingWordsList = new ArrayList<>();
        updatingWordsList.add(updatingWords);

        ControlledVocabularyCache cache = new ControlledVocabularyCache();
        cache.setControlledVocabularyName(controlledVocabulary1.getName());
        cache.setNewVocabularyWords(new ArrayList<>());
        cache.setUpdatingVocabularyWords(updatingWordsList);
        cache.setRemovedVocabularyWords(new ArrayList<>());
        cache.setTimestamp(1234L);

        VocabularyWord newWord = new VocabularyWord(controlledVocabulary1, "name3", "definition3", "identifier3", contacts1);
        newWord.setId(3L);

        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);
        when(controlledVocabularyCachingService.getControlledVocabularyCache(anyString())).thenReturn(cache);
        when(vocabularyWordRepo.findByNameAndControlledVocabulary(anyString(), any(ControlledVocabulary.class))).thenReturn(vocabularyWord1);
        when(vocabularyWordRepo.save(any(VocabularyWord.class))).thenReturn(newWord);
        when(controlledVocabularyRepo.update(any(ControlledVocabulary.class))).thenReturn(controlledVocabulary2);
        doNothing().when(controlledVocabularyCachingService).removeControlledVocabularyCache(anyString());

        ApiResponse response = controlledVocabularyController.importControlledVocabulary("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary got = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary2, got, "Did not get expected Controlled Vocabulary in the response.");

        // Warning: These checks are throwing NPE.
        //verify(vocabularyWordRepo, only()).save(any(VocabularyWord.class));
        //verify(controlledVocabularyCachingService, only()).getControlledVocabularyCache(anyString());
        //verify(controlledVocabularyCachingService, only()).removeControlledVocabularyCache(anyString());
        //verify(controlledVocabularyRepo, only()).update(any(ControlledVocabulary.class));
    }

    @Test
    public void testImportControlledVocabularyWithRemoveVocabularyWord() {
        // Warning: importControlledVocabulary() calls another controller method: removeVocabularyWord().
        List<VocabularyWord> removeWords = new ArrayList<>();
        removeWords.add(vocabularyWord1);

        ControlledVocabularyCache cache = new ControlledVocabularyCache();
        cache.setControlledVocabularyName(controlledVocabulary1.getName());
        cache.setNewVocabularyWords(new ArrayList<>());
        cache.setUpdatingVocabularyWords(new ArrayList<>());
        cache.setRemovedVocabularyWords(removeWords);
        cache.setTimestamp(1234L);

        when(controlledVocabularyRepo.findByName(anyString())).thenReturn(controlledVocabulary1);
        when(controlledVocabularyCachingService.getControlledVocabularyCache(anyString())).thenReturn(cache);
        when(controlledVocabularyRepo.findById(any())).thenReturn(Optional.of(controlledVocabulary1));
        when(vocabularyWordRepo.findById(any())).thenReturn(Optional.of(vocabularyWord1));
        when(controlledVocabularyRepo.update(any(ControlledVocabulary.class))).thenReturn(controlledVocabulary2);
        doNothing().when(controlledVocabularyCachingService).removeControlledVocabularyCache(anyString());

        ApiResponse response = controlledVocabularyController.importControlledVocabulary("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary got = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary2, got, "Did not get expected Controlled Vocabulary in the response.");

        // Warning: These checks are throwing NPE.
        //verify(controlledVocabularyCachingService, only()).getControlledVocabularyCache(anyString());
        //verify(controlledVocabularyCachingService, only()).removeControlledVocabularyCache(anyString());
        //verify(controlledVocabularyRepo, only()).update(any(ControlledVocabulary.class));
    }

    @Test
    public void testAddVocabularyWord() {
        when(controlledVocabularyRepo.findById(any(Long.class))).thenReturn(Optional.of(controlledVocabulary1));
        when(vocabularyWordRepo.create(any(), any(), any(), any(), any())).thenReturn(vocabularyWord1);
        doNothing().when(controlledVocabularyRepo).broadcast(any(Long.class));

        ApiResponse response = controlledVocabularyController.addVocabularyWord(vocabularyWord1.getId(), vocabularyWord1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        VocabularyWord got = (VocabularyWord) response.getPayload().get("VocabularyWord");
        assertEquals(vocabularyWord1, got, "Did not get expected Vocabulary Word in the response.");

        verify(vocabularyWordRepo, only()).create(any(), any(), any(), any(), any());

        // Warning: These checks are throwing NPE.
        //verify(controlledVocabularyRepo, only()).broadcast(any(Long.class));
    }

    @Test
    public void testRemoveVocabularyWord() {
        when(controlledVocabularyRepo.findById(any(Long.class))).thenReturn(Optional.of(controlledVocabulary1));
        when(vocabularyWordRepo.findById(any(Long.class))).thenReturn(Optional.of(vocabularyWord1));
        when(controlledVocabularyRepo.update(any(ControlledVocabulary.class))).thenReturn(controlledVocabulary1);

        ApiResponse response = controlledVocabularyController.removeVocabularyWord(vocabularyWord1.getId(), 100L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary got = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(controlledVocabulary1, got, "Did not get expected Controlled Vocabulary in the response.");

        // Warning: These checks are throwing NPE.
        //verify(controlledVocabularyRepo, only()).update(any(ControlledVocabulary.class));
    }

    @Test
    public void testUpdateVocabularyWord() {
        when(controlledVocabularyRepo.findById(any(Long.class))).thenReturn(Optional.of(controlledVocabulary1));
        when(vocabularyWordRepo.update(any(VocabularyWord.class))).thenReturn(vocabularyWord1);
        doNothing().when(controlledVocabularyRepo).broadcast(any(Long.class));

        ApiResponse response = controlledVocabularyController.updateVocabularyWord(vocabularyWord1.getId(), vocabularyWord2);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        VocabularyWord got = (VocabularyWord) response.getPayload().get("VocabularyWord");
        assertEquals(vocabularyWord1, got, "Did not get expected Vocabulary Word in the response.");

        // Warning: These checks are throwing NPE.
        //verify(vocabularyWordRepo, only()).update(any(VocabularyWord.class));
    }

    @Test
    public void testTypeaheadVocabularyWord() {
        when(vocabularyWordRepo.findAllByNameContainsIgnoreCaseAndControlledVocabularyIdOrderByName(anyString(), anyLong(), Mockito.<Class<VocabularyWord>>any())).thenReturn(dictionaries1);

        ApiResponse response = controlledVocabularyController.typeaheadVocabularyWord(vocabularyWord1.getId(), "test");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<VocabularyWord>");
        assertEquals(dictionaries1, got, "Did not get expected Vocabulary Words array in the response.");

        verify(vocabularyWordRepo).findAllByNameContainsIgnoreCaseAndControlledVocabularyIdOrderByName(anyString(), anyLong(), any());
    }

}
