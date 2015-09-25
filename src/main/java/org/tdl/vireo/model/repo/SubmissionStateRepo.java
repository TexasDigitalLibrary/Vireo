package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.custom.SubmissionStateRepoCustom;

@Repository
public interface SubmissionStateRepo extends JpaRepository<SubmissionState, Long>, SubmissionStateRepoCustom {
    public SubmissionState findByName(String name);
}
