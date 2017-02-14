package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
@DiscriminatorColumn(name = "FP_TYPE")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "field_predicate_id", "originating_workflow_step_id", "fp_type", "overrideable" }))
public abstract class AbstractFieldProfile<FP> extends BaseEntity {

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = false)
    private FieldPredicate fieldPredicate;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = false)
    private InputType inputType;

    @Column(nullable = false)
    private Boolean repeatable;

    @Column(nullable = false)
    private Boolean optional;

    @Column(nullable = true, name = "`usage`", columnDefinition = "text") // "usage" is a keyword in sql
    private String usage;

    @Column(nullable = true, columnDefinition = "text")
    private String help;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    private List<FieldGloss> fieldGlosses;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    private List<ControlledVocabulary> controlledVocabularies;
    
    @OneToOne(cascade = { REFRESH, MERGE }, fetch = EAGER)
    private Configuration mappedShibAttribute;

    /**
     * @return the fieldPredicate
     */
    public FieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }

    /**
     * @param fieldPredicate
     *            the fieldPredicate to set
     */
    public void setFieldPredicate(FieldPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
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
     * 
     * @return
     */
    public Boolean getOptional() {
        return optional;
    }

    /**
     * 
     * @param optional
     */
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    /**
     * 
     * @return
     */
    public String getUsage() {
        return usage;
    }

    /**
     * 
     * @param usage
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * 
     * @return
     */
    public String getHelp() {
        return help;
    }

    /**
     * 
     * @param help
     */
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
     * 
     * @param String
     *            value
     * @param Language
     *            language
     * @return The field gloss that matches the language, or null if not found
     */
    public FieldGloss getFieldGlossByValueAndLanguage(String value, Language language) {
        for (FieldGloss fieldGloss : getFieldGlosses()) {
            if (fieldGloss.getLanguage().equals(language) && fieldGloss.getValue().equals(value))
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
            if (controlledVocabulary.getName().equals(name))
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

    public void clearControlledVocabulary() {
        this.controlledVocabularies = new ArrayList<ControlledVocabulary>();
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
     * @param controlledVocabularies
     */
    public void addControlledVocabulary(int index, ControlledVocabulary controlledVocabulary) {
        getControlledVocabularies().set(index, controlledVocabulary);
    }

    /**
     * 
     * @param controlledVocabulary
     */
    public void removeControlledVocabulary(ControlledVocabulary controlledVocabulary) {
        getControlledVocabularies().remove(controlledVocabulary);
    }
    
    
    /**
	 * @return the mappedShibAttribute
	 */
	public Configuration getMappedShibAttribute() {
		return mappedShibAttribute;
	}

	/**
	 * @param mappedShibAttribute the mappedShibAttribute to set
	 */
	public void setMappedShibAttribute(Configuration mappedShibAttribute) {
		this.mappedShibAttribute = mappedShibAttribute;
	}

}
