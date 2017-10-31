package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface WorkflowStepRepo extends WeaverRepo<WorkflowStep>, WorkflowStepRepoCustom {

    public List<WorkflowStep> findByName(String name);

    public List<WorkflowStep> findByOriginatingOrganization(Organization originatingOrganization);

    public List<WorkflowStep> findByOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);

    public List<WorkflowStep> findByAggregateFieldProfilesId(Long fieldProfileId);

    public List<WorkflowStep> findByAggregateNotesId(Long noteId);

    public WorkflowStep findByNameAndOriginatingOrganization(String name, Organization originatingOrganization);

}
