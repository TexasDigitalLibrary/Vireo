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
import org.tdl.vireo.model.FieldProfile;
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
    private SubmissionFieldProfileRepo submissionFieldProfileRepo;
    
    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public SubmissionFieldProfile create(FieldProfile fieldProfile) {
        
        SubmissionFieldProfile submissionfieldProfile = submissionFieldProfileRepo.findByPredicateAndInputTypeAndRepeatableAndOptionalAndUsageAndHelp(
                    fieldProfile.getPredicate(), 
                    fieldProfile.getInputType(),
                    fieldProfile.getRepeatable(),
                    fieldProfile.getOptional(),
                    fieldProfile.getUsage(),
                    fieldProfile.getHelp()
                );
                        
        if(submissionfieldProfile == null) {
            submissionfieldProfile = new SubmissionFieldProfile();
            
            submissionfieldProfile.setPredicate(fieldProfile.getPredicate());
            
            submissionfieldProfile.setControlledVocabularies(new ArrayList<ControlledVocabulary>(fieldProfile.getControlledVocabularies()));
            submissionfieldProfile.setFieldGlosses(new ArrayList<FieldGloss>(fieldProfile.getFieldGlosses()));
            submissionfieldProfile.setHelp(fieldProfile.getHelp());
            submissionfieldProfile.setInputType(fieldProfile.getInputType());
            submissionfieldProfile.setOptional(fieldProfile.getOptional());
            submissionfieldProfile.setRepeatable(fieldProfile.getRepeatable());
            submissionfieldProfile.setUsage(submissionfieldProfile.getUsage());
            
            submissionfieldProfile = submissionFieldProfileRepo.save(submissionfieldProfile);
            
        }
        
        return submissionfieldProfile;
    }
}
