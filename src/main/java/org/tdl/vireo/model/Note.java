package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.inheritence.Heritable;
import org.tdl.vireo.model.validation.NoteValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@DiscriminatorValue("Org")
public class Note extends AbstractNote<Note> implements Heritable<Note> {

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
        setModelValidator(new NoteValidator());
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

    /**
     * @return the originatingNote
     */
    public Note getOriginatingNote() {
        return originatingNote;
    }

    /**
     * @param originatingNote
     *            the originatingNote to set
     */
    public void setOriginatingNote(Note originatingNote) {
        this.originatingNote = originatingNote;
    }

    /**
     * @return the originatingWorkflowStep
     */
    public WorkflowStep getOriginatingWorkflowStep() {
        return originatingWorkflowStep;
    }

    /**
     * @param originatingWorkflowStep
     *            the originatingWorkflowStep to set
     */
    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        this.originatingWorkflowStep = originatingWorkflowStep;
    }

    /**
     * @return the overrideable
     */
    public Boolean getOverrideable() {
        return overrideable;
    }

    /**
     * @param overrideable
     *            the overrideable to set
     */
    public void setOverrideable(Boolean overrideable) {
        this.overrideable = overrideable;
    }

    @Override
    public void setOriginating(Note originatingHeritableModel) {
        setOriginatingNote(originatingHeritableModel);
    }

    @Override
    public Note getOriginating() {
        return getOriginatingNote();
    }

    @Override
    public Note clone() {
        Note clone = new Note();

        clone.setName(getName());
        clone.setText(getText());
        clone.setOverrideable(getOverrideable());
        clone.setOriginatingNote(getOriginatingNote());
        clone.setOriginatingWorkflowStep(getOriginatingWorkflowStep());

        return clone;
    }

}