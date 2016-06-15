package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionWorkflowStep;

public interface SubmissionFieldProfileRepoCustom {
    
    public SubmissionFieldProfile create(SubmissionWorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean optional);

    public SubmissionFieldProfile create(SubmissionWorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean optional);
    
    public SubmissionFieldProfile create(SubmissionWorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean optional);
    
}
