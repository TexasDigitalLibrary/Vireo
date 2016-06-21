package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_workflow_step_id" }) )
public abstract class AbstractNote<N> extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    @Lob
    @Column(nullable = false)
    private String text;
    
    @Column(nullable = false)
    private Boolean overrideable;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Note.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Note originatingNote;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public Boolean getOverrideable() {
        return overrideable;
    }

    public void setOverrideable(Boolean overrideable) {
        this.overrideable = overrideable;
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
    
}
