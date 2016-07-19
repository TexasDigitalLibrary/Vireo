package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.SubmissionViewColumn;
import org.tdl.vireo.model.repo.custom.SubmissionViewColumnRepoCustom;

public interface SubmissionViewColumnRepo extends JpaRepository<SubmissionViewColumn, Long>, SubmissionViewColumnRepoCustom {

    public SubmissionViewColumn findByTitle(String title);
    
}
