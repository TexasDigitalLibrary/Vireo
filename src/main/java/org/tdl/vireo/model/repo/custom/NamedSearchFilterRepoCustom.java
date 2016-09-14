package org.tdl.vireo.model.repo.custom;

import java.util.Calendar;

import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;

public interface NamedSearchFilterRepoCustom {

    public NamedSearchFilter create(User user, String name, SubmissionListColumn submissionListColumn);
    
    public NamedSearchFilter create(User user, String name, SubmissionListColumn submissionListColumn, String value);
    
    public NamedSearchFilter create(User user, String name, SubmissionListColumn submissionListColumn, Calendar dateValue);
    
    public NamedSearchFilter create(User user, String name, SubmissionListColumn submissionListColumn, Calendar rangeStart, Calendar rangeEnd);

}
