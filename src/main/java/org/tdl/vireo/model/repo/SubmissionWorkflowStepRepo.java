package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.repo.custom.SubmissionWorkflowStepRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionWorkflowStepRepo extends WeaverRepo<SubmissionWorkflowStep>, SubmissionWorkflowStepRepoCustom {

}
