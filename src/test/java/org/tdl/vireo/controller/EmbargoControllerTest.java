package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.repo.EmbargoRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EmbargoControllerTest extends AbstractControllerTest {

    protected static final String EMBARGO_NAME = "Embargo Name";
    protected static final String EMBARGO_DESCRIPTION = "Embargo Description";
    protected static final Integer EMBARGO_DURATION = 12;
    protected static final EmbargoGuarantor EMBARGO_GUARANTOR = EmbargoGuarantor.DEFAULT;
    protected static final Boolean EMBARGO_IS_ACTIVE = true;

    @Mock
    private EmbargoRepo embargoRepo;

    @InjectMocks
    private EmbargoController embargoController;

    private Embargo mockEmbargo;

    private static List<Embargo> mockEmbargoes;

    @BeforeEach
    public void setup() {
        mockEmbargo = new Embargo(EMBARGO_NAME, EMBARGO_DESCRIPTION, EMBARGO_DURATION, EMBARGO_GUARANTOR, EMBARGO_IS_ACTIVE);
        mockEmbargo.setId(1L);

        mockEmbargoes = new ArrayList<Embargo>(Arrays.asList(new Embargo[] { mockEmbargo }));
    }

    @Test
    public void testGetEmbargo() {
        when(embargoRepo.findAllByOrderByGuarantorAscPositionAsc()).thenReturn(mockEmbargoes);

        ApiResponse response = embargoController.getEmbargoes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> embargoes = (ArrayList<?>) response.getPayload().get("ArrayList<Embargo>");
        assertEquals(mockEmbargoes.size(), embargoes.size());
    }

    @Test
    public void testCreateEmbargo() {
        when(embargoRepo.create(any(String.class), any(String.class), any(Integer.class), any(EmbargoGuarantor.class), any(Boolean.class))).thenReturn(mockEmbargo);

        ApiResponse response = embargoController.createEmbargo(mockEmbargo);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Embargo embargo = (Embargo) response.getPayload().get("Embargo");
        assertEquals(mockEmbargo, embargo);
    }

    @Test
    public void testUpdateEmbargo() {
        when(embargoRepo.update(any(Embargo.class))).thenReturn(mockEmbargo);

        ApiResponse response = embargoController.updateEmbargo(mockEmbargo);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Embargo embargo = (Embargo) response.getPayload().get("Embargo");
        assertEquals(mockEmbargo, embargo);
    }

    @Test
    public void testRemoveEmbargo() {
        doNothing().when(embargoRepo).remove(any(Embargo.class));

        ApiResponse response = embargoController.removeEmbargo(mockEmbargo);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(embargoRepo, times(1)).remove(any(Embargo.class));
    }

    @Test
    public void testActivateEmbargo() {
        Embargo updatedEmbargo = mockEmbargo;
        updatedEmbargo.isActive(true);
        mockEmbargo.isActive(false);

        when(embargoRepo.findById(any(Long.class))).thenReturn(Optional.of(mockEmbargo));
        when(embargoRepo.update(any(Embargo.class))).thenReturn(updatedEmbargo);

        ApiResponse response = embargoController.activateEmbargo(mockEmbargo.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Embargo embargo = (Embargo) response.getPayload().get("Embargo");
        assertEquals(updatedEmbargo, embargo);
    }

    @Test
    public void testDeactivateEmbargo() {
        Embargo updatedEmbargo = mockEmbargo;
        updatedEmbargo.isActive(false);
        mockEmbargo.isActive(true);

        when(embargoRepo.findById(any(Long.class))).thenReturn(Optional.of(mockEmbargo));
        when(embargoRepo.update(any(Embargo.class))).thenReturn(updatedEmbargo);

        ApiResponse response = embargoController.deactivateEmbargo(mockEmbargo.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Embargo embargo = (Embargo) response.getPayload().get("Embargo");
        assertEquals(updatedEmbargo, embargo);
    }

    @Test
    public void testReorderEmbargoes() {
        doNothing().when(embargoRepo).reorder(any(Long.class), any(Long.class), any(EmbargoGuarantor.class));

        ApiResponse response = embargoController.reorderEmbargoes(EmbargoGuarantor.DEFAULT.toString(), 1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(embargoRepo, times(1)).reorder(any(Long.class), any(Long.class), any(EmbargoGuarantor.class));
    }

    @Test
    public void testSortEmbargoes() {
        doNothing().when(embargoRepo).sort(any(String.class), any(EmbargoGuarantor.class));

        ApiResponse response = embargoController.sortEmbargoes(EmbargoGuarantor.DEFAULT.toString(), "test");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(embargoRepo, times(1)).sort(any(String.class), any(EmbargoGuarantor.class));
    }

}
