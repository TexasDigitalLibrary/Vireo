package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.custom.SubmissionListColumnRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class SubmissionListColumnRepoImpl extends AbstractWeaverRepoImpl<SubmissionListColumn, SubmissionListColumnRepo> implements SubmissionListColumnRepoCustom {

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Override
    public SubmissionListColumn create(String label, Sort sort, List<String> path, InputType inputType) {
        return submissionListColumnRepo.save(new SubmissionListColumn(label, sort, path, inputType));
    }

    @Override
    public SubmissionListColumn create(String label, Sort sort, String predicate, InputType inputType) {
        return submissionListColumnRepo.save(new SubmissionListColumn(label, sort, predicate, inputType));
    }

    @Override
    protected String getChannel() {
        return "/channel/submission-list-column";
    }

}
