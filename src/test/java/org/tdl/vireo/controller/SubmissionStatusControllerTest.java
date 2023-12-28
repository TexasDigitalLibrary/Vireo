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
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
public class SubmissionStatusControllerTest extends AbstractControllerTest {

    @Mock
    private SubmissionStatusRepo submissionStatusRepo;

    @InjectMocks
    private SubmissionStatusController submissionStatusController;

    private SubmissionStatus mockSubmissionStatus1;

    private static List<SubmissionStatus> mockSubmissionStatuss;

    @BeforeEach
    public void setup() {
        mockSubmissionStatus1 = new SubmissionStatus();
        mockSubmissionStatus1.setId(1L);

        mockSubmissionStatuss = new ArrayList<SubmissionStatus>(Arrays.asList(new SubmissionStatus[] { mockSubmissionStatus1 }));
    }

    @Test
    public void testAllSubmissionStatuss() {
        when(submissionStatusRepo.findAll()).thenReturn(mockSubmissionStatuss);

        ApiResponse response = submissionStatusController.getAllSubmissionStatuses();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<SubmissionStatus>");
        assertEquals(mockSubmissionStatuss.size(), list.size());
    }

}
