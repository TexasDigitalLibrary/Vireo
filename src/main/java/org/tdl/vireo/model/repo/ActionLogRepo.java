package org.tdl.vireo.model.repo;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

public interface ActionLogRepo extends WeaverRepo<ActionLog>, ActionLogRepoCustom {

    public ActionLog findByUserAndSubmissionStatus(User user, SubmissionStatus submissionStatus);

    @Query(value = "SELECT * FROM action_log a WHERE a.action_logs_id = :submissionId ORDER BY a.action_date DESC", nativeQuery=true)
    public List<ActionLog> findAll(@Param("submissionId") Long submissionId);

    @Query(value = "SELECT * FROM action_log a WHERE a.action_logs_id = :submissionId ORDER BY a.action_date DESC LIMIT 1", nativeQuery=true)
    public ActionLog findLastEvent(@Param("submissionId") Long submissionId);

}
