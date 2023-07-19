package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class WorkflowStepControllerTest extends AbstractControllerTest {

    @Mock
    private WorkflowStepRepo fieldPredicateRepo;

    @InjectMocks
    private WorkflowStepController fieldPredicateController;

    private WorkflowStep mockWorkflowStep1;
    private WorkflowStep mockWorkflowStep2;

    private static List<WorkflowStep> mockWorkflowSteps;

    @BeforeEach
    public void setup() {
        mockWorkflowStep1 = new WorkflowStep("WorkflowStep 1");
        mockWorkflowStep1.setId(1L);

        mockWorkflowStep2 = new WorkflowStep("WorkflowStep 2");
        mockWorkflowStep2.setId(2L);

        mockWorkflowSteps = new ArrayList<WorkflowStep>(Arrays.asList(new WorkflowStep[] { mockWorkflowStep1 }));
    }

    @Test
    public void testAllWorkflowSteps() {
        when(fieldPredicateRepo.findAll()).thenReturn(mockWorkflowSteps);

        ApiResponse response = fieldPredicateController.getAll();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<WorkflowStep>");
        assertEquals(mockWorkflowSteps.size(), list.size());
    }

    @Test
    public void testGetWorkflowStep() {
        when(fieldPredicateRepo.findById(any(Long.class))).thenReturn(Optional.of(mockWorkflowStep1));

        ApiResponse response = fieldPredicateController.getStepById(1L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        WorkflowStep fieldPredicate = (WorkflowStep) response.getPayload().get("WorkflowStep");
        assertEquals(mockWorkflowStep1.getId(), fieldPredicate.getId());
    }

}
