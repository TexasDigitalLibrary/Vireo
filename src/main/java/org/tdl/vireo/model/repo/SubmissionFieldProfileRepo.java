package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionFieldProfileRepo extends WeaverRepo<SubmissionFieldProfile>, SubmissionFieldProfileRepoCustom {

    public SubmissionFieldProfile findByFieldPredicateAndInputTypeAndRepeatableAndOptionalAndHiddenAndLoggedAndUsageAndHelpAndMappedShibAttributeAndFlaggedAndDefaultValueAndEnabled(FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean optional, Boolean hidden, Boolean logged, String usage, String help, ManagedConfiguration mappedShibAttribute, Boolean flagged, String defaultValue, Boolean enabled);

}
