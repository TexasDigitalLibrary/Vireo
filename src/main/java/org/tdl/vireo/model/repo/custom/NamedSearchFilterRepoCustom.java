package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;

public interface NamedSearchFilterRepoCustom {

    public NamedSearchFilter create(SubmissionListColumn submissionListColumn);

    public NamedSearchFilter clone(NamedSearchFilter namedSearchFilter);

}
