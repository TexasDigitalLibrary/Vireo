package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.SubmissionNote;
import org.tdl.vireo.model.repo.custom.SubmissionNoteRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionNoteRepo extends WeaverRepo<SubmissionNote>, SubmissionNoteRepoCustom {

    public SubmissionNote findByNameAndText(String name, String text);

}
