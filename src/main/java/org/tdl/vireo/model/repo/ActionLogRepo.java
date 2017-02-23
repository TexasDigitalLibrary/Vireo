package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

public interface ActionLogRepo extends JpaRepository<ActionLog, Long>, ActionLogRepoCustom {

    public ActionLog findByUserAndSubmissionState(User user, SubmissionState submissionState);

    public void delete(ActionLog actionLog);

}
