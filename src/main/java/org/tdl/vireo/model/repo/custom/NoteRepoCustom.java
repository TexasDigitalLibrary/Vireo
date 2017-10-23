package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.HeritableModelNonOverrideableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;

public interface NoteRepoCustom {

	public Note create(WorkflowStep originatingWorkflowStep, String name, String text);

	public Note create(WorkflowStep originatingWorkflowStep, String name, String text, Boolean overrideable);

	public void removeFromWorkflowStep(Organization requestingOrganization, WorkflowStep requestingWorfklowStep, Note noteToRemove) throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;

	public Note update(Note note, Organization requestingOrganization) throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;

}
