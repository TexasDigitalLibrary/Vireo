package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public interface NoteRepo extends JpaRepository<Note, Long>, NoteRepoCustom {

    public Note findByName(String name);

}