package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@DiscriminatorValue("Org")
public class Note extends AbstractNote<Note> {
    
    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Note.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Note originatingNote;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    @Column(nullable = true)
    private Boolean overrideable;

    
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
    
    public Note(WorkflowStep originatingWorkflowStep, String name, String text, Boolean overrideable) {
        this(originatingWorkflowStep, name, text);
        setOverrideable(overrideable);
    }
    
    public Note getOriginatingNote() {
        return originatingNote;
    }

    public void setOriginatingNote(Note originatingNote) {
        this.originatingNote = originatingNote;
    }
    
    public WorkflowStep getOriginatingWorkflowStep() {
        return originatingWorkflowStep;
    }

    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        this.originatingWorkflowStep = originatingWorkflowStep;
    }
    
    public Boolean getOverrideable() {
        return overrideable;
    }

    public void setOverrideable(Boolean overrideable) {
        this.overrideable = overrideable;
    }


}