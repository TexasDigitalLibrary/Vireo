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

import org.tdl.vireo.model.inheritance.HeritableComponent;
import org.tdl.vireo.model.validation.FieldProfileValidator;

import edu.tamu.weaver.data.resolver.BaseEntityIdResolver;

@Entity
@DiscriminatorValue("Org")
public class FieldProfile extends AbstractFieldProfile<FieldProfile> implements HeritableComponent<FieldProfile> {

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, resolver = BaseEntityIdResolver.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private FieldProfile originating;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, resolver = BaseEntityIdResolver.class, property = "id")
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
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep) {
        this();
        setOriginatingWorkflowStep(originatingWorkflowStep);
    }

    /**
     * @param fieldPredicate
     * @param inputType
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        this(originatingWorkflowStep);
        setFieldPredicate(fieldPredicate);
        setInputType(inputType);
        setGloss(gloss);
        setRepeatable(repeatable);
        setOverrideable(overrideable);
        setEnabled(enabled);
        setOptional(optional);
        setFlagged(flagged);
        setLogged(logged);
        setDefaultValue(defaultValue);
    }

    /**
     * @param fieldPredicate
     * @param inputType
     * @param usage
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, gloss, repeatable, overrideable, enabled, optional, flagged, logged, defaultValue);
        setUsage(usage);
    }

    /**
     * @param fieldPredicate
     * @param inputType
     * @param usage
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, gloss, repeatable, overrideable, enabled, optional, flagged, logged, defaultValue);
        setHelp(help);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, ControlledVocabulary controlledVocabulary, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, flagged, logged, defaultValue);
        setControlledVocabulary(controlledVocabulary);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean flagged, Boolean logged, ControlledVocabulary controlledVocabulary, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, flagged, logged, controlledVocabulary, defaultValue);
        setMappedShibAttribute(mappedShibAttribute);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, String gloss, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, Boolean hidden, Boolean flagged, Boolean logged, ControlledVocabulary controlledVocabulary, ManagedConfiguration mappedShibAttribute, String defaultValue) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, gloss, repeatable, overrideable, enabled, optional, flagged, logged, controlledVocabulary, mappedShibAttribute, defaultValue);
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
        // TODO: can be simplified with BeanUtils
        clone.setFieldPredicate(getFieldPredicate());
        clone.setInputType(getInputType());
        clone.setRepeatable(getRepeatable());
        clone.setOptional(getOptional());
        clone.setHidden(getHidden());
        clone.setLogged(getLogged());
        clone.setUsage(getUsage());
        clone.setHelp(getHelp());
        clone.setGloss(getGloss());
        clone.setControlledVocabulary(getControlledVocabulary());
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
