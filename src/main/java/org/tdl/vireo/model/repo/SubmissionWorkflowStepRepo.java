package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.repo.custom.SubmissionWorkflowStepRepoCustom;

public interface SubmissionWorkflowStepRepo extends JpaRepository<SubmissionWorkflowStep, Long>, SubmissionWorkflowStepRepoCustom {

}
