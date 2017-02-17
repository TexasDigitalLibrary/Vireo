package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.SubmissionListColumn;

public interface SubmissionListColumnRepoCustom {

    public SubmissionListColumn create(String label, Sort sort, List<String> path);

    public SubmissionListColumn create(String label, Sort sort, List<String> path, InputType inputType);

    public SubmissionListColumn create(String label, Sort sort, String predicate, List<String> predicatePath, List<String> valuePath);

    public SubmissionListColumn create(String label, Sort sort, String predicate, List<String> predicatePath, List<String> valuePath, InputType inputType);

}
