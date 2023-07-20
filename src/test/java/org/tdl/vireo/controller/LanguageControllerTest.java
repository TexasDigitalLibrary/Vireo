package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.service.ProquestCodesService;

@ActiveProfiles("test")
public class LanguageControllerTest extends AbstractControllerTest {

    @Mock
    private LanguageRepo languageRepo;

    @Mock
    private ProquestCodesService proquestCodesService;

    @InjectMocks
    private LanguageController languageController;

    private Language mockLanguage1;
    private Language mockLanguage2;

    private static List<Language> mockLanguages;

    @BeforeEach
    public void setup() {
        mockLanguage1 = new Language("Language 1");
        mockLanguage1.setId(1L);
        mockLanguage1.setPosition(1L);

        mockLanguage2 = new Language("Language 2");
        mockLanguage2.setId(2L);
        mockLanguage2.setPosition(2L);

        mockLanguages = new ArrayList<Language>(Arrays.asList(new Language[] { mockLanguage1 }));
    }

    @Test
    public void testAllLanguages() {
        when(languageRepo.findAllByOrderByPositionAsc()).thenReturn(mockLanguages);

        ApiResponse response = languageController.getAllLanguages();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<Language>");
        assertEquals(mockLanguages.size(), list.size());
    }

    @Test
    public void testCreateLanguage() {
        when(languageRepo.create(any(String.class))).thenReturn(mockLanguage2);

        ApiResponse response = languageController.createLanguage(mockLanguage1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Language language = (Language) response.getPayload().get("Language");
        assertEquals(mockLanguage2.getId(), language.getId());
    }

    @Test
    public void testUpdateLanguage() {
        when(languageRepo.update(any(Language.class))).thenReturn(mockLanguage2);

        ApiResponse response = languageController.updateLanguage(mockLanguage1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Language language = (Language) response.getPayload().get("Language");
        assertEquals(mockLanguage2.getId(), language.getId());
    }

    @Test
    public void testRemoveLanguage() {
        doNothing().when(languageRepo).remove(any(Language.class));

        ApiResponse response = languageController.removeLanguage(mockLanguage1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(languageRepo, times(1)).remove(any(Language.class));
    }

    @Test
    public void testReorderLanguages() {
        doNothing().when(languageRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = languageController.reorderLanguage(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(languageRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortLanguages() {
        doNothing().when(languageRepo).sort(any(String.class));

        ApiResponse response = languageController.sortLanguage("column");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(languageRepo, times(1)).sort(any(String.class));
    }

    @Test
    public void testGetProquestLanguageCodes() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");

        when(proquestCodesService.getCodes(any(String.class))).thenReturn(map);

        ApiResponse response = languageController.getProquestLanguageCodes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        @SuppressWarnings("unchecked")
        Map<String, String> mapReturned = (HashMap<String, String>) response.getPayload().get("HashMap");
        assertEquals(mapReturned.get("a"), map.get("a"));
    }

}
