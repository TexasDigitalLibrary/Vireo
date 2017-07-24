package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.custom.SubmissionListColumnRepoCustom;

public interface SubmissionListColumnRepo extends JpaRepository<SubmissionListColumn, Long>, SubmissionListColumnRepoCustom {

    public SubmissionListColumn findByTitle(String title);

}
