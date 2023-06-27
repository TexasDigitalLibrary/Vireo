package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.DegreeLevelRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DegreeLevelControllerTest extends AbstractControllerTest {

    @Mock
    private DegreeLevelRepo degreeLevelRepo;

    @InjectMocks
    private DegreeLevelController degreeLevelController;

    private DegreeLevel mockDegreeLevel1;

    private static List<DegreeLevel> mockDegreeLevels;

    @BeforeEach
    public void setup() {
        mockDegreeLevel1 = new DegreeLevel("DegreeLevel 1");
        mockDegreeLevel1.setId(1L);
        mockDegreeLevel1.setPosition(1L);

        mockDegreeLevels = new ArrayList<DegreeLevel>(Arrays.asList(new DegreeLevel[] { mockDegreeLevel1 }));
    }

    @Test
    public void testAllDegreeLevels() {
        when(degreeLevelRepo.findAllByOrderByPositionAsc()).thenReturn(mockDegreeLevels);

        ApiResponse response = degreeLevelController.allDegreeLevels();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<DegreeLevel>");
        assertEquals(mockDegreeLevels.size(), list.size());
    }

}
