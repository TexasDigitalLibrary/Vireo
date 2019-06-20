package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.custom.SubmissionStatusRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionStatusRepo extends WeaverRepo<SubmissionStatus>, SubmissionStatusRepoCustom {

    public SubmissionStatus findByName(String name);

    public SubmissionStatus findBySubmissionStateAndIsDefaultTrue(SubmissionState submissionState);

    @Transactional
    @Modifying
    @Query("update SubmissionStatus ss set ss.isDefault = false where ss.isDefault = true and ss.submissionState = ?1")
    public int updateDefaultsToFalse(SubmissionState submissionState);

    @Transactional
    @Modifying
    @Query("delete from SubmissionStatus ss where ss.isDefault = false and ss.id = ?1")
    public void deletePreserveDefault(long id);

}
