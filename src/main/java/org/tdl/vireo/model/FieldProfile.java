package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.InputType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "predicate_id", "originating_workflow_step_id" }) )
public class FieldProfile extends BaseEntity {

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = false)
    private FieldPredicate predicate;
    
    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;

    @Enumerated
    @Column(nullable = false)
    private InputType inputType;

    @Column(nullable = false)
    private Boolean repeatable;

    @Column(nullable = false)
    private Boolean enabled;
    
    @Column(nullable = false)
    private Boolean optional;
    
    @Column(nullable = false)
    private Boolean overrideable;    
    
    @Lob
    @Column(nullable = true, name = "`usage`") // "usage" is a keyword in sql
    private String usage;
    
    @Lob
    @Column(nullable = true)
    private String help;

    @ManyToMany(cascade = { REFRESH }, fetch = LAZY)
    private List<FieldGloss> fieldGlosses;

    @ManyToMany(cascade = { REFRESH }, fetch = LAZY)
    private List<ControlledVocabulary> controlledVocabularies;
    
    public FieldProfile() {
        setRepeatable(false);
        setEnabled(false);
        setOptional(true);
        setFieldGlosses(new ArrayList<FieldGloss>());
        setControlledVocabularies(new ArrayList<ControlledVocabulary>());
    }
    
    public FieldProfile(WorkflowStep originatingWorkflowStep) {
        this();
        setOriginatingWorkflowStep(originatingWorkflowStep);
    }

    /**
     * 
     * @param predicate
     * @param inputType
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate predicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep);
        setPredicate(predicate);
        setInputType(inputType);
        setRepeatable(repeatable);
        setOverrideable(overrideable);
        setEnabled(enabled);
        setOptional(optional);
    }
    
    /**
     * 
     * @param predicate
     * @param inputType
     * @param usage
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate predicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep, predicate, inputType, repeatable, overrideable, enabled, optional);
        setUsage(usage);
    }
    
    /**
     * 
     * @param predicate
     * @param inputType
     * @param usage
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate predicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep, predicate, inputType, usage, repeatable, overrideable, enabled, optional);
        setHelp(help);
    }

    /**
     * @return the predicate
     */
    public FieldPredicate getPredicate() {
        return predicate;
    }

    /**
     * @param predicate
     *            the predicate to set
     */
    public void setPredicate(FieldPredicate predicate) {
        this.predicate = predicate;
    }
    
    /**
     * @return the originatingWorkflowStep
     */
    public WorkflowStep getOriginatingWorkflowStep() {
        return originatingWorkflowStep;
    }

    /**
     * @param originatingWorkflowStep the originatingWorkflowStep to set
     */
    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        this.originatingWorkflowStep = originatingWorkflowStep;
    }

    /**
     * @return the inputType
     */
    public InputType getInputType() {
        return inputType;
    }

    /**
     * @param inputType
     *            the inputType to set
     */
    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    /**
     * @return the repeatable
     */
    public Boolean getRepeatable() {
        return repeatable;
    }

    /**
     * @param repeatable
     *            the repeatable to set
     */
    public void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }

    /**
     * @return the overrideable
     */
    public Boolean getOverrideable() {
        return overrideable;
    }

    /**
     * @param overrideable the overrideable to set
     */
    public void setOverrideable(Boolean overrideable) {
        this.overrideable = overrideable;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }
    
    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * @return the fieldGlosses
     */
    public List<FieldGloss> getFieldGlosses() {
        return fieldGlosses;
    }

    /**
     * 
     * @param Language
     *            language
     * @return The field gloss that matches the language, or null if not found
     */
    public FieldGloss getFieldGlossByLanguage(Language language) {
        for (FieldGloss fieldGloss : getFieldGlosses()) {
            if (fieldGloss.getLanguage().equals(language))
                return fieldGloss;
        }
        return null;
    }

    /**
     * @param fieldGlosses
     *            the fieldGlosses to set
     */
    public void setFieldGlosses(List<FieldGloss> fieldGlosses) {
        this.fieldGlosses = fieldGlosses;
    }
    
    // TODO : Restrict multiple field gloss with the same language

    /**
     * 
     * @param fieldGloss
     */
    public void addFieldGloss(FieldGloss fieldGloss) {
        getFieldGlosses().add(fieldGloss);
    }

    /**
     * 
     * @param fieldGloss
     */
    public void removeFieldGloss(FieldGloss fieldGloss) {
        getFieldGlosses().remove(fieldGloss);
    }

    /**
     * @return the controlledVocabularies
     */
    public List<ControlledVocabulary> getControlledVocabularies() {
        return controlledVocabularies;
    }

    /**
     * 
     * @param id
     * @return The controlled vocabulary that matches the id, or null if not found
     */
    public ControlledVocabulary getControlledVocabularyById(long id) {
        for (ControlledVocabulary controlledVocabulary : controlledVocabularies) {
            if (controlledVocabulary.getId() == id)
                return controlledVocabulary;
        }
        return null;
    }
    
    /**
     * 
     * @param id
     * @return The controlled vocabulary that matches the name, or null if not found
     */
    public ControlledVocabulary getControlledVocabularyByName(String name) {
        for (ControlledVocabulary controlledVocabulary : controlledVocabularies) {
            if (controlledVocabulary.getName() == name)
                return controlledVocabulary;
        }
        return null;
    }

    /**
     * @param controlledVocabularies
     *            the controlledVocab to set
     */
    public void setControlledVocabularies(List<ControlledVocabulary> controlledVocabularies) {
        this.controlledVocabularies = controlledVocabularies;
    }

    // TODO : Restrict multiple controlled vocabulary with the same language 
    
    /**
     * 
     * @param controlledVocabularies
     */
    public void addControlledVocabulary(ControlledVocabulary controlledVocabulary) {
        getControlledVocabularies().add(controlledVocabulary);
    }

    /**
     * 
     * @param controlledVocabulary
     */
    public void removeControlledVocabulary(ControlledVocabulary controlledVocabulary) {
        getControlledVocabularies().remove(controlledVocabulary);
    }
    
}
