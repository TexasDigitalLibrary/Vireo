package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public class WorkflowStepRepoImpl implements WorkflowStepRepoCustom {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;

    @Override
    public WorkflowStep create(String name, Organization originatingOrganization) {
        WorkflowStep workflowStep = workflowStepRepo.save(new WorkflowStep(name, originatingOrganization));
//        originatingOrganization.addWorkflowStep(workflowStep);
//        organizationRepo.save(originatingOrganization);
        return workflowStep;
    }
    
    @Override
    public void delete(WorkflowStep workflowStep) {
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        
        workflowStep.getContainedByOrganizations().forEach(organization -> {
            organization.removeWorkflowStep(workflowStep);
            organizationRepo.save(organization);
            workflowStep.removeContainedByOrganization(organization);
        });
        
        originatingOrganization.removeWorkflowStep(workflowStep);
        organizationRepo.save(originatingOrganization);
        
        workflowStepRepo.delete(workflowStep.getId());
        
        //entityManager.remove(entityManager.contains(workflowStep) ? workflowStep : entityManager.merge(workflowStep));
    }

}
