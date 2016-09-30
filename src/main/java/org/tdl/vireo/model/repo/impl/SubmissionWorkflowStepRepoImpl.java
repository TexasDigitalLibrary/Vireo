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
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

    @Autowired
    private SubmissionFieldProfileRepo submissionFieldProfileRepo;

    @Autowired
    private SubmissionNoteRepo submissionNoteStepRepo;

    // a submission workflow must be created each time a new submission is created
    
    // if retrieved from the database from a previous submission creation,
    // the organizations workflow steps could have been modified
    // therefore, each new submission would have an out of date workflow

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
            submissionWorkflowStep.addFieldProfile(submissionFieldProfileRepo.create(fieldProfile));
        }

        for (Note note : workflowStep.getAggregateNotes()) {
            submissionWorkflowStep.addAggregateNote(submissionNoteStepRepo.create(note));
        }

        return submissionWorkflowStepRepo.save(submissionWorkflowStep);
    }

}
