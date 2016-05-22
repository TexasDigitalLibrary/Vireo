package org.tdl.vireo.model.repo.impl;

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
        
        //workflowStep.addContainingOrganization(originatingOrganization);
        
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
                
        
        
        
        
        
        if(requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            
            if(!workflowStep.getOverrideable() && originalWorkflowStep.getOverrideable()) {
                
                
                
            }
            
            
        }
        else {
            workflowStep.setOriginatingOrganization(requestingOrganization);
            
            
            
            if(workflowStep.getOverrideable()) {
                
                
                
            }
            
        }
        
        
        
        
        
//        System.out.println("\n" + organizationRepo.findByWorkflowId(workflowStep.getId()).size() + "\n");
//        
//        
//        
//        organizationRepo.findByWorkflowId(workflowStep.getId()).forEach(containingOrganization -> {
//            System.out.println("\n" + containingOrganization + "\n");
//        });
       
        
       
        
        
        
        
        
        
        
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
    
}
