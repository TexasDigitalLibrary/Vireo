package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.packager.AbstractPackager;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;

@ActiveProfiles("test")
public class PackagerControllerTest extends AbstractControllerTest {

    @Mock
    private AbstractPackagerRepo packagerRepo;

    @InjectMocks
    private PackagerController packagerController;

    private static List<AbstractPackager<?>> mockPackages;

    @BeforeEach
    public void setup() {
        mockPackages = new ArrayList<AbstractPackager<?>>();
    }

    @Test
    public void testAllNotes() {
        when(packagerRepo.findAll()).thenReturn(mockPackages);

        ApiResponse response = packagerController.allPackagers();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList");
        assertNotNull(mockPackages, "Payload response is not an ArrayList.");
        assertEquals(mockPackages.size(), list.size(), "Payload response array is the wrong length.");
    }

}
