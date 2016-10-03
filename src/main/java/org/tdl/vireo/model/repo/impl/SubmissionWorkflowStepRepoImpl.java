package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionNote;
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
    
    // TODO: either create submission workflow for each submission or track when a workflow changes to force create new submission workflow
    
    // if user creates a submission and the organization changes its workflow,
    // the next person to create a submission will not have the updated workflow!
    
    @Override
    public SubmissionWorkflowStep findOrCreate(Organization originatingOrganization, WorkflowStep originatingWorkflowStep) {
       
        SubmissionWorkflowStep submissionWorkflowStep = submissionWorkflowStepRepo.findByNameAndOriginatingOrganizationId(originatingWorkflowStep.getName(), originatingOrganization.getId());
        
        if(submissionWorkflowStep == null) {
            submissionWorkflowStep = new SubmissionWorkflowStep(originatingWorkflowStep.getName(), originatingOrganization);
            
            for(FieldProfile fieldProfile : originatingWorkflowStep.getAggregateFieldProfiles()) {
                SubmissionFieldProfile submissionFieldProfile = submissionFieldProfileRepo.create(fieldProfile);
                submissionWorkflowStep.addFieldProfile(submissionFieldProfile);
            }
            
            for(Note note : originatingWorkflowStep.getAggregateNotes()) {
                SubmissionNote submissionNote = submissionNoteStepRepo.create(note);
                submissionWorkflowStep.addAggregateNote(submissionNote);
            }
            
            submissionWorkflowStep = submissionWorkflowStepRepo.save(submissionWorkflowStep);
            
        }
        
        return submissionWorkflowStep;
        
    }
    
    
}
