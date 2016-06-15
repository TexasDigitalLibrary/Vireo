package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.SubmissionNote;
import org.tdl.vireo.model.repo.SubmissionNoteRepo;
import org.tdl.vireo.model.repo.custom.SubmissionNoteRepoCustom;

public class SubmissionNoteRepoImpl implements SubmissionNoteRepoCustom {

    @Autowired
    private SubmissionNoteRepo submissionNoteRepo;

    @Override
    public SubmissionNote create(String name, String text) {
        return submissionNoteRepo.save(new SubmissionNote(name, text));
    }

}
