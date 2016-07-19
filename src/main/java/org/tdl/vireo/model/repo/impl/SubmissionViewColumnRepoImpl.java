package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.SubmissionViewColumn;
import org.tdl.vireo.model.repo.SubmissionViewColumnRepo;
import org.tdl.vireo.model.repo.custom.SubmissionViewColumnRepoCustom;

public class SubmissionViewColumnRepoImpl implements SubmissionViewColumnRepoCustom {

    @Autowired
    private SubmissionViewColumnRepo submissionViewColumnRepo;
    

    @Override
    public SubmissionViewColumn create(String label, Sort sort, String...path) {
        
        
        return null;
    }

}
