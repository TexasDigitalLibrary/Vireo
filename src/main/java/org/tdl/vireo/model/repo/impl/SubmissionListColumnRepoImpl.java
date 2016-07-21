package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.custom.SubmissionListColumnRepoCustom;

public class SubmissionListColumnRepoImpl implements SubmissionListColumnRepoCustom {

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;
  
    @Override
    public SubmissionListColumn create(String label, Sort sort, List<String> path) {
        return submissionListColumnRepo.save(new SubmissionListColumn(label, sort, path));
    }

}
