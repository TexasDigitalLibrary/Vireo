package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

public interface SubmissionFieldProfileRepo extends JpaRepository<SubmissionFieldProfile, Long>, SubmissionFieldProfileRepoCustom {

    public SubmissionFieldProfile findByFieldPredicateAndInputTypeAndRepeatableAndOptionalAndHiddenAndLoggedAndUsageAndHelpAndMappedShibAttributeAndFlaggedAndDefaultValueAndEnabled(FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean optional, Boolean hidden, Boolean logged, String usage, String help, Configuration mappedShibAttribute, Boolean flagged, String defaultValue, Boolean enabled);

}
