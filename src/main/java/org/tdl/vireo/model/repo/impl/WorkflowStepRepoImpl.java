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
    
    public WorkflowStep reorderFieldProfiles(Organization requestingOrganization, WorkflowStep workflowStep, int src, int dest) throws WorkflowStepNonOverrideableException {
    	
    	// if requesting organization is not the workflow step's orignating organization    	    	    	
        if(!workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
        	// create a new workflow step
            workflowStep = update(workflowStep, requestingOrganization);
        }

		// reorder aggregate field profiles
        workflowStep.reorderAggregateFieldProfile(src, dest);
        
        // save workflow step
        return workflowStepRepo.save(workflowStep);
    }
    
    public WorkflowStep swapFieldProfiles(Organization requestingOrganization, WorkflowStep workflowStep, FieldProfile fp1, FieldProfile fp2) throws WorkflowStepNonOverrideableException {
    	
    	// if requesting organization is not the workflow step's orignating organization
        if(!workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
        	// create a new workflow step
            workflowStep = update(workflowStep, requestingOrganization);
        }
        
        // swap aggregate field profiles
        workflowStep.swapAggregateFieldProfile(fp1, fp2);
        
        // save workflow step
        return workflowStepRepo.save(workflowStep);
    }
    
    public void disinheritFromOrganization(Organization requestingOrg, WorkflowStep workflowStepToDisinherit) {
        
    	// if requesting organization is the workflow step's orignating organization
        if(requestingOrg.getId().equals(workflowStepToDisinherit.getOriginatingOrganization().getId())) {
            //the requesting organization is the owning organization so just delete
            workflowStepRepo.delete(workflowStepToDisinherit);
        } else {
          //the requesting organization is not the owning organization so only remove from aggregate workflowsteps
          requestingOrg.removeAggregateWorkflowStep(workflowStepToDisinherit);
          organizationRepo.save(requestingOrg);
        }
        
    }
    
    public WorkflowStep update(WorkflowStep workflowStepRequestedChanges, Organization requestingOrganization) throws WorkflowStepNonOverrideableException {
    	
        WorkflowStep workflowStepBeingUpdated = workflowStepRepo.findOne(workflowStepRequestedChanges.getId());
        
        boolean overrideableUntilNow = workflowStepBeingUpdated.getOverrideable();
        
        // If the requestingOrganization originates the workflowStep, make the change directly
        if(requestingOrganization.getId().equals(workflowStepBeingUpdated.getOriginatingOrganization().getId())) {

            //change is from overrideable to non-overrideable
        	if(!workflowStepRequestedChanges.getOverrideable() && overrideableUntilNow) {
        	    
            	workflowStepBeingUpdated.setOverrideable(false);
            	
            	//TODO: need to handle other changes to the workflow step in addition to setting overrideable to false?
            	
            	workflowStepRepo.save(workflowStepBeingUpdated);
            	            	
            	List<WorkflowStep> descendentWorkflowSteps = getDescendantsOfStep(workflowStepRequestedChanges);
            	
            	//if you find the descendant of the workflow step on the child org, replace it
            	for(WorkflowStep descendentWorkflowStep : descendentWorkflowSteps) {
	            	for(Organization organization : organizationRepo.findByAggregateWorkflowStepsId(descendentWorkflowStep.getId())) {
	            	    organization.replaceAggregateWorkflowStep(descendentWorkflowStep, workflowStepBeingUpdated);
	            		organizationRepo.save(organization);
	            	}
            	}
        		
            	requestingOrganization.addAggregateWorkflowStep(workflowStepBeingUpdated, requestingOrganization.getAggregateWorkflowSteps().indexOf(workflowStepBeingUpdated));
        	    organizationRepo.save(requestingOrganization);
            	
            	descendentWorkflowSteps.forEach(descendentWorkflowStep -> {
            		delete(descendentWorkflowStep);
            	});
                
            }
        	else {
        	    Boolean overridable = workflowStepRequestedChanges.getOverrideable();
        	    String name = workflowStepRequestedChanges.getName();
        	    
                workflowStepBeingUpdated.setOverrideable(overridable);
                workflowStepBeingUpdated.setName(name);
                
                //TODO: need to handle other changes to the workflow step in addition to setting overrideable and name to what was passed?
                
                workflowStepRequestedChanges = workflowStepRepo.save(workflowStepBeingUpdated);
        	}
            
        }
        // If the requestingOrganization is not originator of workflowStep, make a new workflow step to override the original
        else {
        	
            //if the workflowstep is (or was...) overrideable, then the requestor can make the change
            if(overrideableUntilNow) {
                
                List<FieldProfile> originalFieldProfiles = new ArrayList<FieldProfile>();
            	List<FieldProfile> aggregateFieldProfiles = new ArrayList<FieldProfile>();
            	
            	for(FieldProfile fp : workflowStepRequestedChanges.getOriginalFieldProfiles()) {
            		originalFieldProfiles.add(fp);            		
            	}
            	
				for(FieldProfile fp : workflowStepRequestedChanges.getAggregateFieldProfiles()) {
					aggregateFieldProfiles.add(fp);
            	}
            	
            	em.detach(workflowStepRequestedChanges);
                workflowStepRequestedChanges.setId(null);
                                
                workflowStepRequestedChanges.setOriginatingWorkflowStep(null);
                
                workflowStepRequestedChanges.setOriginatingOrganization(requestingOrganization);
                
                workflowStepRequestedChanges.setOriginalFieldProfiles(originalFieldProfiles);
                workflowStepRequestedChanges.setAggregateFieldProfiles(aggregateFieldProfiles);
                
                workflowStepRequestedChanges = workflowStepRepo.save(workflowStepRequestedChanges);
               
                for(Organization organization : getContainingDescendantOrganization(requestingOrganization, workflowStepBeingUpdated)) {
            		organization.replaceAggregateWorkflowStep(workflowStepBeingUpdated, workflowStepRequestedChanges);
            		organizationRepo.save(organization);
            	}
                
                requestingOrganization.replaceAggregateWorkflowStep(workflowStepBeingUpdated, workflowStepRequestedChanges);
                
                
            	organizationRepo.save(requestingOrganization);
            	
            	// if parent organization updates a workflow step originating form a descendent, the original workflow steps need to be deleted
                if(organizationRepo.findByAggregateWorkflowStepsId(workflowStepBeingUpdated.getId()).size() == 0) {
                    workflowStepRepo.delete(workflowStepBeingUpdated);
                }
                else {
                    workflowStepRequestedChanges.setOriginatingWorkflowStep(workflowStepBeingUpdated);
                    workflowStepRequestedChanges = workflowStepRepo.save(workflowStepRequestedChanges);
                }
            	
            }
            //if the workflow step to be updated was not overrideable, then this non-originating organization can't make the change
            else {
            	
            	// provide feedback of attempt to override non overrideable
            	// exceptions may be of better use for unavoidable error handling
            	                
                throw new WorkflowStepNonOverrideableException();
            }
            
        }
        
        return workflowStepRequestedChanges;
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
