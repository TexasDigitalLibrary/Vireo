package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.SubmissionViewColumn;
import org.tdl.vireo.model.repo.SubmissionViewColumnRepo;
import org.tdl.vireo.model.repo.custom.SubmissionViewColumnRepoCustom;

public class SubmissionViewColumnRepoImpl implements SubmissionViewColumnRepoCustom {

    @Autowired
    private SubmissionViewColumnRepo submissionViewColumnRepo;
  
    @Override
    public SubmissionViewColumn create(String label, Sort sort, List<String> path) {
        return submissionViewColumnRepo.save(new SubmissionViewColumn(label, sort, path));
    }

}
