package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
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
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Override
    public WorkflowStep create(String name, Organization originatingOrganization) {
        WorkflowStep workflowStep = workflowStepRepo.save(new WorkflowStep(name, originatingOrganization));
//        System.out.println("Call to create: on WorkflowStep creation set to name: " + name + " origin " + originatingOrganization.getName());
        
        //recursive call
        originatingOrganization.addWorkflowStep(workflowStep);
        
//        System.out.println("Originating org now has workflow step of id " + originatingOrganization.getWorkflowSteps().get(0).getId() + " i.e. " + workflowStep.getId());
        organizationRepo.save(originatingOrganization);
        
        return workflowStepRepo.findOne(workflowStep.getId());
    }
    
    @Override
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) throws WorkflowStepNonOverrideableException {
//        System.out.println("Update on workflow step ID " + workflowStep.getId() + " named "+ workflowStep.getName());
        
        //if the Org trying to update is the originating Org of the WorkflowStep, make the update.
        if(workflowStep.getOriginatingOrganization() != null && requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            //em.merge(workflowStep);
            workflowStep = workflowStepRepo.findOne(workflowStep.getId());
            workflowStep = workflowStepRepo.save(workflowStep);
        //else, if the child (non-originating) Org trying to update finds that the WorkflowStep is overrideable, make a new WorkflowStep for the update
        } else if(workflowStep.getOverrideable()) {
//            System.out.println("making new workflow step");
            //lose the pointer to the original workflow step
            requestingOrganization.removeWorkflowStep(workflowStepRepo.findOne(workflowStep.getId()));
            
            //refresh and reattach for this context
            //workflowStep = workflowStepRepo.findOne(workflowStep.getId());
                    
            //create the new workflow step
            WorkflowStep newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), requestingOrganization);
//            System.out.println("Created new workflowStep of id " + newWorkflowStep.getId());
            
            
            //make the new workflow step remember from whence it was derived
            newWorkflowStep.setOriginatingWorkflowStep(workflowStep.getOriginatingWorkflowStep());

            //make the new workflow step note that it both originates in and is contained by the requesting organization
            newWorkflowStep.addContainedByOrganization(requestingOrganization);
            newWorkflowStep.setOriginatingOrganization(requestingOrganization);
            
            //make all the field profiles remember from whence they came, and put them on the new workflow step
            for(FieldProfile fieldProfile : workflowStep.getFieldProfiles()) {
                fieldProfile.setOriginatingWorkflowStep(workflowStep);
                newWorkflowStep.addFieldProfile(fieldProfile);
            }
           
            //make the requesting organization contain the new workflow step
            requestingOrganization.addWorkflowStep(newWorkflowStep);
            
            //make the new workflow step be at the same spot in the order as was its originating step
            ArrayList<Long> newOrder = new ArrayList<Long>();
            for(int i = 0; i < requestingOrganization.getWorkflowStepOrder().size(); i++)
            {
                if(requestingOrganization.getWorkflowStepOrder().get(i).equals(workflowStep.getId()))
                {
                    newOrder.add(newWorkflowStep.getId());
                }
                else
                {
                    newOrder.add(requestingOrganization.getWorkflowStepOrder().get(i));
                }
            }
            requestingOrganization.setWorkflowStepOrder(newOrder);
            
            organizationRepo.save(requestingOrganization);
//            System.out.println("Created new workflowStep of id " + newWorkflowStep.getId());
            
            //refresh and reattach for this context
            //workflowStep = workflowStepRepo.findOne(workflowStep.getId());
            
            return newWorkflowStep;

        }
        //else, the requesting Org doesn't originate the step and can't override it, so throw an exception
        else
        {
            throw new WorkflowStepNonOverrideableException();
        }
         
        return workflowStep;
    }
    
    @Override
    public void delete(WorkflowStep workflowStep) {
    	    
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        if(originatingOrganization != null) {
            originatingOrganization.removeWorkflowStep(workflowStep);
            //organizationRepo.save(originatingOrganization);
        }
        
        for(Organization organization : workflowStep.getContainedByOrganizations()) {
            organization.removeWorkflowStep(workflowStep);
            //organizationRepo.save(organization);
        }
        
        workflowStep.setContainedByOrganizations(null);
        
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
    
    private void recursivelyRemoveDescendantSteps(Organization organization, WorkflowStep originalStep)
    {
        //TODO:
        //find the workflow step, if any, that descends from the original
        
        //have the organization point at the originating step
        
        //delete the descendant
        
        for(Organization org : organization.getChildrenOrganizations())
        {
            recursivelyRemoveDescendantSteps(org, originalStep);
        }
    }
}
