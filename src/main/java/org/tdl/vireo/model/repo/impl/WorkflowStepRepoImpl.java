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
        originatingOrganization.addWorkflowStep(workflowStep);
        return workflowStep;
    }
    
    // TODO: this method needs to handle all inheretence and aggregation duties
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) {
        
        Long originalWorkflowStepId = workflowStep.getId();        
        
        em.detach(workflowStep);
        
        workflowStep.setId(null);
        
        workflowStep = workflowStepRepo.save(workflowStep);
        
        
        WorkflowStep originalWorkflowStep = workflowStepRepo.findOne(originalWorkflowStepId);
        
        
        WorkflowStep originatingWorkflowStep = originalWorkflowStep.getOriginatingWorkflowStep();
        Organization originatingOrganization = originalWorkflowStep.getOriginatingOrganization();
        
        
        workflowStep.setOriginatingWorkflowStep(originalWorkflowStep);
                
        
        
        
        
        
        // If the requestingOrganization originates the workflowStep, make the change directly
        if(originatingOrganization != null && requestingOrganization.getId().equals(originatingOrganization.getId())) {
            
            if(!workflowStep.getOverrideable() && originalWorkflowStep.getOverrideable()) {
                
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
            
            
        }
        // If the requestingOrganization is not originator of workflowStep,

        else {
            workflowStep.setOriginatingOrganization(requestingOrganization);
            
            
            
            if(workflowStep.getOverrideable()) {
                
                // provide feedback of attempt to override non overrideable
            	// exceptions may be of better use for unavoidable error handling
                
            	workflowStep.setOriginatingWorkflowStep(originalWorkflowStep);
                workflowStep.setOriginatingOrganization(requestingOrganization);
            }
            
        }
        
        
        
        return workflowStepRepo.save(workflowStep);
    }
    
    @Override
    public void delete(WorkflowStep workflowStep) {
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        
        originatingOrganization.removeWorkflowStep(workflowStep);
        
        organizationRepo.save(originatingOrganization);
        
        workflowStep.setOriginatingWorkflowStep(null);
        
        organizationRepo.findByWorkflowId(workflowStep.getId()).forEach(containingOrganization -> {
            containingOrganization.removeWorkflowStep(workflowStep);
            containingOrganization.removeStepFromWorkflow(workflowStep);
        });
        
//        workflowStepRepo.findByOriginatingWorkflowStep(workflowStep).forEach(migratingWorkflowStep -> {
//            
//            if(migratingWorkflowStep.getId().equals(workflowStep.getId())) {
//                System.out.println("\nHOWD THIS HAPPEN\n");
//            }
//            else {
//                if(migratingWorkflowStep.getOriginatingWorkflowStep() == null) {
//                    
//                }
//                else {
//                    
//                }
//            }
//            
//            
//        });

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
