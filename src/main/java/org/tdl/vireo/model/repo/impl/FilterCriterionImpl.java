package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.FilterCriterionRepo;
import org.tdl.vireo.model.repo.custom.FilterCriterionRepoCustom;

public class FilterCriterionImpl implements FilterCriterionRepoCustom {
	@Autowired
	FilterCriterionRepo filterCriterionRepo;
	
	@Override
	public FilterCriterion create(SubmissionListColumn submissionListColumn) {
		FilterCriterion fc = new FilterCriterion();
		fc.addSubmissionListColumn(submissionListColumn);
        return filterCriterionRepo.save(fc);
	}

}
