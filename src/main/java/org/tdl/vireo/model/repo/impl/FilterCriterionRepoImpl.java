package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.FilterCriterionRepo;
import org.tdl.vireo.model.repo.custom.FilterCriterionRepoCustom;

public class FilterCriterionRepoImpl implements FilterCriterionRepoCustom {

	@Autowired
	FilterCriterionRepo filterCriterionRepo;
	
	@Override
	public FilterCriterion create(SubmissionListColumn submissionListColumn) {
		FilterCriterion fc = new FilterCriterion();
		fc.setName(submissionListColumn.getTitle());
		fc.setSubmissionListColumn(submissionListColumn);
        return filterCriterionRepo.save(fc);
	}
	
	public FilterCriterion cloneFilterCriterion(FilterCriterion filterCriterion) {
		FilterCriterion newFilterCriterion = filterCriterionRepo.create(filterCriterion.getSubmissionListColumn());
		
		newFilterCriterion.setName(filterCriterion.getName());
		filterCriterion.getFilters().forEach(filter -> {
			newFilterCriterion.addFilter(filter);
		});
		
		return filterCriterionRepo.save(newFilterCriterion);
	}

}
