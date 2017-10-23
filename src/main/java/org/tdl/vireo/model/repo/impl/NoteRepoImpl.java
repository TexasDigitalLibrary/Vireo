package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.inheritance.HeritableRepoImpl;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public class NoteRepoImpl extends HeritableRepoImpl<Note, NoteRepo> implements NoteRepoCustom {

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
        organizationRepo.broadcast(organizationRepo.findAllByOrderByIdAsc());
        return noteRepo.findOne(note.getId());
    }

    @Override
    public Note create(WorkflowStep originatingWorkflowStep, String name, String text, Boolean overrideable) {
        Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text, overrideable));
        originatingWorkflowStep.addOriginalNote(note);
        workflowStepRepo.save(originatingWorkflowStep);
        organizationRepo.broadcast(organizationRepo.findAllByOrderByIdAsc());
        return noteRepo.findOne(note.getId());
    }

    @Override
    protected String getChannel() {
        return "/channel/note";
    }

}
