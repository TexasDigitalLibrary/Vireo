package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public class FieldProfileRepoImpl implements FieldProfileRepoCustom {

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Override
    public FieldProfile create(WorkflowStep workflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean enabled, Boolean optional) {
        return create(workflowStep, fieldProfileRepo.save(new FieldProfile(workflowStep, fieldPredicate, inputType, repeatable, enabled, optional)));
    }

    @Override
    public FieldProfile create(WorkflowStep workflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean enabled, Boolean optional) {
        return create(workflowStep, fieldProfileRepo.save(new FieldProfile(workflowStep, fieldPredicate, inputType, usage, repeatable, enabled, optional)));
    }
    
    @Override
    public FieldProfile create(WorkflowStep workflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean enabled, Boolean optional) {
        return create(workflowStep, fieldProfileRepo.save(new FieldProfile(workflowStep, fieldPredicate, inputType, usage, help, repeatable, enabled, optional)));
    }

    private FieldProfile create(WorkflowStep workflowStep, FieldProfile detachedFieldProfile) {
        workflowStep.addFieldProfile(detachedFieldProfile);
        workflowStep.addOriginalFieldProfile(detachedFieldProfile);
        workflowStepRepo.save(workflowStep);
        return fieldProfileRepo.findOne(detachedFieldProfile.getId());
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
