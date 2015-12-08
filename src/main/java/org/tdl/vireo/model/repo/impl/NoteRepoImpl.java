package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public class NoteRepoImpl implements NoteRepoCustom {

    @Autowired
    private NoteRepo noteRepo;

    @Override
    public Note create(String name, String text) {
        return noteRepo.save(new Note(name, text));
    }

}
