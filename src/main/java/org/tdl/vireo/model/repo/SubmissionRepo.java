package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public interface SubmissionRepo extends JpaRepository<Submission, Long>, SubmissionRepoCustom {

    public Submission findBySubmitterAndState(User submitter, SubmissionState submissionState);

}
