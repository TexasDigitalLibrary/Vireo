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

    public FieldProfile() {
        setModelValidator(new FieldProfileValidator());
        setRepeatable(false);
        setEnabled(true);
        setOptional(true);
        setHidden(false);
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
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        this(originatingWorkflowStep);
        setFieldPredicate(fieldPredicate);
        setInputType(inputType);
        setRepeatable(repeatable);
        setOverrideable(overrideable);
        setEnabled(enabled);
        setOptional(optional);
        setFlagged(flagged);
        setLogged(logged);
        setDefaultValue(defaultValue);
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
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, repeatable, overrideable, enabled, optional, flagged, logged, defaultValue);
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
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, repeatable, overrideable, enabled, optional, flagged, logged, defaultValue);
        setHelp(help);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, ControlledVocabulary controlledVocabulary, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, defaultValue);
        addControlledVocabulary(0, controlledVocabulary);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, defaultValue);
        setControlledVocabularies(controlledVocabularies);
        setFieldGlosses(fieldGlosses);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, controlledVocabularies, fieldGlosses, defaultValue);
        setMappedShibAttribute(mappedShibAttribute);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean hidden, Boolean flagged, Boolean logged, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional, flagged, logged, controlledVocabularies, fieldGlosses, mappedShibAttribute, defaultValue);
        setHidden(hidden);
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

        clone.setFieldPredicate(getFieldPredicate());

        clone.setInputType(getInputType());
        clone.setRepeatable(getRepeatable());
        clone.setOptional(getOptional());
        clone.setHidden(getHidden());
        clone.setLogged(getLogged());
        clone.setUsage(getUsage());
        clone.setHelp(getHelp());

        List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();
        for (FieldGloss fg : getFieldGlosses()) {
            fieldGlosses.add(fg);
        }
        clone.setFieldGlosses(fieldGlosses);

        List<ControlledVocabulary> controlledVocabularies = new ArrayList<ControlledVocabulary>();
        for (ControlledVocabulary cv : getControlledVocabularies()) {
            controlledVocabularies.add(cv);
        }
        clone.setControlledVocabularies(controlledVocabularies);

        clone.setMappedShibAttribute(getMappedShibAttribute());

        clone.setFlagged(getFlagged());
        clone.setDefaultValue(getDefaultValue());
        clone.setEnabled(getEnabled());

        clone.setOriginating(getOriginating());
        clone.setOriginatingWorkflowStep(getOriginatingWorkflowStep());

        clone.setOverrideable(getOverrideable());

        return clone;
    }

}