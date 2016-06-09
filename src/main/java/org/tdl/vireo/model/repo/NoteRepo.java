package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public interface NoteRepo extends JpaRepository<Note, Long>, NoteRepoCustom {

    public Note findByNameAndOriginatingWorkflowStep(String name, WorkflowStep originatingWorkflowStep);
    
    public List<Note> findByOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);
    
    public List<Note> findByOriginatingNote(Note originatingNote);
    
    public void delete(Note note);

}