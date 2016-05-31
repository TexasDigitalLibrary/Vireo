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
        originatingOrganization.addWorkflowStep(workflowStep);
        organizationRepo.save(originatingOrganization);
        return workflowStepRepo.findOne(workflowStep.getId());
    }

    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) {
    	
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
                
        // If the requestingOrganization originates the workflowStep, make the change directly
        if(originatingOrganization != null && requestingOrganization.getId().equals(originatingOrganization.getId())) {

        	if(!workflowStep.getOverrideable()) {
        		                
            	Long originalWorkflowStepId = workflowStep.getId();
            	
            	WorkflowStep originalWorkflowStep = workflowStepRepo.findOne(originalWorkflowStepId);
            	            	
            	List<WorkflowStep> descendentWorkflowSteps = getDescendantsOfStep(workflowStep);
            	
            	for(WorkflowStep descendentWorkflowStep : descendentWorkflowSteps) {
	            	for(Organization organization : organizationRepo.findByWorkflowId(descendentWorkflowStep.getId())) {
	            		organization.replaceStepInWorkflow(descendentWorkflowStep, originalWorkflowStep);
	            		organizationRepo.save(organization);
	            	}
            	}
            	
            	descendentWorkflowSteps.forEach(descendentWorkflowStep -> {
            		delete(descendentWorkflowStep);
            	});
            	
            	return workflowStep;
                
            }
            
        }
        // If the requestingOrganization is not originator of workflowStep,
        else {
        	
            if(workflowStep.getOverrideable()) {
            	
            	Long originalWorkflowStepId = workflowStep.getId();
            	
            	
            	List<FieldProfile> fieldProfiles = new ArrayList<FieldProfile>();
            	List<FieldProfile> fields = new ArrayList<FieldProfile>();
            	
            	for(FieldProfile fp : workflowStep.getFieldProfiles()) {
            		fieldProfiles.add(fp);            		
            	}
            	
				for(FieldProfile fp : workflowStep.getFields()) {
					fields.add(fp);
            	}
            	
				workflowStep.setFieldProfiles(new ArrayList<FieldProfile>());
				workflowStep.setFields(new ArrayList<FieldProfile>());
            	
            	em.detach(workflowStep);
                workflowStep.setId(null);
                
                
                WorkflowStep originalWorkflowStep = workflowStepRepo.findOne(originalWorkflowStepId);

                workflowStep.setOriginatingWorkflowStep(originalWorkflowStep);
                workflowStep.setOriginatingOrganization(requestingOrganization);
                
                
                workflowStep.setFieldProfiles(fieldProfiles);
                workflowStep.setFields(fields);
                
                
                workflowStep = workflowStepRepo.save(workflowStep);
                
                
                for(Organization organization : getContainingDescendantOrganization(requestingOrganization, originalWorkflowStep)) {
            		organization.replaceStepInWorkflow(originalWorkflowStep, workflowStep);
            		organizationRepo.save(organization);
            	}
                
                
                requestingOrganization.replaceStepInWorkflow(originalWorkflowStep, workflowStep);
                
                
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
    	
    	// allows for delete by iterating through findAll, while still deleting descendents
    	if(workflowStepRepo.findOne(workflowStep.getId()) != null) {
        
	        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
	        
	        originatingOrganization.removeWorkflowStep(workflowStep);
	                
	        if(workflowStep.getOriginatingWorkflowStep() != null) {
	            workflowStep.setOriginatingWorkflowStep(null);
	        }
	        
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
        if(organization.getWorkflow().contains(workflowStep)) {
        	descendantOrganizationsContainingWorkflowStep.add(organization);
        }
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
        	descendantOrganizationsContainingWorkflowStep.addAll(getContainingDescendantOrganization(descendantOrganization, workflowStep));
        });
        return descendantOrganizationsContainingWorkflowStep;
    }
    
}
