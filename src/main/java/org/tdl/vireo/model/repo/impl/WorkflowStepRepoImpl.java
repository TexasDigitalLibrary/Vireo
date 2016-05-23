package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
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
        
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        WorkflowStep originatingWorkflowStep = workflowStep.getOriginatingWorkflowStep();
        
        // If the requestingOrganization originates the workflowStep, make the change directly
        if(originatingOrganization != null && requestingOrganization.getId().equals(originatingOrganization.getId())) {
            
            if(!workflowStep.getOverrideable()) {
                            	
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
        	
            if(workflowStep.getOverrideable()) {
            	
            	Long originalWorkflowStepId = workflowStep.getId();        
                
                em.detach(workflowStep);
                
                workflowStep.setId(null);
                
                workflowStep = workflowStepRepo.save(workflowStep);
                
                
                WorkflowStep originalWorkflowStep = workflowStepRepo.findOne(originalWorkflowStepId);
                
            	workflowStep.setOriginatingWorkflowStep(originalWorkflowStep);
            	workflowStep.setOriginatingOrganization(requestingOrganization);                
                
            	
            	// this is important!
            	// This causes problems for some reason
            	
//            	requestingOrganization.addWorkflowStep(workflowStep);
//              organizationRepo.save(requestingOrganization);
            }
            else {
            	
            	// provide feedback of attempt to override non overrideable
            	// exceptions may be of better use for unavoidable error handling
            	
            	//TODO: add non overridable exception and throw it here
            }
            
        }
        
        
        
        
        return workflowStepRepo.save(workflowStep);
    }
    
    @Override
    public void delete(WorkflowStep workflowStep) {
    	
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        
        originatingOrganization.removeWorkflowStep(workflowStep);
        
        organizationRepo.save(originatingOrganization);
        
        
        if(workflowStep.getOriginatingWorkflowStep() != null) {
        	workflowStep.setOriginatingWorkflowStep(null);
        }
        
                
        organizationRepo.findByWorkflowId(workflowStep.getId()).forEach(containingOrganization -> {
        	containingOrganization.removeStepFromWorkflow(workflowStep);
            organizationRepo.save(containingOrganization);
        });
        
        
//        workflowStepRepo.findByOriginatingWorkflowStep(workflowStep).forEach(migratingWorkflowStep -> {
//            
//            if(migratingWorkflowStep.getId().equals(workflowStep.getId())) {
//                
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
//        });

        
        fieldProfileRepo.findByOriginatingWorkflowStep(workflowStep).forEach(fieldProfile -> {
        	workflowStep.removeFieldProfile(fieldProfile);
        	workflowStepRepo.save(workflowStep);
        	fieldProfileRepo.delete(fieldProfile);
        });
        
        
        workflowStepRepo.delete(workflowStep.getId());
    }
    
    private List<WorkflowStep> getDescendantsOfStep(WorkflowStep workflowStep) {
        List<WorkflowStep> descendantWorkflowSteps = new ArrayList<WorkflowStep>();
        List<WorkflowStep> currentWorkflowSteps = workflowStepRepo.findByOriginatingWorkflowStep(workflowStep);
        descendantWorkflowSteps.addAll(currentWorkflowSteps);
        currentWorkflowSteps.forEach(desendantWorflowStep -> {
            descendantWorkflowSteps.addAll(getDescendantsOfStep(desendantWorflowStep));
        });
        return descendantWorkflowSteps;
    }

    
}
