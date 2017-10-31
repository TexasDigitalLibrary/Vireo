package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.custom.SubmissionStatusRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionStatusRepo extends WeaverRepo<SubmissionStatus>, SubmissionStatusRepoCustom {

    public SubmissionStatus findByName(String name);

}
