package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.WorkflowStep;

public interface SubmissionWorkflowStepRepoCustom {
       
    public SubmissionWorkflowStep findOrCreate(Organization originatingOrganization, WorkflowStep originatingWorkflowStep);

}
