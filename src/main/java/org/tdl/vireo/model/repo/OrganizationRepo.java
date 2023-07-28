package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface OrganizationRepo extends WeaverRepo<Organization>, OrganizationRepoCustom {

    public long countById(Long id);

    public <T> T findViewById(Long id, Class<T> type);

    public <T> List<T> findViewAllByOrderByIdAsc(Class<T> type);

    public List<Organization> findAllByOrderByIdAsc();

    public List<Organization> findByAggregateWorkflowStepsId(Long workflowStepId);

    public List<Organization> findByOriginalWorkflowStepsId(Long workflowStepId);

    public List<Organization> findAllByNameAndCategory(String name, OrganizationCategory category);

    public Organization findByNameAndCategory(String name, OrganizationCategory category);

}
