package org.tdl.vireo.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Org")
public class Note extends AbstractNote<Note> {

    public Note() {
        setOverrideable(true);
    }
    
    public Note(String name, String text) {
        this();
        setName(name);
        setText(text);
    }
    
    public Note(WorkflowStep originatingWorkflowStep, String name, String text) {
        this(name, text);
        setOriginatingWorkflowStep(originatingWorkflowStep);
    }

}