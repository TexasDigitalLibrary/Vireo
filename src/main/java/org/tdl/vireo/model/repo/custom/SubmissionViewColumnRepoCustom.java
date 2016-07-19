package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.SubmissionViewColumn;

public interface SubmissionViewColumnRepoCustom {
    
    public SubmissionViewColumn create(String label, Sort sort, String...path);

}
