package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public class WorkflowStepRepoImpl implements WorkflowStepRepoCustom {
	
	@PersistenceContext
    private EntityManager em;
	
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Override
    public WorkflowStep create(String name, Organization originatingOrganization) {
        WorkflowStep workflowStep = workflowStepRepo.save(new WorkflowStep(name, originatingOrganization));
        originatingOrganization.addOriginalWorkflowStep(workflowStep);
        organizationRepo.save(originatingOrganization);
        return workflowStepRepo.findOne(workflowStep.getId());
    }
    
    public WorkflowStep reorderFieldProfiles(Organization requestingOrganization, WorkflowStep workflowStep, FieldProfile fp1, FieldProfile fp2) throws WorkflowStepNonOverrideableException {
       
        if(workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
            
            for(WorkflowStep ws : requestingOrganization.getAggregateWorkflowSteps()) {
                if(ws.getId().equals(workflowStep.getId())) {
                    ws.swapAggregateFieldProfile(fp1, fp2);
                    workflowStep = ws;
                    break;
                }
            }
            
            
        }
        else {
            
            workflowStep = update(workflowStep, requestingOrganization);
            
            requestingOrganization = organizationRepo.findOne(requestingOrganization.getId());
            
            for(WorkflowStep ws : requestingOrganization.getAggregateWorkflowSteps()) {
                if(ws.getId().equals(workflowStep.getId())) {
                    ws.swapAggregateFieldProfile(fp1, fp2);
                    workflowStep = ws;
                    break;
                }
            }
            
        }
        
        requestingOrganization = organizationRepo.save(requestingOrganization);
        
        return workflowStep;
    }
    
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) throws WorkflowStepNonOverrideableException {
    	
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
                
        // If the requestingOrganization originates the workflowStep, make the change directly
        if(originatingOrganization != null && requestingOrganization.getId().equals(originatingOrganization.getId())) {

        	if(!workflowStep.getOverrideable()) {
        		                
            	Long originalWorkflowStepId = workflowStep.getId();
            	
            	WorkflowStep originalWorkflowStep = workflowStepRepo.findOne(originalWorkflowStepId);
            	            	
            	List<WorkflowStep> descendentWorkflowSteps = getDescendantsOfStep(workflowStep);
            	
            	for(WorkflowStep descendentWorkflowStep : descendentWorkflowSteps) {
	            	for(Organization organization : organizationRepo.findByAggregateWorkflowStepsId(descendentWorkflowStep.getId())) {
	            		organization.replaceAggregateWorkflowStep(descendentWorkflowStep, originalWorkflowStep);
	            		organizationRepo.save(organization);
	            	}
            	}
            	
            	descendentWorkflowSteps.forEach(descendentWorkflowStep -> {
            		delete(descendentWorkflowStep);
            	});
                
            }
        	else {
        	    workflowStep = workflowStepRepo.save(workflowStep);
        	}
            
        }
        // If the requestingOrganization is not originator of workflowStep,
        else {
        	
            if(workflowStep.getOverrideable()) {
            	
            	Long originalWorkflowStepId = workflowStep.getId();
            	
            	
            	List<FieldProfile> originalFieldProfiles = new ArrayList<FieldProfile>();
            	List<FieldProfile> aggregateFieldProfiles = new ArrayList<FieldProfile>();
            	
            	for(FieldProfile fp : workflowStep.getOriginalFieldProfiles()) {
            		originalFieldProfiles.add(fp);            		
            	}
            	
				for(FieldProfile fp : workflowStep.getAggregateFieldProfiles()) {
					aggregateFieldProfiles.add(fp);
            	}
            	
				
            	em.detach(workflowStep);
                workflowStep.setId(null);
                
                
                WorkflowStep originalWorkflowStep = workflowStepRepo.findOne(originalWorkflowStepId);
                
                workflowStep.setOriginatingWorkflowStep(null);
                
                workflowStep.setOriginatingOrganization(requestingOrganization);
                
                
                workflowStep.setOriginalFieldProfiles(originalFieldProfiles);
                workflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);
                
                
                workflowStep = workflowStepRepo.save(workflowStep);
                
                
                for(Organization organization : getContainingDescendantOrganization(requestingOrganization, originalWorkflowStep)) {
            		organization.replaceAggregateWorkflowStep(originalWorkflowStep, workflowStep);
            		organizationRepo.save(organization);
            	}
                
                requestingOrganization.replaceAggregateWorkflowStep(originalWorkflowStep, workflowStep);
                
                
            	organizationRepo.save(requestingOrganization);
            	
            	// if parent organization updates a workflow step originating form a descendent, the original workflow steps need to be deleted
                if(organizationRepo.findByAggregateWorkflowStepsId(originalWorkflowStep.getId()).size() == 0) {
                    workflowStepRepo.delete(originalWorkflowStep);
                }
                else {
                    workflowStep.setOriginatingWorkflowStep(originalWorkflowStep);
                    workflowStep = workflowStepRepo.save(workflowStep);
                }
            	
            }
            else {
            	
            	// provide feedback of attempt to override non overrideable
            	// exceptions may be of better use for unavoidable error handling
            	                
                throw new WorkflowStepNonOverrideableException();
            }
            
        }
        
        return workflowStep;
    }
    
    @Override
    public void delete(WorkflowStep workflowStep) {
    	
    	// allows for delete by iterating through findAll, while still deleting descendents
    	if(workflowStepRepo.findOne(workflowStep.getId()) != null) {
    	    
	        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
	        
	        originatingOrganization.removeOriginalWorkflowStep(workflowStep);
	                
	        if(workflowStep.getOriginatingWorkflowStep() != null) {
	            workflowStep.setOriginatingWorkflowStep(null);
	        }
	        
	        organizationRepo.findByAggregateWorkflowStepsId(workflowStep.getId()).forEach(organization -> {
	            organization.removeAggregateWorkflowStep(workflowStep);
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
    
    private List<Organization> getContainingDescendantOrganization(Organization organization, WorkflowStep workflowStep) {
        List<Organization> descendantOrganizationsContainingWorkflowStep = new ArrayList<Organization>();
        if(organization.getAggregateWorkflowSteps().contains(workflowStep)) {
        	descendantOrganizationsContainingWorkflowStep.add(organization);
        }
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
        	descendantOrganizationsContainingWorkflowStep.addAll(getContainingDescendantOrganization(descendantOrganization, workflowStep));
        });
        return descendantOrganizationsContainingWorkflowStep;
    }
    
}
