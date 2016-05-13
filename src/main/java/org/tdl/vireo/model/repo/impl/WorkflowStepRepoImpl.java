package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.tdl.vireo.model.repo.impl.exception.WorkflowStepNonOverrideableException;

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
    public WorkflowStep create(String name, Organization originatingOrganization)
    {
        return create(name, originatingOrganization, originatingOrganization.getWorkflowStepOrder().size());
    }
    
    @Override
    public WorkflowStep create(String name, Organization originatingOrganization, Integer orderIndex) {
        return create(name, originatingOrganization, orderIndex, null);
    }
    
    @Override
    public WorkflowStep create(String name, Organization originatingOrganization, Integer orderIndex, WorkflowStep originatingWorkflowStep) {
        WorkflowStep workflowStep = workflowStepRepo.save(new WorkflowStep(name, originatingOrganization));
        workflowStep.setOriginatingWorkflowStep(originatingWorkflowStep);
    
        // this might be needed, but breaks tests
        //workflowStep = workflowStepRepo.save(workflowStep);
        
        //recursive call
        originatingOrganization.addWorkflowStep(workflowStep, orderIndex);
        
        organizationRepo.save(originatingOrganization);
        
        return workflowStepRepo.findOne(workflowStep.getId());
    }
    
    @Override
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) throws WorkflowStepNonOverrideableException {
        //if the Org trying to update is the originating Org of the WorkflowStep, make the update.
        if(workflowStep.getOriginatingOrganization() != null && requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
            
            Organization originatingOrg = workflowStep.getOriginatingOrganization();
            
            //If the workflowStep is now non-overrideable, blow away descendants and change pointer to them to point to the non-overrideable
            if(workflowStep.getOverrideable() == false)
            {
                Set<WorkflowStep> derivativeWorkflowStepsToDelete = recursivelyReplaceDescendantStepsYieldingSetToDelete(originatingOrg, workflowStep);
                
                for(WorkflowStep ws : derivativeWorkflowStepsToDelete)
                {
                    workflowStepRepo.delete(ws);
                }
            }
            
            workflowStep = workflowStepRepo.save(workflowStep);
            
        }
        //else, if the child (non-originating) Org trying to update finds that the WorkflowStep is overrideable, make a new WorkflowStep for the update
        else if(workflowStep.getOverrideable()) {
            //create the new workflow step
            WorkflowStep newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), requestingOrganization, requestingOrganization.getPositionOfWorkflowStep(workflowStep), workflowStep);
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
            
            
            requestingOrganization.removeWorkflowStep(workflowStepRepo.findOne(workflowStep.getId()));
            requestingOrganization.removeWorkflowStepOrder(workflowStep.getId());
           
            //at the child organizations, make the new workflow step be at the same spot in the order as was its originating step
            for(Organization childOrg : requestingOrganization.getChildrenOrganizations())
            {
                recursivelyReplaceWSIdsInOrders(childOrg, workflowStep, newWorkflowStep);
            }

            organizationRepo.save(requestingOrganization);
            
            return newWorkflowStep;
        }
        //else, the requesting Org doesn't originate the step and can't override it, so throw an exception
        else
        {
            throw new WorkflowStepNonOverrideableException();
        }
         
        return workflowStep;
    }
    
    private void recursivelyReplaceWSIdsInOrders(Organization requestingOrganization, WorkflowStep oldWorkflowStep, WorkflowStep newWorkflowStep) {
        List<Long> newOrder = new ArrayList<Long>();
        
        for(int i = 0; i < requestingOrganization.getWorkflowStepOrder().size(); i++)
        {
            //if the workflow step at this point in the order is a descendant of the one being replaced, put pointer to the new one here
            if(workflowStepRepo.findOne(requestingOrganization.getWorkflowStepOrder().get(i)).descendsFrom(oldWorkflowStep))
            {
                newOrder.add(newWorkflowStep.getId());
            }
            //otherwise, retain the current step at this point in the order
            else
            {
                newOrder.add(requestingOrganization.getWorkflowStepOrder().get(i));
            }
        }
        requestingOrganization.setWorkflowStepOrder(newOrder);
        
        for(Organization childOrg : requestingOrganization.getChildrenOrganizations())
        {
            recursivelyReplaceWSIdsInOrders(childOrg, oldWorkflowStep, newWorkflowStep);
        }        
    }

    @Override
    public void delete(WorkflowStep workflowStep) {
    	    
        if(workflowStep == null || workflowStepRepo.findOne(workflowStep.getId()) == null) return;
    	    
        Organization originatingOrganization = workflowStep.getOriginatingOrganization();
        if(originatingOrganization != null) {
            originatingOrganization.removeWorkflowStep(workflowStep);
            organizationRepo.save(originatingOrganization);
        }
        
        for(Organization organization : workflowStep.getContainedByOrganizations()) {
            organization.removeWorkflowStepOrder(workflowStep.getId());
            organization.removeWorkflowStep(workflowStep);
            organizationRepo.save(organization);
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
    
    private Set<WorkflowStep> recursivelyReplaceDescendantStepsYieldingSetToDelete(Organization organization, WorkflowStep originalStep)
    {
        Set<WorkflowStep> derivedStepsToDelete = new HashSet<WorkflowStep>();
        
        //find the workflow step, if any, that descends from the original
        for(WorkflowStep ws : organization.getWorkflowSteps())
        {
            if(ws.descendsFrom(originalStep))
            {
                derivedStepsToDelete.add(ws);
                organization.replaceWorkflowStep(ws, originalStep);
            }
        }
        
        for(Organization childOrg : organization.getChildrenOrganizations())
        {
            derivedStepsToDelete.addAll(
                    recursivelyReplaceDescendantStepsYieldingSetToDelete(childOrg, originalStep));
        }
        
        return derivedStepsToDelete;
    }
}
