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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

@ActiveProfiles("test")
public class CustomActionControllerTest extends AbstractControllerTest {

    @Mock
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @InjectMocks
    private CustomActionSettingsController customActionDefinitionController;

    private CustomActionDefinition mockCustomActionDefinition1;
    private CustomActionDefinition mockCustomActionDefinition2;

    private static List<CustomActionDefinition> mockCustomActionDefinitions;

    @BeforeEach
    public void setup() {
        mockCustomActionDefinition1 = new CustomActionDefinition("CustomActionDefinition 1", true);
        mockCustomActionDefinition1.setId(1L);

        mockCustomActionDefinition2 = new CustomActionDefinition("CustomActionDefinition 2", false);
        mockCustomActionDefinition2.setId(2L);

        mockCustomActionDefinitions = new ArrayList<CustomActionDefinition>(Arrays.asList(new CustomActionDefinition[] { mockCustomActionDefinition1 }));
    }

    @Test
    public void testAllCustomActionDefinitions() {
        when(customActionDefinitionRepo.findAllByOrderByPositionAsc()).thenReturn(mockCustomActionDefinitions);

        ApiResponse response = customActionDefinitionController.getCustomActions();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<CustomActionDefinition>");
        assertEquals(mockCustomActionDefinitions.size(), list.size());
    }

    @Test
    public void testCreateCustomActionDefinition() {
        when(customActionDefinitionRepo.create(any(String.class), any(Boolean.class))).thenReturn(mockCustomActionDefinition2);

        ApiResponse response = customActionDefinitionController.createCustomAction(mockCustomActionDefinition1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        CustomActionDefinition customActionDefinition = (CustomActionDefinition) response.getPayload().get("CustomActionDefinition");
        assertEquals(mockCustomActionDefinition2.getId(), customActionDefinition.getId());
    }

    @Test
    public void testUpdateCustomActionDefinition() {
        when(customActionDefinitionRepo.update(any(CustomActionDefinition.class))).thenReturn(mockCustomActionDefinition2);

        ApiResponse response = customActionDefinitionController.updateCustomAction(mockCustomActionDefinition1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        CustomActionDefinition customActionDefinition = (CustomActionDefinition) response.getPayload().get("CustomActionDefinition");
        assertEquals(mockCustomActionDefinition2.getId(), customActionDefinition.getId());
    }

    @Test
    public void testRemoveCustomActionDefinition() {
        doNothing().when(customActionDefinitionRepo).remove(any(CustomActionDefinition.class));

        ApiResponse response = customActionDefinitionController.removeCustomAction(mockCustomActionDefinition1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(customActionDefinitionRepo, times(1)).remove(any(CustomActionDefinition.class));
    }

    @Test
    public void testReorderDegrees() {
        doNothing().when(customActionDefinitionRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = customActionDefinitionController.reorderCustomActions(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(customActionDefinitionRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

}
