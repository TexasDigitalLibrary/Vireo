package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.custom.SubmissionStateRepoCustom;

@Repository
public interface SubmissionStateRepo extends JpaRepository<SubmissionState, Long>, SubmissionStateRepoCustom {

	public SubmissionState create(String name, Boolean archived, Boolean publishable, Boolean deletable, Boolean editableByReviewer, Boolean editableByStudent, Boolean active);
	
	public void delete(SubmissionState submissionState);
	
}
