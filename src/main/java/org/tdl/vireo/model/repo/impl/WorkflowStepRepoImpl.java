package org.tdl.vireo.model.repo.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public class WorkflowStepRepoImpl implements WorkflowStepRepoCustom {
    
    @PersistenceContext
    private EntityManager em;
	
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Override
    public WorkflowStep create(String name, Organization originatingOrganization) {
        WorkflowStep workflowStep = workflowStepRepo.save(new WorkflowStep(name, originatingOrganization));
        originatingOrganization.addOriginalWorkflowStep(workflowStep);
        
        //workflowStep.addContainingOrganization(originatingOrganization);
        
        return workflowStep;
    }
    
    //this method needs to handle all inheritance and aggregation duties
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) throws WorkflowStepNonOverrideableException {
        
        WorkflowStep originatingWorkflowStep = workflowStep.getOriginatingWorkflowStep();
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
         
        //If the requestingOrganization originates the workflowStep, make the change directly
        if(originatingOrganization != null && requestingOrganization.getId().equals(originatingOrganization.getId())) {
            
            if(workflowStep.getOverrideable() == false) {

                List<WorkflowStep> derivativeWorkflowStepsToDelete = getDescendantsOfStep(workflowStep);
                
                for(WorkflowStep ws : derivativeWorkflowStepsToDelete){
                    
                    organizationRepo.findByWorkflowId(ws.getId()).forEach(organization -> {
                        //TODO:  this puts it at the end; we should make sure it gets into the right spot
                        organization.addStepToWorkflow(originatingWorkflowStep);
                        organizationRepo.save(organization);
                    });
                    
                    workflowStepRepo.delete(ws);
                    
                } 
                
            }
            
            //TODO:  needed?
            //organizationRepo.save(requestingOrganization);
            
            //reattach, persist changes, and return our updated workflow step
            return workflowStepRepo.save(workflowStep);        
        }
        //If the requestingOrganization is not originator of workflowStep,
        else {
            
            em.detach(workflowStep);
            workflowStep.setId(null);
            
            //throw exception if non-overrideable
            if(!workflowStep.getOverrideable()) {
                throw new WorkflowStepNonOverrideableException();
            }
            //otherwise, make a newWorkflowStep at requestingOrganization
            //and have descendants of requestingOrganization get the replacement
            else {
                workflowStep.setId(null);
                //workflowStep.setOriginatingWorkflowStep(originalWorkflowStep);
                workflowStep.setOriginatingOrganization(requestingOrganization);
                
            }
        }
        
        return workflowStepRepo.save(workflowStep);
    }

    @Override
    public void delete(WorkflowStep workflowStep) {
        
        //TODO:  can we obviate these three lines with a better removeStepFromWorkflow method?
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        originatingOrganization.removeOriginalWorkflowStep(workflowStep);
        //organizationRepo.save(originatingOrganization);
        
        organizationRepo.findByWorkflowId(workflowStep.getId()).forEach(organization -> {
            organization.removeStepFromWorkflow(workflowStep);
            organizationRepo.save(organization);
        });
        
        fieldProfileRepo.findByOriginatingWorkflowStep(workflowStep).forEach(fieldProfile -> {
        	fieldProfileRepo.delete(fieldProfile);
        });
        
        workflowStepRepo.delete(workflowStep.getId());
    }
    
    
    private List<WorkflowStep> getDescendantsOfStep(WorkflowStep workflowStep) {

        List<WorkflowStep> descendantWorkflowSteps = workflowStepRepo.findByOriginatingWorkflowStep(workflowStep);

        descendantWorkflowSteps.forEach(desendantWorflowStep -> {
            descendantWorkflowSteps.addAll(getDescendantsOfStep(desendantWorflowStep));
        });
        
        return descendantWorkflowSteps;
    }
    
}
