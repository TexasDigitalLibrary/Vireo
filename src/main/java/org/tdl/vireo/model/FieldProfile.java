package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.InputType;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "predicate_id" }) )
public class FieldProfile extends BaseEntity {

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private FieldPredicate predicate;

    @Enumerated
    @Column(nullable = false)
    private InputType inputType;

    @Column(nullable = false)
    private Boolean repeatable;

    @Column(nullable = false)
    private Boolean required;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = LAZY)
    private Set<FieldGloss> fieldGlosses;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = LAZY)
    private Set<ControlledVocabulary> controlledVocabularies;
    
    public FieldProfile() {
        setFieldGlosses(new TreeSet<FieldGloss>());
        setControlledVocabularies(new TreeSet<ControlledVocabulary>());
    }

    /**
     * 
     * @param predicate
     * @param inputType
     * @param repeatable
     * @param required
     */
    public FieldProfile(FieldPredicate predicate, InputType inputType, Boolean repeatable, Boolean required) {
        this();
        setPredicate(predicate);
        setInputType(inputType);
        setRepeatable(repeatable);
        setRequired(required);
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
     * @return the required
     */
    public Boolean getRequired() {
        return required;
    }

    /**
     * @param required
     *            the required to set
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * @return the fieldGlosses
     */
    public Set<FieldGloss> getFieldGlosses() {
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
    public void setFieldGlosses(Set<FieldGloss> fieldGlosses) {
        this.fieldGlosses = fieldGlosses;
    }
    
    // TODO : Restrict multiple field gloss with the same language
    // Could a field gloss with different values and the same language be added to this set?

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
    public Set<ControlledVocabulary> getControlledVocabularies() {
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
    public void setControlledVocabularies(Set<ControlledVocabulary> controlledVocabularies) {
        this.controlledVocabularies = controlledVocabularies;
    }

    // TODO : Restrict multiple controlled vocabulary with the same language
    // Could a controlled vocabulary with different names and the same language be added to this set?
    
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
