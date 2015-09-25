package org.tdl.vireo.model.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long>, SubmissionRepoCustom {
	
    public Set<Submission> findByState(SubmissionState submissionState);
   
}
