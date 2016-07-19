package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.SubmissionViewColumn;

public interface SubmissionViewColumnRepoCustom {
    
    public SubmissionViewColumn create(String label, Sort sort, List<String> path);

}
