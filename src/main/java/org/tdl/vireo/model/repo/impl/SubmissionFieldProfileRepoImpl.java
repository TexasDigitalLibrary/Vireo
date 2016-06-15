package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

public class SubmissionFieldProfileRepoImpl implements SubmissionFieldProfileRepoCustom {
	
	@PersistenceContext
    private EntityManager em;

    @Autowired
    private SubmissionFieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private SubmissionWorkflowStepRepo workflowStepRepo;
    
    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public SubmissionFieldProfile create(SubmissionWorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean optional) {
        SubmissionFieldProfile fieldProfile = fieldProfileRepo.save(new SubmissionFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, repeatable, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public SubmissionFieldProfile create(SubmissionWorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean optional) {
        SubmissionFieldProfile fieldProfile = fieldProfileRepo.save(new SubmissionFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, repeatable, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }
    
    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public SubmissionFieldProfile create(SubmissionWorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean optional) {
        SubmissionFieldProfile fieldProfile = fieldProfileRepo.save(new SubmissionFieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }
}
