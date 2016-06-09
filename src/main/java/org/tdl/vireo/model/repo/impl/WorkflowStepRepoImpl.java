package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
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
    private FieldProfileRepo fieldProfileRepo;
	
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
    	
        if(workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || workflowStep.getOverrideable()) {
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
        else {
            throw new WorkflowStepNonOverrideableException();
        }
    }
    
    public WorkflowStep swapFieldProfiles(Organization requestingOrganization, WorkflowStep workflowStep, FieldProfile fp1, FieldProfile fp2) throws WorkflowStepNonOverrideableException {
    	
        if(workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || workflowStep.getOverrideable()) {
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
        else {
            throw new WorkflowStepNonOverrideableException();
        }
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
    
    public WorkflowStep update(WorkflowStep pendingWorkflowStep, Organization requestingOrganization) throws WorkflowStepNonOverrideableException {
    	
        WorkflowStep resultingWorkflowStep = null;
        
        WorkflowStep persistedWorkflowStep = workflowStepRepo.findOne(pendingWorkflowStep.getId());
        
        boolean overridabilityOfPersistedWorkflowStep = persistedWorkflowStep.getOverrideable();
        
        // if the requestingOrganization originates the workflowStep, make the change directly
        if(requestingOrganization.getId().equals(persistedWorkflowStep.getOriginatingOrganization().getId())) {

        	if(!pendingWorkflowStep.getOverrideable() && overridabilityOfPersistedWorkflowStep) {
        	    
            	persistedWorkflowStep.setOverrideable(false);
            	
            	WorkflowStep savedWorkflowStep = workflowStepRepo.save(persistedWorkflowStep);
            	            	
            	List<WorkflowStep> descendentWorkflowSteps = getDescendantsOfStep(persistedWorkflowStep);
            	
            	for(WorkflowStep descendentWorkflowStep : descendentWorkflowSteps) {
	            	for(Organization organization : organizationRepo.findByAggregateWorkflowStepsId(descendentWorkflowStep.getId())) {
	            	    organization.replaceAggregateWorkflowStep(descendentWorkflowStep, savedWorkflowStep);
	            		organizationRepo.save(organization);
	            	}
            	}
        		
            	requestingOrganization.addAggregateWorkflowStep(savedWorkflowStep, requestingOrganization.getAggregateWorkflowSteps().indexOf(savedWorkflowStep));
        	    organizationRepo.save(requestingOrganization);
            	
            	descendentWorkflowSteps.forEach(descendentWorkflowStep -> {
            		delete(descendentWorkflowStep);
            	});
            	
            	resultingWorkflowStep = savedWorkflowStep;
                
            }
        	else {
        	    Boolean overridable = pendingWorkflowStep.getOverrideable();
        	    String name = pendingWorkflowStep.getName();
        	    
        	    List<Note> notes = new ArrayList<Note>();
                for(Note n : pendingWorkflowStep.getNotes()) {
                    notes.add(n);
                }
        	    
        	    List<FieldProfile> originalFieldProfiles = new ArrayList<FieldProfile>();
                for(FieldProfile fp : pendingWorkflowStep.getOriginalFieldProfiles()) {
                    originalFieldProfiles.add(fp);
                }
        	    
        	    List<FieldProfile> aggregateFieldProfiles = new ArrayList<FieldProfile>();                
                for(FieldProfile fp : pendingWorkflowStep.getAggregateFieldProfiles()) {
                    aggregateFieldProfiles.add(fp);
                }
        	    
                persistedWorkflowStep.setOverrideable(overridable);
                persistedWorkflowStep.setName(name);
                
                persistedWorkflowStep.setNotes(notes);
                
                persistedWorkflowStep.setOriginalFieldProfiles(originalFieldProfiles);
                persistedWorkflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);
                
                // TODO: handle additional properties to the workflow step
                
                resultingWorkflowStep = workflowStepRepo.save(persistedWorkflowStep);
        	}
            
        }
        // if the requestingOrganization is not originator of workflowStep, make a new workflow step to override the original
        else {
        	
            if(overridabilityOfPersistedWorkflowStep) {
                
                List<FieldProfile> aggregateFieldProfiles = new ArrayList<FieldProfile>();
				for(FieldProfile fp : pendingWorkflowStep.getAggregateFieldProfiles()) {
					aggregateFieldProfiles.add(fp);
            	}
				
				List<Note> notes = new ArrayList<Note>();                
                for(Note n : pendingWorkflowStep.getNotes()) {
                    notes.add(n);
                }
            	
            	em.detach(pendingWorkflowStep);
                pendingWorkflowStep.setId(null);
                                
                pendingWorkflowStep.setOriginatingWorkflowStep(null);
                
                pendingWorkflowStep.setOriginatingOrganization(requestingOrganization);
                
                // this is important, original field profiles will be related to this new workflow step as the originator 
                pendingWorkflowStep.setOriginalFieldProfiles(new ArrayList<FieldProfile>());
                
                pendingWorkflowStep.setAggregateFieldProfiles(aggregateFieldProfiles);
                
                pendingWorkflowStep.setNotes(notes);
                
                WorkflowStep newWorkflowStep = workflowStepRepo.save(pendingWorkflowStep);
                
                
                
                for(Organization organization : getContainingDescendantOrganization(requestingOrganization, persistedWorkflowStep)) {
                    organization.replaceAggregateWorkflowStep(persistedWorkflowStep, newWorkflowStep);
                    organizationRepo.save(organization);
                }
                
                
                if(organizationRepo.findByAggregateWorkflowStepsId(persistedWorkflowStep.getId()).size() == 0) {
                    workflowStepRepo.delete(persistedWorkflowStep);
                }
                else {
                    newWorkflowStep.setOriginatingWorkflowStep(persistedWorkflowStep);
                    newWorkflowStep = workflowStepRepo.save(newWorkflowStep);
                }


                if(!pendingWorkflowStep.getOverrideable()) {
                    
                    List<WorkflowStep> descendentWorkflowSteps = getDescendantsOfStep(persistedWorkflowStep);

                    for(WorkflowStep descendentWorkflowStep : descendentWorkflowSteps) {

                        for(Organization organization : organizationRepo.findByAggregateWorkflowStepsId(descendentWorkflowStep.getId())) {
                            organization.replaceAggregateWorkflowStep(descendentWorkflowStep, newWorkflowStep);
                            organizationRepo.save(organization);
                        }
                        
                        // delete if not belonging to any aggregate
                        if(organizationRepo.findByAggregateWorkflowStepsId(descendentWorkflowStep.getId()).size() == 0) {
                        	
                        	descendentWorkflowStep.getOriginalFieldProfiles().forEach(fp -> {
                        		System.out.println("deleting field profile id: " + fp.getId());
                        		System.out.println("deleting field profile: " + fp.getPredicate().getValue());
                        	});
                        	
                            workflowStepRepo.delete(descendentWorkflowStep);
                        }
                    }
                    
                }


                resultingWorkflowStep = newWorkflowStep;
            }
            //if the workflow step to be updated was not overrideable, then this non-originating organization can't make the change
            else {
            	
            	// provide feedback of attempt to override non overrideable
            	// exceptions may be of better use for unavoidable error handling

                throw new WorkflowStepNonOverrideableException();
            }
            
        }
        
        return resultingWorkflowStep;
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
	        
	        List<FieldProfile> fieldProfilesToDelete = new ArrayList<FieldProfile>();
	        
	        fieldProfileRepo.findByOriginatingWorkflowStep(workflowStep).forEach(fp -> {
	        	fieldProfilesToDelete.add(fp);
	        });
	        
	        fieldProfilesToDelete.forEach(fp -> {
	           fieldProfileRepo.delete(fp);
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
