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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.repo.FieldProfileRepo;

@ActiveProfiles("test")
public class FieldProfileControllerTest extends AbstractControllerTest {

    @Mock
    private FieldProfileRepo fieldProfileRepo;

    @InjectMocks
    private FieldProfileController fieldProfileController;

    private FieldProfile mockFieldProfile1;

    private static List<FieldProfile> mockFieldProfiles;

    @BeforeEach
    public void setup() {
        mockFieldProfile1 = new FieldProfile();
        mockFieldProfile1.setId(1L);

        mockFieldProfiles = new ArrayList<FieldProfile>(Arrays.asList(new FieldProfile[] { mockFieldProfile1 }));
    }

    @Test
    public void testAllFieldProfiles() {
        when(fieldProfileRepo.findAll()).thenReturn(mockFieldProfiles);

        ApiResponse response = fieldProfileController.getAllFieldProfiles();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<FieldProfile>");
        assertEquals(mockFieldProfiles.size(), list.size());
    }

}
