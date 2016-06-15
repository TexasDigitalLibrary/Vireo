package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.SubmissionNote;
import org.tdl.vireo.model.repo.custom.SubmissionNoteRepoCustom;

public interface SubmissionNoteRepo extends JpaRepository<SubmissionNote, Long>, SubmissionNoteRepoCustom {

    public SubmissionNote findByNameAndText(String name, String text);

}