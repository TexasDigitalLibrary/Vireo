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
import javax.persistence.OneToOne;

import org.tdl.vireo.inheritence.Heritable;
import org.tdl.vireo.model.validation.FieldProfileValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@DiscriminatorValue("Org")
public class FieldProfile extends AbstractFieldProfile<FieldProfile> implements Heritable<FieldProfile> {
    
    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private FieldProfile originatingFieldProfile;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
   
    @Column(nullable = true)
    private Boolean overrideable;
    
    @Column(nullable = true)
    private Boolean enabled;
    
    @OneToOne(optional = true)
    private DocumentType documentType;
    
    public FieldProfile() {
        setModelValidator(new FieldProfileValidator());
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
     * @param fieldPredicate
     * @param inputType
     * @param repeatable
     * @param enabled
     * @param optional
     */
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep);
        setFieldPredicate(fieldPredicate);
        setInputType(inputType);
        setRepeatable(repeatable);
        setOverrideable(overrideable);
        setEnabled(enabled);
        setOptional(optional);
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
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep, fieldPredicate, inputType, repeatable, overrideable, enabled, optional);
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
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, repeatable, overrideable, enabled, optional);
        setHelp(help);
    }

    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, ControlledVocabulary controlledVocabulary, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional);
        addControlledVocabulary(0, controlledVocabulary);
    }
    
    public FieldProfile(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional, List<ControlledVocabulary> controlledVocabularies, List<FieldGloss> fieldGlosses) {
        this(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional);
        setControlledVocabularies(controlledVocabularies);
        setFieldGlosses(fieldGlosses);
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
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setOriginating(FieldProfile originatingHeritableModel) {
        setOriginatingFieldProfile(originatingHeritableModel);
    }

    @Override
    public FieldProfile getOriginating() {
        return getOriginatingFieldProfile();
    }

    @Override
    public FieldProfile clone() {
        FieldProfile clone = new FieldProfile();
        
        List<ControlledVocabulary> controlledVocabularies = new ArrayList<ControlledVocabulary>();
        for(ControlledVocabulary cv : getControlledVocabularies()) {
            controlledVocabularies.add(cv);
        }
        
        List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();                
        for(FieldGloss fg : getFieldGlosses()) {
            fieldGlosses.add(fg);
        }
       
        clone.setHelp(getHelp());
        clone.setUsage(getUsage());
        clone.setEnabled(getEnabled());
        clone.setOptional(getOptional());
        clone.setRepeatable(getRepeatable());
        
        clone.setOverrideable(getOverrideable());
        
        clone.setInputType(getInputType());
        clone.setFieldPredicate(getFieldPredicate());
        
        clone.setOriginatingFieldProfile(getOriginatingFieldProfile());
        clone.setOriginatingWorkflowStep(getOriginatingWorkflowStep());
        
        clone.setControlledVocabularies(controlledVocabularies);
        
        clone.setFieldGlosses(fieldGlosses);
        
        return clone;
    }

}