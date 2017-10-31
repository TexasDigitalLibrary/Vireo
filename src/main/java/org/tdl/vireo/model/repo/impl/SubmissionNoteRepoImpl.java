package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.SubmissionNote;
import org.tdl.vireo.model.repo.SubmissionNoteRepo;
import org.tdl.vireo.model.repo.custom.SubmissionNoteRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class SubmissionNoteRepoImpl extends AbstractWeaverRepoImpl<SubmissionNote, SubmissionNoteRepo> implements SubmissionNoteRepoCustom {

    @Autowired
    private SubmissionNoteRepo submissionNoteRepo;

    @Override
    public SubmissionNote create(Note note) {
        SubmissionNote submissionNote = submissionNoteRepo.findByNameAndText(note.getName(), note.getText());
        if (submissionNote == null) {
            submissionNote = super.create(new SubmissionNote(note.getName(), note.getText()));
        }
        return submissionNote;
    }

    @Override
    protected String getChannel() {
        return "/channel/submission-note";
    }

}
