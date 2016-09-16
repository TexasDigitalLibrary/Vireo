package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.custom.FilterCriterionRepoCustom;

public interface FilterCriterionRepo extends JpaRepository<FilterCriterion, Long>, FilterCriterionRepoCustom {
	public FilterCriterion findBySubmissionListColumn(SubmissionListColumn submissionListColumn);
}
