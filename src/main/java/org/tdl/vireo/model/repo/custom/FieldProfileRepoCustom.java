package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.impl.ComponentNotPresentOnOrgException;
import org.tdl.vireo.model.repo.impl.HeritableModelNonOverrideableException;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

public interface FieldProfileRepoCustom {
    
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional);

    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional);
    
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional);
    
    public void removeFromWorkflowStep(Organization requestingOrganization, WorkflowStep requestingWorfklowStep, FieldProfile fieldProfileToDisinherit) throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException;

    public FieldProfile update(FieldProfile fieldProfile, Organization requestingOrganization) throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;
    
    public void delete(FieldProfile fieldProfile);
    
    public List<FieldProfile> findByOriginating(FieldProfile originatingFieldProfile);
    
}
