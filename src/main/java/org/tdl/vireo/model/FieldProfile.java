package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.tdl.vireo.enums.InputType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@DiscriminatorValue("Org")
public class FieldProfile extends AbstractFieldProfile<FieldProfile> {
    
    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private FieldProfile originatingFieldProfile;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
   
    //@Column(nullable = false)
    private Boolean overrideable;
    
    //@Column(nullable = true)
    private Boolean enabled;
    
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

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate predicate, InputType inputType, ControlledVocabulary controlledVocabulary, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep);
        setPredicate(predicate);
        setInputType(inputType);
		setUsage(usage);
		setHelp(help);
        setRepeatable(repeatable);
        setOverrideable(overrideable);
        setEnabled(enabled);
        setOptional(optional);
        addControlledVocabulary(0, controlledVocabulary);
    }
    
    /**
     * @return the originatingFieldProfile
     */
    public FieldProfile getOriginatingFieldProfile() {
        return originatingFieldProfile;
    }

    /**
     * @param originatingFieldProfile the originatingFieldProfile to set
     */
    public void setOriginatingFieldProfile(FieldProfile originatingFieldProfile) {
        this.originatingFieldProfile = originatingFieldProfile;
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
    
    /**
     * 
     * @return
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 
     * @param enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}