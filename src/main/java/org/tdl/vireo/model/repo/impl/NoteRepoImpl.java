package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public class NoteRepoImpl implements NoteRepoCustom {
    
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private NoteRepo noteRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Override
    public Note create(WorkflowStep originatingWorkflowStep, String name, String text) {
        Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text));
        originatingWorkflowStep.addOriginalNote(note);
        workflowStepRepo.save(originatingWorkflowStep);
        return noteRepo.findOne(note.getId());
    }
    
    public void disinheritFromWorkflowStep(Organization requestingOrganization, WorkflowStep workflowStep, Note noteToDisinherit) throws WorkflowStepNonOverrideableException, NoteNonOverrideableException {
        
        if(workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || workflowStep.getOverrideable()) {
            
            if(noteToDisinherit.getOriginatingWorkflowStep().getId().equals(noteToDisinherit.getId()) || noteToDisinherit.getOverrideable()) {
            
                // if requesting organization is not the workflow step's orignating organization                        
                if(!workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
                    // create a new workflow step
                    workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
                    
                    workflowStep.removeAggregateNote(noteToDisinherit);
                    
                    workflowStepRepo.save(workflowStep);
                }
                else {
                    
                    List<WorkflowStep> workflowStepsContainingNote = getContainingDescendantWorkflowStep(requestingOrganization, noteToDisinherit);
                    
                    if(workflowStepsContainingNote.size() > 0) {
                        
                        boolean foundNewOriginalOwner = false;
                        
                        for(WorkflowStep workflowStepContainingNote : workflowStepsContainingNote) {
                            // add field profile as original to first workflow step
                            if(!foundNewOriginalOwner) {
                                workflowStepContainingNote.addOriginalNote(noteToDisinherit);
                                foundNewOriginalOwner = true;
                            }
                            else {
                                workflowStepContainingNote.addAggregateNote(noteToDisinherit);
                            }
                            workflowStepRepo.save(workflowStepContainingNote);
                        }
                        
                        workflowStep.removeOriginalNote(noteToDisinherit);
                        
                        workflowStepRepo.save(workflowStep);
                        
                    }
                    else {            
                        noteRepo.delete(noteToDisinherit);
                    }
                }
            }
            else {
                throw new NoteNonOverrideableException();
            }
        }
        else {
            throw new WorkflowStepNonOverrideableException();
        }
        
    }
    
    public Note update(Note note, Organization requestingOrganization) throws NoteNonOverrideableException, WorkflowStepNonOverrideableException {
        
        //if the requesting organization does not originate the step that originates the note, and it is non-overrideable, then throw an exception.
        boolean requestorOriginatesNote = false;
        
        for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {           
            //if this step of the requesting organization happens to be the originator of the field profile, and the step also originates in the requesting organization, then this organization truly originates the field profile.
            if(note.getOriginatingWorkflowStep().getId().equals(workflowStep.getId()) && requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
                requestorOriginatesNote = true;
            }
        }
        
        //if the requestor is not the originator and it is not overrideable, we can't make the update
        if(!requestorOriginatesNote && !note.getOverrideable()) {
            
            // provide feedback of attempt to override non overrideable
            // exceptions may be of better use for unavoidable error handling
            
            throw new NoteNonOverrideableException();
        }
        //if the requestor is not originator, and the step the profile's on is not overrideable, we can't make the update
        else if(!requestorOriginatesNote && !note.getOriginatingWorkflowStep().getOverrideable())
        {
            throw new WorkflowStepNonOverrideableException();
        }
        //if the requestor originates, make the update at the requestor
        else if(requestorOriginatesNote) {
            // do nothing, just save changes
            note = noteRepo.save(note);
        }
        //else, it's overrideable and we didn't oringinate it so we need to make a new one that overrides.
        else {
            
            Long originalNoteId = note.getId();
            
            em.detach(note);
            note.setId(null);
            
            
            Note originalNote = noteRepo.findOne(originalNoteId);
             
            
            WorkflowStep originalOriginatingWorkflowStep = originalNote.getOriginatingWorkflowStep();
            
            // when a organization that did not originate the workflow step needs to update the field profile with the step,
            // a new workflow step must be created with the requesting organization as the originator           
            if(!originalOriginatingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
                
                WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(originalOriginatingWorkflowStep, requestingOrganization);
                
                note.setOriginatingWorkflowStep(newOriginatingWorkflowStep);
            }
            
            
            note.setOriginatingNote(null);
                        
            note = noteRepo.save(note);
            
            
            for(WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, originalNote)) {
                workflowStep.replaceAggregateNote(originalNote, note);
                workflowStepRepo.save(workflowStep);
            }
            
            
            for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
                if(workflowStep.getAggregateNotes().contains(originalNote)) {
                    workflowStep.replaceAggregateNote(originalNote, note);
                    workflowStepRepo.save(workflowStep);
                }
            }
            
            
            // if parent organization's workflow step updates a field profile originating form a descendent, the original field profile need to be deleted
            if(workflowStepRepo.findByAggregateNotesId(originalNote.getId()).size() == 0) {
                noteRepo.delete(originalNote);
            }
            else {
                note.setOriginatingNote(originalNote);
                note = noteRepo.save(note);
            }
            
        }
        
        return note;

    }

    @Override
    public void delete(Note note) {
        
        // allows for delete by iterating through findAll, while still deleting descendents
        if(noteRepo.findOne(note.getId()) != null) {
        
            WorkflowStep originatingWorkflowStep = note.getOriginatingWorkflowStep();
            
            originatingWorkflowStep.removeOriginalNote(note);
            
            if(note.getOriginatingNote() != null) {
                note.setOriginatingNote(null);
            }
            
            workflowStepRepo.findByAggregateNotesId(note.getId()).forEach(workflowStep -> {
                workflowStep.removeAggregateNote(note);
                workflowStepRepo.save(workflowStep);
            });
            
            noteRepo.findByOriginatingNote(note).forEach(fp -> {
                fp.setOriginatingNote(null);
            });
            
            deleteDescendantsOfNote(note);
            
            noteRepo.delete(note.getId());
        
        }
    }
    
    private void deleteDescendantsOfNote(Note note) {
        noteRepo.findByOriginatingNote(note).forEach(desendantNote -> {
            delete(desendantNote);
        });
    }
    
    private List<WorkflowStep> getContainingDescendantWorkflowStep(Organization organization, Note note) {
        List<WorkflowStep> descendantWorkflowStepsContainingNote = new ArrayList<WorkflowStep>();
        organization.getAggregateWorkflowSteps().forEach(ws -> {
            if(ws.getAggregateNotes().contains(note)) {
                descendantWorkflowStepsContainingNote.add(ws);
            }
        });
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
            descendantWorkflowStepsContainingNote.addAll(getContainingDescendantWorkflowStep(descendantOrganization, note));
        });
        return descendantWorkflowStepsContainingNote;
    }

}
