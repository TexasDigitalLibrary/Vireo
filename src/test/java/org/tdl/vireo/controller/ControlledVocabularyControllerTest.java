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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ControlledVocabularyControllerTest extends AbstractControllerTest {

    @Mock
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @InjectMocks
    private ControlledVocabularyController controlledVocabularyController;

    private ControlledVocabulary mockControlledVocabulary1;
    private ControlledVocabulary mockControlledVocabulary2;

    private static List<ControlledVocabulary> mockControlledVocabularys;

    @BeforeEach
    public void setup() {
        mockControlledVocabulary1 = new ControlledVocabulary("ControlledVocabulary 1");
        mockControlledVocabulary1.setId(1L);

        mockControlledVocabulary2 = new ControlledVocabulary("ControlledVocabulary 2");
        mockControlledVocabulary2.setId(2L);

        mockControlledVocabularys = new ArrayList<ControlledVocabulary>(Arrays.asList(new ControlledVocabulary[] { mockControlledVocabulary1 }));
    }

    @Test
    public void testAllControlledVocabularys() {
        when(controlledVocabularyRepo.findAllByOrderByPositionAsc()).thenReturn(mockControlledVocabularys);

        ApiResponse response = controlledVocabularyController.getAllControlledVocabulary();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<ControlledVocabulary>");
        assertEquals(mockControlledVocabularys.size(), list.size());
    }

    @Test
    public void testGetControlledVocabulary() {
        when(controlledVocabularyRepo.findByName(any(String.class))).thenReturn(mockControlledVocabulary1);

        ApiResponse response = controlledVocabularyController.getControlledVocabularyByName("name");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary controlledVocabulary = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(mockControlledVocabulary1.getId(), controlledVocabulary.getId());
    }

    @Test
    public void testCreateControlledVocabulary() {
        when(controlledVocabularyRepo.create(any(String.class))).thenReturn(mockControlledVocabulary2);

        ApiResponse response = controlledVocabularyController.createControlledVocabulary(mockControlledVocabulary1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary controlledVocabulary = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(mockControlledVocabulary2.getId(), controlledVocabulary.getId());
    }

    @Test
    public void testUpdateControlledVocabulary() {
        when(controlledVocabularyRepo.findById(any(Long.class))).thenReturn(Optional.of(mockControlledVocabulary2));
        when(controlledVocabularyRepo.update(any(ControlledVocabulary.class))).thenReturn(mockControlledVocabulary2);

        ApiResponse response = controlledVocabularyController.updateControlledVocabulary(mockControlledVocabulary1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ControlledVocabulary controlledVocabulary = (ControlledVocabulary) response.getPayload().get("ControlledVocabulary");
        assertEquals(mockControlledVocabulary2.getId(), controlledVocabulary.getId());
    }

    @Test
    public void testRemoveControlledVocabulary() {
        doNothing().when(controlledVocabularyRepo).remove(any(ControlledVocabulary.class));

        ApiResponse response = controlledVocabularyController.removeControlledVocabulary(mockControlledVocabulary1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyRepo, times(1)).remove(any(ControlledVocabulary.class));
    }

    @Test
    public void testReorderControlledVocabularys() {
        doNothing().when(controlledVocabularyRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = controlledVocabularyController.reorderControlledVocabulary(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortControlledVocabularys() {
        doNothing().when(controlledVocabularyRepo).sort(any(String.class));

        ApiResponse response = controlledVocabularyController.sortControlledVocabulary("column");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(controlledVocabularyRepo, times(1)).sort(any(String.class));
    }

}
