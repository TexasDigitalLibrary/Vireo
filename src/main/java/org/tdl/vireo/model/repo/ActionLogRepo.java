package org.tdl.vireo.model.repo;

import java.util.Calendar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface ActionLogRepo extends WeaverRepo<ActionLog>, ActionLogRepoCustom {

    public ActionLog findByUserAndSubmissionStatus(User user, SubmissionStatus submissionStatus);

    /**
     * Returns the action log for a given file upload based on file name and nearest action date from file creation date.
     *
     * Column action_logs_id is join column representing submission id.
     * Returning Page as JPQL does not support LIMIT.
     */
    @Query("SELECT al FROM ActionLog al WHERE action_logs_id = :submission_id and al.entry LIKE %:file_identifier% AND al.actionDate >= :creation_date ORDER BY al.actionDate ASC")
    public Page<ActionLog> findBySubmissionIdAndEntryLikeAndBeforeActionDate(@Param("submission_id") Long submissionId, @Param("file_identifier") String fileIdentifier, @Param("creation_date") Calendar creationDate, Pageable pageable);

}
