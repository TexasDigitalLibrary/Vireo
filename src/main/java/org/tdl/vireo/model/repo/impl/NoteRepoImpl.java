package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.inheritence.HeritableRepo;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public class NoteRepoImpl extends HeritableRepo<Note, NoteRepo> implements NoteRepoCustom {

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

    @Override
    public Note create(WorkflowStep originatingWorkflowStep, String name, String text, Boolean overrideable) {
        Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text, overrideable));
        originatingWorkflowStep.addOriginalNote(note);
        workflowStepRepo.save(originatingWorkflowStep);
        return noteRepo.findOne(note.getId());
    }

    @Override
    public List<Note> findByOriginating(Note originatingNote) {
        return noteRepo.findByOriginatingNote(originatingNote);
    }

}
