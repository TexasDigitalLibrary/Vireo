package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface OrganizationRepo extends WeaverRepo<Organization>, OrganizationRepoCustom {

    public List<Organization> findAllByOrderByIdAsc();

    public List<Organization> findByAggregateWorkflowStepsId(Long workflowStepId);

    public List<Organization> findByOriginalWorkflowStepsId(Long workflowStepId);

    public List<Organization> findByCategory(String name, OrganizationCategory category);

    public Organization findByNameAndCategory(String name, OrganizationCategory category);

}
