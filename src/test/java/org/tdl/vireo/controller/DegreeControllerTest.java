package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
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
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.service.ProquestCodesService;

@ActiveProfiles("test")
public class DegreeControllerTest extends AbstractControllerTest {

    @Mock
    private DegreeRepo degreeRepo;

    @Mock
    private ProquestCodesService proquestCodesService;

    @InjectMocks
    private DegreeController degreeController;

    private Degree degree1;
    private Degree degree2;

    private DegreeLevel degreeLevel1;
    private DegreeLevel degreeLevel2;

    private static List<Degree> degrees;

    @BeforeEach
    public void setup() {
        degreeLevel1 = new DegreeLevel("1");
        degreeLevel2 = new DegreeLevel("2");
        degree1 = new Degree("Degree 1", degreeLevel1, "Proquest 1");
        degree2 = new Degree("Degree 2", degreeLevel2, "Proquest 2");

        degreeLevel1.setId(1L);
        degreeLevel2.setId(2L);
        degree1.setId(1L);
        degree2.setId(2L);

        degree1.setPosition(1L);
        degree2.setPosition(2L);

        degrees = new ArrayList<Degree>(Arrays.asList(new Degree[] { degree1 }));
    }

    @Test
    public void testAllDegrees() {
        when(degreeRepo.findAllByOrderByPositionAsc()).thenReturn(degrees);

        ApiResponse response = degreeController.allDegrees();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<Degree>");
        assertEquals(degrees.size(), list.size());
    }

    @Test
    public void testCreateDegree() {
        when(degreeRepo.create(any(String.class), any(DegreeLevel.class))).thenReturn(degree2);

        ApiResponse response = degreeController.createDegree(degree1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Degree degree = (Degree) response.getPayload().get("Degree");
        assertEquals(degree2.getId(), degree.getId());
    }

    @Test
    public void testUpdateDegree() {
        when(degreeRepo.update(any(Degree.class))).thenReturn(degree2);

        ApiResponse response = degreeController.updateDegree(degree1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Degree degree = (Degree) response.getPayload().get("Degree");
        assertEquals(degree2.getId(), degree.getId());
    }

    @Test
    public void testRemoveDegree() {
        doNothing().when(degreeRepo).remove(any(Degree.class));

        ApiResponse response = degreeController.removeDegree(degree1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(degreeRepo).remove(any(Degree.class));
    }

    @Test
    public void testRemoveAllDegrees() {
        doNothing().when(degreeRepo).deleteAll();
        doNothing().when(degreeRepo).broadcast(anyList());

        ApiResponse response = degreeController.removeAllDegrees();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(degreeRepo).deleteAll();
        verify(degreeRepo).broadcast(anyList());
    }

    @Test
    public void testReorderDegrees() {
        doNothing().when(degreeRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = degreeController.reorderDegrees(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(degreeRepo).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortDegrees() {
        doNothing().when(degreeRepo).sort(any(String.class));

        ApiResponse response = degreeController.sortDegrees("column");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(degreeRepo).sort(any(String.class));
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
