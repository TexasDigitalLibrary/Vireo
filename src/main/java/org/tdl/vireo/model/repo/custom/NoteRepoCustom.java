package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Note;

public interface NoteRepoCustom {

    public Note create(String name, String text);

}
