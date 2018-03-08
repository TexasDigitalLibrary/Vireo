package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
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
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, null, null, repeatable, overrideable, enabled, optional, false, flagged, logged, new ArrayList<ControlledVocabulary>(), new ArrayList<FieldGloss>(), null, defaultValue);
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, null, repeatable, overrideable, enabled, optional, false, flagged, logged, new ArrayList<ControlledVocabulary>(), new ArrayList<FieldGloss>(), null, defaultValue);
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, false, flagged, logged, new ArrayList<ControlledVocabulary>(), new ArrayList<FieldGloss>(), null, defaultValue);
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, false, flagged, logged, controlledVocabularies, fieldGlosses, null, defaultValue);
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, false, flagged, logged, controlledVocabularies, fieldGlosses, mappedShibAttribute, defaultValue);
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean hidden, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        return newFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, hidden, flagged, logged, controlledVocabularies, fieldGlosses, mappedShibAttribute, defaultValue);
    }

    private FieldProfile newFieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean hidden, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, hidden, flagged, logged, controlledVocabularies, fieldGlosses, mappedShibAttribute, defaultValue));
        originatingWorkflowStep.addOriginalFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        fieldGlosses.forEach(fieldGloss -> {
            Optional<SubmissionListColumn> slc = submissionListColumnRepo.findByTitleAndPredicateAndInputType(fieldGloss.getValue(), fieldPredicate.getValue(), inputType);
            if (!slc.isPresent()) {
                submissionListColumnRepo.create(fieldGloss.getValue(), Sort.NONE, fieldPredicate.getValue(), inputType);
            }
        });
        organizationRepo.broadcast(organizationRepo.findAllByOrderByIdAsc());
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }

    @Override
    protected String getChannel() {
        return "/channel/field-profile";
    }

}