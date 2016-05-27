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
        organizationRepo.save(originatingOrganization);
        return workflowStep;
    }
    
    // TODO: this method needs to handle all inheretence and aggregation duties
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) {
    	
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        
        // If the requestingOrganization originates the workflowStep, make the change directly
        if(originatingOrganization != null && requestingOrganization.getId().equals(originatingOrganization.getId())) {
            
            if(!workflowStep.getOverrideable()) {
                
                List<WorkflowStep> descendents = getDescendantsOfStep(workflowStep);
                
                for(WorkflowStep ws : descendents) {
                    
                    System.out.println("descendent " + ws.getId());
                    
                    for(Organization organization : organizationRepo.findByWorkflowId(ws.getId())) {
                        
                        System.out.println("  org " + organization.getId() + " " + organization.getName());
                        System.out.println("    replacing " + ws.getId() + " with " + workflowStep.getId());
                        System.out.println("    replaced: " + organization.replaceStepInWorkflow(ws, workflowStep));
                        System.out.println("    replaced: " + organization.replaceWorkflowStep(ws, workflowStep));
                        
                        organization = organizationRepo.save(organization);
                        
                        organization.getWorkflow().forEach(tws -> {
                            System.out.println("                      in replace " + tws.getId());
                        });
                    }
                    
                }
                
                for(WorkflowStep ws : descendents) {
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
                
                requestingOrganization.removeWorkflowStep(originalWorkflowStep);
                                
                System.out.println("new: " + workflowStep.getId());
                System.out.println("original: " + originalWorkflowStep.getId());
                
                workflowStep.setOriginatingWorkflowStep(originalWorkflowStep);
                workflowStep.setOriginatingOrganization(requestingOrganization);
                
                
                if(requestingOrganization.getWorkflow().contains(originalWorkflowStep)) {
                    requestingOrganization.replaceStepInWorkflow(originalWorkflowStep, workflowStep);
                }
                else {
                    requestingOrganization.addWorkflowStep(workflowStep);
                }
                
            	organizationRepo.save(requestingOrganization);
            	            	
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
        
        if(workflowStep != null && workflowStepRepo.findOne(workflowStep.getId()) != null) {

            Organization originatingOrganization = workflowStep.getOriginatingOrganization();
            
            originatingOrganization.removeWorkflowStep(workflowStep);
                    
            if(workflowStep.getOriginatingWorkflowStep() != null) {
                workflowStep.setOriginatingWorkflowStep(null);
            }
            
            fieldProfileRepo.findByOriginatingWorkflowStep(workflowStep).forEach(fieldProfile -> {
                workflowStep.removeFieldProfile(fieldProfile);
                workflowStepRepo.save(workflowStep);
                fieldProfileRepo.delete(fieldProfile);
            });
            
            organizationRepo.findByWorkflowId(workflowStep.getId()).forEach(organization -> {
                organization.removeStepFromWorkflow(workflowStep);
                organizationRepo.save(organization);
            });
            
            workflowStepRepo.findByOriginatingWorkflowStep(workflowStep).forEach(ws -> {
                ws.setOriginatingWorkflowStep(null);
            });
            
            deleteDescendantsOfStep(workflowStep);
            
            workflowStepRepo.delete(workflowStep.getId());
        
        }
    }
    
    private void deleteDescendantsOfStep(WorkflowStep workflowStep) {
        workflowStepRepo.findByOriginatingWorkflowStep(workflowStep).forEach(desendantWorflowStep -> {
            deleteDescendantsOfStep(desendantWorflowStep);
            delete(desendantWorflowStep);
        });
    }
    
    private List<WorkflowStep> getDescendantsOfStep(WorkflowStep workflowStep) {
        List<WorkflowStep> descendantWorkflowSteps = new ArrayList<WorkflowStep>();
        List<WorkflowStep> currentDescendentsWorkflowSteps = workflowStepRepo.findByOriginatingWorkflowStep(workflowStep);
        descendantWorkflowSteps.addAll(currentDescendentsWorkflowSteps);
        currentDescendentsWorkflowSteps.forEach(desendantWorflowStep -> {
            descendantWorkflowSteps.addAll(getDescendantsOfStep(desendantWorflowStep));
        });
        return descendantWorkflowSteps;
    }
    
}
