package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;


public interface FieldProfileRepoCustom {
    
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional);

    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional);
    
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional);

    public FieldProfile update(FieldProfile fieldProfile, Organization requestingOrganization);
    
    public void delete(FieldProfile fieldProfile);
    
}
