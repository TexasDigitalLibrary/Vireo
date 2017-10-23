package org.tdl.vireo.model.repo;

import java.util.Optional;

import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.custom.SubmissionListColumnRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionListColumnRepo extends WeaverRepo<SubmissionListColumn>, SubmissionListColumnRepoCustom {

    public SubmissionListColumn findByTitle(String title);

    public Optional<SubmissionListColumn> findByTitleAndPredicateAndInputType(String title, String predicate, InputType inputType);

}
