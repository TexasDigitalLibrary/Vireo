package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.inheritance.HeritableComponent;
import org.tdl.vireo.model.validation.FieldProfileValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@DiscriminatorValue("Org")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "originating_workflow_step", "originating_field_profile", "field_predicate" }))
public class FieldProfile extends AbstractFieldProfile<FieldProfile> implements HeritableComponent<FieldProfile> {

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private FieldProfile originating;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;

    @Column(nullable = true)
    private Boolean overrideable;

    @Column(nullable = true)
    private Boolean enabled;

    public FieldProfile() {
        setModelValidator(new FieldProfileValidator());
        setRepeatable(false);
        setEnabled(true);
        setOptional(true);
        setFlagged(false);
        setLogged(false);
        setFieldGlosses(new ArrayList<FieldGloss>());
        setControlledVocabularies(new ArrayList<ControlledVocabulary>());
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep) {
        this();
        setOriginatingWorkflowStep(originatingWorkflowStep);
    }

    /**
     *
     * @param fieldPredicate
     * @param inputType
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged) {
        this(originatingWorkflowStep);
        setFieldPredicate(fieldPredicate);
        setInputType(inputType);
        setRepeatable(repeatable);
        setOverrideable(overrideable);
        setEnabled(enabled);
        setOptional(optional);
        setFlagged(flagged);
        setLogged(logged);
    }

    /**
     *
     * @param fieldPredicate
     * @param inputType
     * @param usage
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged) {
        this(originatingWorkflowStep, fieldPredicate, inputType, repeatable, overrideable, enabled, optional, flagged, logged);
        setUsage(usage);
    }

    /**
     *
     * @param fieldPredicate
     * @param inputType
     * @param usage
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, repeatable, overrideable, enabled, optional, flagged, logged);
        setHelp(help);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, ControlledVocabulary controlledVocabulary, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged);
        addControlledVocabulary(0, controlledVocabulary);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged);
        setControlledVocabularies(controlledVocabularies);
        setFieldGlosses(fieldGlosses);
    }
    
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, Configuration mappedShibAttribute) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, controlledVocabularies, fieldGlosses);
        setMappedShibAttribute(mappedShibAttribute);
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

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setOriginating(FieldProfile originating) {
    	this.originating = originating;
    }

    @Override
    public FieldProfile getOriginating() {
    	return originating;
    }

    @Override
    public FieldProfile clone() {
        FieldProfile clone = new FieldProfile();

        List<ControlledVocabulary> controlledVocabularies = new ArrayList<ControlledVocabulary>();
        for (ControlledVocabulary cv : getControlledVocabularies()) {
            controlledVocabularies.add(cv);
        }

        List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();
        for (FieldGloss fg : getFieldGlosses()) {
            fieldGlosses.add(fg);
        }

        clone.setMappedShibAttribute(getMappedShibAttribute());
        clone.setHelp(getHelp());
        clone.setUsage(getUsage());
        clone.setEnabled(getEnabled());
        clone.setOptional(getOptional());
        clone.setRepeatable(getRepeatable());

        clone.setFlagged(getFlagged());
        clone.setOverrideable(getOverrideable());

        clone.setInputType(getInputType());
        clone.setFieldPredicate(getFieldPredicate());

        clone.setOriginating(getOriginating());
        clone.setOriginatingWorkflowStep(getOriginatingWorkflowStep());

        clone.setControlledVocabularies(controlledVocabularies);

        clone.setFieldGlosses(fieldGlosses);

        return clone;
    }

}
