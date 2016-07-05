package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.impl.ComponentNotPresentOnOrgException;
import org.tdl.vireo.model.repo.impl.NoteNonOverrideableException;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

public interface NoteRepoCustom {

    public Note create(WorkflowStep originatingWorkflowStep, String name, String text);
    
    public Note create(WorkflowStep originatingWorkflowStep, String name, String text, Boolean overrideable);
    
    public void removeFromWorkflowStep(Organization requestingOrganization, WorkflowStep requestingWorfklowStep, Note noteToRemove) throws NoteNonOverrideableException, WorkflowStepNonOverrideableException;

    public Note update(Note note, Organization requestingOrganization) throws NoteNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;
    
    public void delete(Note note);

}
