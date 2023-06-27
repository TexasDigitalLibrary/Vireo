package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class GraduationMonthControllerTest extends AbstractControllerTest {

    @Mock
    private GraduationMonthRepo graduationMonthRepo;

    @InjectMocks
    private GraduationMonthController graduationMonthController;

    private GraduationMonth mockGraduationMonth1;
    private GraduationMonth mockGraduationMonth2;

    private static List<GraduationMonth> mockGraduationMonths;

    @BeforeEach
    public void setup() {
        mockGraduationMonth1 = new GraduationMonth(1);
        mockGraduationMonth1.setId(1L);

        mockGraduationMonth2 = new GraduationMonth(2);
        mockGraduationMonth2.setId(2L);

        mockGraduationMonths = new ArrayList<GraduationMonth>(Arrays.asList(new GraduationMonth[] { mockGraduationMonth1 }));
    }

    @Test
    public void testAllGraduationMonths() {
        when(graduationMonthRepo.findAllByOrderByPositionAsc()).thenReturn(mockGraduationMonths);

        ApiResponse response = graduationMonthController.allGraduationMonths();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<GraduationMonth>");
        assertEquals(mockGraduationMonths.size(), list.size());
    }

    @Test
    public void testCreateGraduationMonth() {
        when(graduationMonthRepo.create(anyInt())).thenReturn(mockGraduationMonth2);

        ApiResponse response = graduationMonthController.createGraduationMonth(mockGraduationMonth1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        GraduationMonth graduationMonth = (GraduationMonth) response.getPayload().get("GraduationMonth");
        assertEquals(mockGraduationMonth2.getId(), graduationMonth.getId());
    }

    @Test
    public void testUpdateGraduationMonth() {
        when(graduationMonthRepo.update(any(GraduationMonth.class))).thenReturn(mockGraduationMonth2);

        ApiResponse response = graduationMonthController.updateGraduationMonth(mockGraduationMonth1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        GraduationMonth graduationMonth = (GraduationMonth) response.getPayload().get("GraduationMonth");
        assertEquals(mockGraduationMonth2.getId(), graduationMonth.getId());
    }

    @Test
    public void testRemoveGraduationMonth() {
        doNothing().when(graduationMonthRepo).remove(any(GraduationMonth.class));

        ApiResponse response = graduationMonthController.removeGraduationMonth(mockGraduationMonth1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(graduationMonthRepo, times(1)).remove(any(GraduationMonth.class));
    }

    @Test
    public void testReorderGraduationMonths() {
        doNothing().when(graduationMonthRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = graduationMonthController.reorderGraduationMonths(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(graduationMonthRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortGraduationMonths() {
        doNothing().when(graduationMonthRepo).sort(any(String.class));

        ApiResponse response = graduationMonthController.sortGraduationMonths("column");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(graduationMonthRepo, times(1)).sort(any(String.class));
    }

}
