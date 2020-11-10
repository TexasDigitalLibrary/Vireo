package org.tdl.vireo.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonInclude;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@Inheritance
@DiscriminatorColumn(name = "FP_TYPE")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "originating_id", "originating_workflow_step_id", "field_predicate_id", "fp_type", "overrideable" }))
public abstract class AbstractFieldProfile<FP> extends ValidatingBaseEntity {

    @ManyToOne(fetch = EAGER, optional = false)
    private FieldPredicate fieldPredicate;

    @ManyToOne(fetch = EAGER, optional = false)
    private InputType inputType;

    @Column(nullable = false)
    private Boolean repeatable;

    @Column(nullable = false)
    private Boolean optional;

    @Column(nullable = false)
    private Boolean hidden;

    @Column(nullable = false)
    private Boolean logged;

    @Column(nullable = true, name = "`usage`", columnDefinition = "text") // "usage" is a keyword in sql
    private String usage;

    @Column(nullable = true, columnDefinition = "text")
    private String help;

    @Column(nullable = false)
    private String gloss;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ManyToOne(cascade = { REFRESH }, fetch = EAGER)
    private ControlledVocabulary controlledVocabulary;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ManyToOne(cascade = { REFRESH }, fetch = EAGER)
    private ManagedConfiguration mappedShibAttribute;

    @Column(nullable = true)
    private Boolean flagged;

    @Column(columnDefinition = "text", nullable = true)
    private String defaultValue;

    @Column(nullable = true)
    private Boolean enabled;

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
     * @return
     */
    public Boolean getOptional() {
        return optional;
    }

    /**
     * @param optional
     */
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    /**
     * @return
     */
    public Boolean getHidden() {
        return hidden;
    }

    /**
     * @param hidden
     */
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @return
     */
    public Boolean getLogged() {
        return logged;
    }

    /**
     * @param logged
     */
    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

    /**
     * @return
     */
    public String getUsage() {
        return usage;
    }

    /**
     * @param usage
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * @return
     */
    public String getHelp() {
        return help;
    }

    /**
     * @param help
     */
    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * @return
     */
    public String getGloss() {
        return gloss;
    }

    /**
     * @param gloss
     */
    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public ControlledVocabulary getControlledVocabulary() {
        return controlledVocabulary;
    }

    public void setControlledVocabulary(ControlledVocabulary controlledVocabulary) {
        this.controlledVocabulary = controlledVocabulary;
    }

    /**
     * @return the flagged
     */
    public Boolean getFlagged() {
        return flagged;
    }

    /**
     * @param flagged
     *            the flagged to set
     */
    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * @return
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    /**
     * @return the mappedShibAttribute
     */
    public ManagedConfiguration getMappedShibAttribute() {
        return mappedShibAttribute;
    }

    /**
     * @param mappedShibAttribute
     *            the mappedShibAttribute to set
     */
    public void setMappedShibAttribute(ManagedConfiguration mappedShibAttribute) {
        this.mappedShibAttribute = mappedShibAttribute;
    }

}
