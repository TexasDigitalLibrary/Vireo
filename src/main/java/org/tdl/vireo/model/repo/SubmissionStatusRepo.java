package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.custom.SubmissionStatusRepoCustom;

public interface SubmissionStatusRepo extends JpaRepository<SubmissionStatus, Long>, SubmissionStatusRepoCustom {

    public SubmissionStatus findByName(String name);

}
