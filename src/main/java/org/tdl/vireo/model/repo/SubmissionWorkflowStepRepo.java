package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.repo.custom.SubmissionWorkflowStepRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.List;

public interface SubmissionWorkflowStepRepo extends WeaverRepo<SubmissionWorkflowStep>, SubmissionWorkflowStepRepoCustom {

    @Query(value = "SELECT w.* FROM submission_workflow_step w INNER JOIN submission_submission_workflow_steps s ON w.id = submission_workflow_steps_id WHERE s.submission_id = :submissionId ORDER BY s.submission_workflow_steps_order DESC", nativeQuery=true)
    public List<SubmissionWorkflowStep> findBySubmissionId(@Param("submissionId") Long submissionId);
}
