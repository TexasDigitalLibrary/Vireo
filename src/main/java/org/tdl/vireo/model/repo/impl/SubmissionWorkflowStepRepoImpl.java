package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionNoteRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.SubmissionWorkflowStepRepoCustom;

public class SubmissionWorkflowStepRepoImpl implements SubmissionWorkflowStepRepoCustom {

    @Autowired
    SubmissionWorkflowStepRepo submissionWorkflowStepRepo;
    
    @Autowired
    SubmissionFieldProfileRepo submissionFieldProfileRepo;
    
    @Autowired
    SubmissionNoteRepo submissionNoteStepRepo;
    
    @Override
    public List<SubmissionWorkflowStep> cloneWorkflow(Organization organization) {

        List<SubmissionWorkflowStep> submissionWorkflow = new ArrayList<SubmissionWorkflowStep>();

        for (WorkflowStep workflowStep : organization.getAggregateWorkflowSteps()) {
            submissionWorkflow.add(cloneWorkflowStep(workflowStep));
        }

        return submissionWorkflow;
    }

    @Override
    public SubmissionWorkflowStep cloneWorkflowStep(WorkflowStep workflowStep) {

        SubmissionWorkflowStep submissionWorkflowStep = new SubmissionWorkflowStep(workflowStep.getName());

        for (FieldProfile fieldProfile : workflowStep.getAggregateFieldProfiles()) {
        	if(fieldProfile.getEnabled()) {
        		submissionWorkflowStep.addFieldProfile(submissionFieldProfileRepo.create(fieldProfile));
        	}
        }

        for (Note note : workflowStep.getAggregateNotes()) {
            submissionWorkflowStep.addAggregateNote(submissionNoteStepRepo.create(note));
        }

        return submissionWorkflowStepRepo.save(submissionWorkflowStep);
    }
    
    
}
