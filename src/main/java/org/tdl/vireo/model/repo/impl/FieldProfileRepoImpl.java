package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
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
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        return fieldProfile;
    }

    @Override
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        return fieldProfile;
    }
    
    @Override
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        return fieldProfile;
    }
    
    // TODO: this method needs to handle all inheretence and aggregation duties
    public FieldProfile update(FieldProfile fieldProfile, Organization requestingOrganization) {
        return null;
    }

    @Override
    public void delete(FieldProfile fieldProfile) {
        if(fieldProfileRepo.findOne(fieldProfile.getId()) == null)
            return;
        
    	WorkflowStep originatingWorkflowStep = fieldProfile.getOriginatingWorkflowStep();
    	
    	if(originatingWorkflowStep != null) {
    	    originatingWorkflowStep.removeFieldProfile(fieldProfile);
    	    workflowStepRepo.save(originatingWorkflowStep);
    	}
    	
    	fieldProfileRepo.delete(fieldProfile.getId());
    	
    }
    
}
