package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.inheritence.HeritableJpaRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public interface NoteRepo extends HeritableJpaRepo<Note>, NoteRepoCustom {

    public Note findByNameAndOriginatingWorkflowStep(String name, WorkflowStep originatingWorkflowStep);

    public List<Note> findByOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);

    public List<Note> findByOriginatingNote(Note originatingNote);

    public void delete(Note note);

}