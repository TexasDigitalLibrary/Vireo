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
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public class NoteRepoImpl implements NoteRepoCustom {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private NoteRepo noteRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Override
    public Note create(WorkflowStep originatingWorkflowStep, String name, String text) {
        Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text));
        originatingWorkflowStep.addOriginalNote(note);
        workflowStepRepo.save(originatingWorkflowStep);
        return noteRepo.findOne(note.getId());
    }

    @Override
    public Note create(WorkflowStep originatingWorkflowStep, String name, String text, Boolean overrideable) {
        Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text, overrideable));
        originatingWorkflowStep.addOriginalNote(note);
        workflowStepRepo.save(originatingWorkflowStep);
        return noteRepo.findOne(note.getId());
    }

    public void removeFromWorkflowStep(Organization requestingOrganization, WorkflowStep pendingWorkflowStep, Note noteToRemove) throws WorkflowStepNonOverrideableException, NoteNonOverrideableException {

        // if requesting organization originates the workflow step or the workflow step is overrideable,
        if (pendingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || pendingWorkflowStep.getOverrideable()) {
            // ... and if also that workflow step originates the note or the note is overrideable,
            if (noteToRemove.getOriginatingWorkflowStep().getId().equals(noteToRemove.getId()) || noteToRemove.getOverrideable()) {
                // ...then the update is permissible.

                // if requesting organization is not the workflow step's orignating organization,
                if (!pendingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
                    // create a new workflow step
                    pendingWorkflowStep = workflowStepRepo.update(pendingWorkflowStep, requestingOrganization);

                    // recursive call
                    pendingWorkflowStep.removeAggregateNote(noteToRemove);

                    workflowStepRepo.save(pendingWorkflowStep);
                }
                // else, requesting organization originates the workflow step
                else {

                    List<WorkflowStep> workflowStepsContainingNote = getContainingDescendantWorkflowStep(requestingOrganization, noteToRemove);

                    if (workflowStepsContainingNote.size() > 0) {

                        boolean foundNewOriginalOwner = false;

                        for (WorkflowStep workflowStepContainingNote : workflowStepsContainingNote) {
                            // add note as original to first workflow step
                            if (!foundNewOriginalOwner) {
                                workflowStepContainingNote.addOriginalNote(noteToRemove);
                                foundNewOriginalOwner = true;
                            } else {
                                workflowStepContainingNote.addAggregateNote(noteToRemove);
                            }
                            workflowStepRepo.save(workflowStepContainingNote);
                        }

                        pendingWorkflowStep.removeOriginalNote(noteToRemove);

                        workflowStepRepo.save(pendingWorkflowStep);

                    } else {
                        noteRepo.delete(noteToRemove);
                    }
                }
            } // workflow step doesn't originate the note and it is non-overrideable
            else {
                throw new NoteNonOverrideableException();
            }
        } // requesting org doesn't originate the note's workflow step, and the workflow step is non-overrideable
        else {
            throw new WorkflowStepNonOverrideableException();
        }
    }

    public Note update(Note pendingNote, Organization requestingOrganization) throws NoteNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Note resultingNote = null;

        Note persistedNote = noteRepo.findOne(pendingNote.getId());

        boolean overridabilityOfPersistedNote = persistedNote.getOverrideable();

        boolean overridabilityOfOriginatingWorkflowStep = persistedNote.getOriginatingWorkflowStep().getOverrideable();

        // The ws that has the note on the requesting org
        WorkflowStep workflowStepWithNoteOnRequestingOrganization = null;

        // Did the requesting organization originate the workflow step that the note is on?
        boolean requestingOrganizationOriginatedWorkflowStep = false;

        // Is the workflow step on which the note is found on the requesting organization the workflow step that originates the note?
        boolean workflowStepOriginatesNote = false;

        for (WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
            if (workflowStep.getAggregateNotes().contains(persistedNote)) {
                workflowStepWithNoteOnRequestingOrganization = workflowStep;
                requestingOrganizationOriginatedWorkflowStep = workflowStepWithNoteOnRequestingOrganization.getOriginatingOrganization().getId().equals(requestingOrganization.getId());
                break;
            }
        }

        // A workflow step that has the note on it was found on the requesting organization
        if (workflowStepWithNoteOnRequestingOrganization != null) {
            workflowStepOriginatesNote = persistedNote.getOriginatingWorkflowStep().getId().equals(workflowStepWithNoteOnRequestingOrganization.getId());
        } else {
            // The requesting org doesn't even have this note anywhere!
            throw new ComponentNotPresentOnOrgException();
        }

        if (!overridabilityOfOriginatingWorkflowStep && !requestingOrganizationOriginatedWorkflowStep) {
            throw new WorkflowStepNonOverrideableException();
        }

        if (!overridabilityOfPersistedNote && !(workflowStepOriginatesNote && requestingOrganizationOriginatedWorkflowStep)) {
            throw new NoteNonOverrideableException();
        }

        // If the requesting org originates the WS, then we don't need to make a new one
        if (requestingOrganizationOriginatedWorkflowStep) {
            // If the WS originates the Note, we don't need a new one
            if (workflowStepOriginatesNote) {

                // update note directly
                resultingNote = noteRepo.save(pendingNote);

                // if change to non-overrideable, replace descendants of this note in subordinate orgs and put it back on ones that deleted it
                if (overridabilityOfPersistedNote && !resultingNote.getOverrideable()) {
                    reInheritDescendantsOfNoteWithAnotherNoteUnderWS(persistedNote, resultingNote, workflowStepWithNoteOnRequestingOrganization, requestingOrganization);
                }
            }
            // If the WS didn't originate the Note, we need a new Note to replace it
            else {

                // new note
                em.detach(pendingNote);
                pendingNote.setOriginatingNote(persistedNote);
                pendingNote.setId(null);
                pendingNote.setOriginatingWorkflowStep(workflowStepWithNoteOnRequestingOrganization);
                Note newNote = noteRepo.save(pendingNote);

                // replace descendants of the persisted (original) Note with our new Note at subordinate organizations
                // replace the note on all descendant orgs aggregate workflows
                for (WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedNote)) {
                    workflowStep.replaceAggregateNote(persistedNote, newNote);
                    workflowStepRepo.save(workflowStep);
                }

                // if change to non-overrideable, replace descendants of originating note in subordinate orgs
                if (overridabilityOfPersistedNote && !newNote.getOverrideable()) {
                    reInheritDescendantsOfNoteWithAnotherNoteUnderWS(persistedNote, newNote, workflowStepWithNoteOnRequestingOrganization, requestingOrganization);
                }
            }
        }
        // If the requesting org didn't originate the WS, we need a new WS to replace it and to originate a new Note
        // workflowStepWithNoteOnRequestingOrganization does not originate on the requesting org
        else {

            // make the new step; the update call will propagate step replacement in subordinate orgs
            Long origWSId = workflowStepWithNoteOnRequestingOrganization.getId();
            workflowStepWithNoteOnRequestingOrganization.setOriginatingWorkflowStep(workflowStepRepo.findOne(origWSId));

            WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(workflowStepWithNoteOnRequestingOrganization, requestingOrganization);
            
            workflowStepWithNoteOnRequestingOrganization = workflowStepRepo.findOne(origWSId);
            requestingOrganization = organizationRepo.findOne(requestingOrganization.getId());

            // new Note on the new WS
            em.detach(pendingNote);
            pendingNote.setId(null);
            pendingNote.setOriginatingNote(persistedNote);
            pendingNote.setOriginatingWorkflowStep(newOriginatingWorkflowStep);
            Note newNote = noteRepo.save(pendingNote);
            newOriginatingWorkflowStep.getOriginalNotes().add(newNote);
            newOriginatingWorkflowStep.replaceAggregateNote(persistedNote, newNote);
            newOriginatingWorkflowStep = workflowStepRepo.save(newOriginatingWorkflowStep);

            requestingOrganization.replaceAggregateWorkflowStep(workflowStepWithNoteOnRequestingOrganization, newOriginatingWorkflowStep);
            requestingOrganization = organizationRepo.save(requestingOrganization);

            // replace the note on all descendant orgs aggregate workflows
            for (WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedNote)) {
                workflowStep.replaceAggregateNote(persistedNote, newNote);
                workflowStepRepo.save(workflowStep);
            }

            // if parent organization's workflow step updates a note originating form a descendent, the original note needs to be deleted
            if (workflowStepRepo.findByAggregateNotesId(persistedNote.getId()).size() == 0) {
                noteRepo.delete(persistedNote);
            } else {
                newNote.setOriginatingNote(persistedNote);
                newNote = noteRepo.save(newNote);
            }

            // if change to non-overrideable, replace descendants of originating note in subordinate orgs
            if (overridabilityOfPersistedNote && !newNote.getOverrideable()) {
                reInheritDescendantsOfNoteWithAnotherNoteUnderWS(persistedNote, newNote, workflowStepWithNoteOnRequestingOrganization, requestingOrganization);
            }

            resultingNote = newNote;
        }

        return resultingNote;

    }

    @Override
    public void delete(Note note) {

        // allows for delete by iterating through findAll, while still deleting descendents
        if (noteRepo.findOne(note.getId()) != null) {

            WorkflowStep originatingWorkflowStep = note.getOriginatingWorkflowStep();

            originatingWorkflowStep.removeOriginalNote(note);

            if (note.getOriginatingNote() != null) {
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

    /**
     * Gets a list of WorkflowSteps on the org and its descendants that contain a given note
     * 
     * @param organization
     * @param note
     * @return
     */
    private List<WorkflowStep> getContainingDescendantWorkflowStep(Organization organization, Note note) {
        List<WorkflowStep> descendantWorkflowStepsContainingNote = new ArrayList<WorkflowStep>();
        organization.getAggregateWorkflowSteps().forEach(ws -> {
            if (ws.getAggregateNotes().contains(note)) {
                descendantWorkflowStepsContainingNote.add(ws);
            }
        });
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
            descendantWorkflowStepsContainingNote.addAll(getContainingDescendantWorkflowStep(descendantOrganization, note));
        });
        return descendantWorkflowStepsContainingNote;
    }

    // TODO: same logic here as in WorkflowStepRepoImpl.getDescendantsOfStep
    private List<Note> getDescendantsOfNote(Note note) {
        List<Note> descendantNotes = new ArrayList<Note>();
        List<Note> currentDescendants = noteRepo.findByOriginatingNote(note);
        descendantNotes.addAll(currentDescendants);
        currentDescendants.forEach(descendantNote -> {
            descendantNotes.addAll(getDescendantsOfNote(descendantNote));
        });
        return descendantNotes;
    }

    /**
     * Have all the notes (in workflow steps descended from a given step) that are derived from a particular ancestor note be replaced with a replacement note (which could also be just that ancestor note)
     * 
     */

    private void reInheritDescendantsOfNoteWithAnotherNoteUnderWS(Note ancestorNote, Note replacementNote, WorkflowStep workflowStepWithNoteOnRequestingOrganization, Organization requestingOrganization) {
        // First off, note the Notes that descend from the ancestor note
        List<Note> descendantNotes = getDescendantsOfNote(ancestorNote);

        // For every workflow step derived off the step in question...
        // for(WorkflowStep ws : workflowStepRepo.getDescendantsOfStep(workflowStepWithNoteOnRequestingOrganization)) {
        List<Note> notesToDelete = new ArrayList<Note>();

        for (WorkflowStep ws : workflowStepRepo.getDescendantsOfStepUnderOrganization(workflowStepWithNoteOnRequestingOrganization, requestingOrganization)) {
        
            boolean aggregatesNoteOrDescendant = ws.getAggregateNotes().contains(replacementNote);
            // For every note on that step (the aggregates will include the originals)
            List<Note> copyOfAggregatedNotes = new ArrayList<Note>();
            copyOfAggregatedNotes.addAll(ws.getAggregateNotes());

            for (Note n : copyOfAggregatedNotes) {
                // If that note is a descendant of the note in question, replace it with the note in question and get rid of it
                if (descendantNotes.contains(n) && !replacementNote.equals(n)) {
                    if (ws.replaceAggregateNote(n, replacementNote)) {
                        ws.removeOriginalNote(n);
                        workflowStepRepo.save(ws);
                        notesToDelete.add(n);
                        ws = workflowStepRepo.findOne(ws.getId());
                        aggregatesNoteOrDescendant = true;
                    }
                }
            }

            // If the note was not found on the aggregates at all, then add it back in
            if (!aggregatesNoteOrDescendant) {
                ws.addAggregateNote(replacementNote);
                workflowStepRepo.save(ws);
            }
        }
        for (Note n : notesToDelete) {
            delete(n);
        }
    }

}
