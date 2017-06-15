package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.WorkflowStep;

public interface SubmissionWorkflowStepRepoCustom {

    public List<SubmissionWorkflowStep> cloneWorkflow(Organization organization);

    public SubmissionWorkflowStep cloneWorkflowStep(WorkflowStep workflowStep);

}
