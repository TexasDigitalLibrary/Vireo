package org.tdl.vireo.model;

import javax.persistence.Entity;

@Entity
public class SubmissionNote extends AbstractNote<SubmissionNote> {
    
    public SubmissionNote() {}
    
    public SubmissionNote(String name, String text) {
        setName(name);
        setText(text);
    }    
    
    public SubmissionNote(WorkflowStep originatingWorkflowStep, String name, String text) {
        this(name, text);
        setOriginatingWorkflowStep(originatingWorkflowStep);
    }
    
}
