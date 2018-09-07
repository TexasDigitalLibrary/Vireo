package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.inheritance.HeritableRepoImpl;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public class FieldProfileRepoImpl extends HeritableRepoImpl<FieldProfile, FieldProfileRepo> implements FieldProfileRepoCustom {

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Override
    @Transactional
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, null, null, gloss, repeatable, overrideable, enabled, optional, false, flagged, logged, new ArrayList<ControlledVocabulary>(), null, defaultValue);
    }

    @Override
    @Transactional
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, null, gloss, repeatable, overrideable, enabled, optional, false, flagged, logged, new ArrayList<ControlledVocabulary>(), null, defaultValue);
    }

    @Override
    @Transactional
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, false, flagged, logged, new ArrayList<ControlledVocabulary>(), null, defaultValue);
    }

    @Override
    @Transactional
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, false, flagged, logged, controlledVocabularies, null, defaultValue);
    }

    @Override
    @Transactional
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, false, flagged, logged, controlledVocabularies, mappedShibAttribute, defaultValue);
    }

    @Override
    @Transactional
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean hidden, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, hidden, flagged, logged, controlledVocabularies, mappedShibAttribute, defaultValue);
    }

    private synchronized FieldProfile newFieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean hidden, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, hidden, flagged, logged, controlledVocabularies, mappedShibAttribute, defaultValue));
        originatingWorkflowStep.addOriginalFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        Optional<SubmissionListColumn> slc = submissionListColumnRepo.findByTitleAndPredicateAndInputType(gloss, fieldPredicate.getValue(), inputType);
        if (!slc.isPresent()) {
            submissionListColumnRepo.create(gloss, Sort.NONE, fieldPredicate.getValue(), inputType);
        }
        organizationRepo.broadcast(organizationRepo.findAllByOrderByIdAsc());
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }

    @Override
    protected String getChannel() {
        return "/channel/field-profile";
    }

}