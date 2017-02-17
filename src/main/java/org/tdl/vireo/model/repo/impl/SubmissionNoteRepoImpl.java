package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.SubmissionNote;
import org.tdl.vireo.model.repo.SubmissionNoteRepo;
import org.tdl.vireo.model.repo.custom.SubmissionNoteRepoCustom;

public class SubmissionNoteRepoImpl implements SubmissionNoteRepoCustom {

    @Autowired
    private SubmissionNoteRepo submissionNoteRepo;

    @Override
    public SubmissionNote create(Note note) {

        SubmissionNote submissionNote = submissionNoteRepo.findByNameAndText(note.getName(), note.getText());

        if(submissionNote == null) {
            submissionNote = new SubmissionNote(note.getName(), note.getText());
            submissionNote = submissionNoteRepo.save(submissionNote);
        }

        return submissionNote;
    }

}
