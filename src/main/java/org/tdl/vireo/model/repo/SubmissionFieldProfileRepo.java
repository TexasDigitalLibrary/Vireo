package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

public interface SubmissionFieldProfileRepo extends JpaRepository<SubmissionFieldProfile, Long>, SubmissionFieldProfileRepoCustom {

    public SubmissionFieldProfile findByFieldPredicateAndInputTypeAndRepeatableAndOptionalAndEnabledAndUsageAndHelp(FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean optional, Boolean enabled, String usage, String help);

}
