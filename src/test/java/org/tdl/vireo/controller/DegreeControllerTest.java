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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.service.ProquestCodesService;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DegreeControllerTest extends AbstractControllerTest {

    @Mock
    private DegreeRepo degreeRepo;

    @Mock
    private ProquestCodesService proquestCodesService;

    @InjectMocks
    private DegreeController degreeController;

    private Degree mockDegree1;
    private Degree mockDegree2;

    private DegreeLevel mockDegreeLevel1;
    private DegreeLevel mockDegreeLevel2;

    private static List<Degree> mockDegrees;

    @BeforeEach
    public void setup() {
        mockDegreeLevel1 = new DegreeLevel("1");
        mockDegreeLevel1.setId(1L);

        mockDegreeLevel2 = new DegreeLevel("2");
        mockDegreeLevel2.setId(2L);

        mockDegree1 = new Degree("Degree 1", mockDegreeLevel1, "Proquest 1");
        mockDegree1.setId(1L);
        mockDegree1.setPosition(1L);

        mockDegree2 = new Degree("Degree 2", mockDegreeLevel2, "Proquest 2");
        mockDegree2.setId(2L);
        mockDegree2.setPosition(2L);

        mockDegrees = new ArrayList<Degree>(Arrays.asList(new Degree[] { mockDegree1 }));
    }

    @Test
    public void testAllDegrees() {
        when(degreeRepo.findAllByOrderByPositionAsc()).thenReturn(mockDegrees);

        ApiResponse response = degreeController.allDegrees();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<Degree>");
        assertEquals(mockDegrees.size(), list.size());
    }

    @Test
    public void testCreateDegree() {
        when(degreeRepo.create(any(String.class), any(DegreeLevel.class))).thenReturn(mockDegree2);

        ApiResponse response = degreeController.createDegree(mockDegree1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Degree degree = (Degree) response.getPayload().get("Degree");
        assertEquals(mockDegree2.getId(), degree.getId());
    }

    @Test
    public void testUpdateDegree() {
        when(degreeRepo.update(any(Degree.class))).thenReturn(mockDegree2);

        ApiResponse response = degreeController.updateDegree(mockDegree1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Degree degree = (Degree) response.getPayload().get("Degree");
        assertEquals(mockDegree2.getId(), degree.getId());
    }

    @Test
    public void testRemoveDegree() {
        doNothing().when(degreeRepo).remove(any(Degree.class));

        ApiResponse response = degreeController.removeDegree(mockDegree1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(degreeRepo, times(1)).remove(any(Degree.class));
    }

    @Test
    public void testReorderDegrees() {
        doNothing().when(degreeRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = degreeController.reorderDegrees(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(degreeRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortDegrees() {
        doNothing().when(degreeRepo).sort(any(String.class));

        ApiResponse response = degreeController.sortDegrees("column");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(degreeRepo, times(1)).sort(any(String.class));
    }

    @Test
    public void testGetProquestLanguageCodes() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");

        when(proquestCodesService.getCodes(any(String.class))).thenReturn(map);

        ApiResponse response = degreeController.getProquestLanguageCodes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        @SuppressWarnings("unchecked")
        Map<String, String> mapReturned = (HashMap<String, String>) response.getPayload().get("HashMap");
        assertEquals(mapReturned.get("a"), map.get("a"));
    }

}
