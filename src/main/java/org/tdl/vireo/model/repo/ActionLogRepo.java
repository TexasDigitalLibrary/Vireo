package org.tdl.vireo.model.repo;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.Calendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

public interface ActionLogRepo extends WeaverRepo<ActionLog>, ActionLogRepoCustom {

    public ActionLog findByUserAndSubmissionStatus(User user, SubmissionStatus submissionStatus);

    /**
     * Returns the action log for a given file upload based on file name and nearest action date from file creation date.
     *
     * Column action_logs_id is join column representing submission id.
     *
     * @return Page as JPQL does not support LIMIT.
     */
    @Query("SELECT al FROM ActionLog al WHERE action_logs_id = :submission_id and al.entry LIKE %:file_identifier% AND al.actionDate >= :creation_date ORDER BY al.actionDate ASC")
    public Page<ActionLog> findBySubmissionIdAndEntryLikeAndBeforeActionDate(@Param("submission_id") Long submissionId, @Param("file_identifier") String fileIdentifier, @Param("creation_date") Calendar creationDate, Pageable pageable);

    /**
     * Returns the action logs that are not private and are associated with a given submission.
     *
     * A native query is used to work-around JPQL+Pageable problems that result in errors like:
     * - "QueryException: could not resolve property: action_date of: org.tdl.vireo.model.ActionLog"
     *
     * @param submissionId ID representing the submission.
     * @param privateFlag The value of the private flag to filter by.
     *
     * @return Page of all action logs fulfilling the given parameters.
     */
    @Query(value = "SELECT al.* FROM action_log AS al WHERE al.action_logs_id = :submission_id AND al.private_flag = :private_flag", nativeQuery = true)
    public Page<ActionLog> getAllActionLogs(@Param("submission_id") Long submissionId, @Param("private_flag") Boolean privateFlag, Pageable pageable);

    /**
     * Returns the action logs that are not private and are associated with a given advisor access hash.
     *
     * A native query is used to work-around JPQL+Pageable problems that result in errors like:
     * - "QueryException: could not resolve property: action_date of: org.tdl.vireo.model.ActionLog"
     *
     * @param advisorAccessHash ID representing the submission.
     * @param privateFlag The value of the private flag to filter by.
     *
     * @return Page of all action logs fulfilling the given parameters.
     */
    @Query(value = "SELECT al.* FROM action_log AS al INNER JOIN submission AS s ON al.action_logs_id = s.id WHERE s.advisor_access_hash = :advisor_access_hash AND al.private_flag = :private_flag", nativeQuery = true)
    public Page<ActionLog> getAllActionLogs(@Param("advisor_access_hash") String advisorAccessHash, @Param("private_flag") Boolean privateFlag, Pageable pageable);

}
