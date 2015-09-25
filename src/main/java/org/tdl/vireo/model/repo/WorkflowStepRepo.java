package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

@Repository
public interface WorkflowStepRepo extends JpaRepository<WorkflowStep, Long>, WorkflowStepRepoCustom {

}
