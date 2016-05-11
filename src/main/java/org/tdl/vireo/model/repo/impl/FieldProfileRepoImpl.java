package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;
import org.tdl.vireo.model.repo.impl.exception.FieldProfileNonOverrideableException;

public class FieldProfileRepoImpl implements FieldProfileRepoCustom {

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    

    @Override
    public FieldProfile create(WorkflowStep workflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        return create(workflowStep, new FieldProfile(workflowStep, fieldPredicate, inputType, repeatable, overrideable, enabled, optional));
    }

    @Override
    public FieldProfile create(WorkflowStep workflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        return create(workflowStep, new FieldProfile(workflowStep, fieldPredicate, inputType, usage, repeatable, overrideable, enabled, optional));
    }
    
    @Override
    public FieldProfile create(WorkflowStep workflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        return create(workflowStep, new FieldProfile(workflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional));
    }

    private FieldProfile create(WorkflowStep workflowStep, FieldProfile detachedFieldProfile) {
        FieldProfile newFieldProfile = fieldProfileRepo.save(detachedFieldProfile);
        workflowStep.addFieldProfile(newFieldProfile);
        workflowStep.addOriginalFieldProfile(newFieldProfile);
        workflowStepRepo.save(workflowStep);
        return fieldProfileRepo.findOne(newFieldProfile.getId());
    }
 
    public FieldProfile update(FieldProfile fieldProfile, Organization requestingOrganization) throws FieldProfileNonOverrideableException {
        //if the requesting organization does not originate the step that originates the fieldProfile, and it is non-overrideable, then throw an exception.
        boolean requestorOriginatesProfile = false;
        for(WorkflowStep prospectiveOriginatingStepOfFieldProfile : requestingOrganization.getWorkflowSteps())
        {
            //if this step of the requesting organization happens to be the originator of the field profile, and the step also originates in the requesting organization, then this organization truly originates the field profile.
            if(fieldProfile.getOriginatingWorkflowStep().equals(prospectiveOriginatingStepOfFieldProfile) && 
               requestingOrganization.equals(prospectiveOriginatingStepOfFieldProfile.getOriginatingOrganization()))
            {
                requestorOriginatesProfile=true;
            }
        }
        
        //if the requestor is not the originator and it is not overrideable, we can't make the update
        if(requestorOriginatesProfile == false && fieldProfile.getOverrideable() == false)
        {
            throw new FieldProfileNonOverrideableException();
        }
        //if the requestor originates, make the update at the requestor
        else if(requestorOriginatesProfile == true)
        {
            fieldProfile = fieldProfileRepo.save(fieldProfile);
            return fieldProfile;
        }
        //else, it's overrideable and we didn't oringinate it so we need to make a new one that overrides.
        else
        {
            //TODO:
            return null;
        }
        
        
    }
    
    @Override
    public void delete(FieldProfile fieldProfile) {
        WorkflowStep originatingWorkflowStep = fieldProfile.getOriginatingWorkflowStep();
        
        originatingWorkflowStep.removeFieldProfile(fieldProfile);
        originatingWorkflowStep.removeOriginalFieldProfile(fieldProfile);
        
        workflowStepRepo.save(originatingWorkflowStep);
        
        if(fieldProfileRepo.findOne(fieldProfile.getId()) != null) {
            fieldProfileRepo.delete(fieldProfile.getId());
        }
    }

}
