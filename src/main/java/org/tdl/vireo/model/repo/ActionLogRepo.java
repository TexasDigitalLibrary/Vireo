package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface ActionLogRepo extends WeaverRepo<ActionLog>, ActionLogRepoCustom {

    public ActionLog findByUserAndSubmissionStatus(User user, SubmissionStatus submissionStatus);

}
