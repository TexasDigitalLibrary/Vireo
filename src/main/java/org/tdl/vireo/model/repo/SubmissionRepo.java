package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long>, SubmissionRepoCustom {

}
