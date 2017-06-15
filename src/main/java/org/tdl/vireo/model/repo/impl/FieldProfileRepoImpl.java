package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.inheritence.HeritableRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public class FieldProfileRepoImpl extends HeritableRepo<FieldProfile, FieldProfileRepo> implements FieldProfileRepoCustom {

    private static final List<String> PREDICATE_PATH = new ArrayList<String>(Arrays.asList(new String[] { "fieldValues", "fieldPredicate", "value" }));

    private static final List<String> VALUE_PATH = new ArrayList<String>(Arrays.asList(new String[] { "fieldValues", "value" }));

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, null, null, repeatable, overrideable, enabled, optional, flagged, logged, new ArrayList<ControlledVocabulary>(), new ArrayList<FieldGloss>());
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, null, repeatable, overrideable, enabled, optional, flagged, logged, new ArrayList<ControlledVocabulary>(), new ArrayList<FieldGloss>());
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, new ArrayList<ControlledVocabulary>(), new ArrayList<FieldGloss>());
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, controlledVocabularies, fieldGlosses);
    }

    private FieldProfile newFieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, controlledVocabularies, fieldGlosses));
        originatingWorkflowStep.addOriginalFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);

        fieldGlosses.forEach(fieldGloss -> {
            submissionListColumnRepo.create(fieldGloss.getValue(), Sort.NONE, fieldPredicate.getValue(), PREDICATE_PATH, VALUE_PATH, inputType);
        });

        return fieldProfileRepo.findOne(fieldProfile.getId());
    }

}
