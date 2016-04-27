package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public class WorkflowStepRepoImpl implements WorkflowStepRepoCustom {
	
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Override
    public WorkflowStep create(String name, Organization originatingOrganization) {
        WorkflowStep workflowStep = workflowStepRepo.save(new WorkflowStep(name, originatingOrganization));
        originatingOrganization.addWorkflowStep(workflowStep);
        organizationRepo.save(originatingOrganization);
        return workflowStepRepo.findOne(workflowStep.getId());
    }
    
    @Override
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) {
        
        if(requestingOrganization.getId() == workflowStep.getOriginatingOrganization().getId()) {
            workflowStep = workflowStepRepo.save(workflowStep);
        } else if(workflowStep.getOptional()) {
            
            requestingOrganization.removeWorkflowStep(workflowStepRepo.findOne(workflowStep.getId()));
            
            WorkflowStep newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), requestingOrganization);
            
            //refreshed (and reattached)
            workflowStep = workflowStepRepo.findOne(workflowStep.getId());
            
            newWorkflowStep.setOriginatingWorkflowStep(workflowStep);
            
            for(FieldProfile fieldProfile : workflowStep.getFieldProfiles()) {
                fieldProfile.setOriginatingWorkflowStep(workflowStep);
                newWorkflowStep.addFieldProfile(fieldProfile);
            }
           
            workflowStep = workflowStepRepo.save(newWorkflowStep);
            
        }
         
        return workflowStep;
    }
    
    @Override
    public void delete(WorkflowStep workflowStep) {
    	
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        
        if(originatingOrganization != null) {
            originatingOrganization.removeWorkflowStep(workflowStep);
            organizationRepo.save(originatingOrganization);
        }
        
        for(Organization organization : workflowStep.getContainedByOrganizations()) {
            workflowStep.removeContainedByOrganization(organization);
            organization.removeWorkflowStep(workflowStep);
            organizationRepo.save(organization);
        }
        
        
        List<FieldProfile> fieldProfilesToRemove = new ArrayList<FieldProfile>();
        List<FieldProfile> fieldProfilesToDelete = new ArrayList<FieldProfile>();
        
        
        for(FieldProfile fieldProfile : workflowStep.getFieldProfiles()) {
        	
        	fieldProfilesToRemove.add(fieldProfile);
        	
        	WorkflowStep originatingWorkflowStep = fieldProfile.getOriginatingWorkflowStep();
            
            if(originatingWorkflowStep.getId().equals(workflowStep.getId())) {    
                fieldProfilesToDelete.add(fieldProfile);
            }
        }
        
        for(FieldProfile fieldProfile : fieldProfilesToRemove) {
            workflowStep.removeFieldProfile(fieldProfile);
        }
        
        
        for(FieldProfile fieldProfile : fieldProfilesToDelete) {
        	recursivelyRemoveFieldProfile(originatingOrganization, fieldProfile);
            fieldProfileRepo.delete(fieldProfile);            
        }
        
        workflowStepRepo.delete(workflowStep.getId());
        
    }

    private void recursivelyRemoveFieldProfile(Organization organization, FieldProfile targetFieldProfile) {
    	
        for(WorkflowStep workflowStep : organization.getWorkflowSteps()) {
            
            boolean removedFieldProfile = false;
            
            for(FieldProfile fieldProfile: workflowStep.getFieldProfiles()) {
                
                if(fieldProfile.getId().equals(targetFieldProfile.getId())) {
                                        
                    workflowStep.removeFieldProfile(fieldProfile);
                    
                    removedFieldProfile = true;
                }
                
            }
            
            if(removedFieldProfile) {
                workflowStepRepo.save(workflowStep);
            }
        }
        
        organization.getChildrenOrganizations().parallelStream().forEach(child -> {
            recursivelyRemoveFieldProfile(child, targetFieldProfile);
        });
        
    }

}
