package org.tdl.vireo.model.repo.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public class NamedSearchFilterRepoImpl implements NamedSearchFilterRepoCustom {

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Override
    public NamedSearchFilter create(User user, String name, SubmissionListColumn submissionListColumn) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(user, name, submissionListColumn));
    }

    @Override
    public NamedSearchFilter create(User user, String name, SubmissionListColumn submissionListColumn, String value) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(user, name, submissionListColumn, value));
    }

    @Override
    public NamedSearchFilter create(User user, String name, SubmissionListColumn submissionListColumn, Calendar rangeStart, Calendar rangeEnd) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(user, name, submissionListColumn, rangeStart, rangeEnd));
    }

}
