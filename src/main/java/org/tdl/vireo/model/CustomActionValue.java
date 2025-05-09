package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.response.Views;
import org.tdl.vireo.model.validation.CustomActionValueValidator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

/**
 * The value of a custom action associated with a particular submission.
 *
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CustomActionValue extends ValidatingBaseEntity {

    @JsonView(Views.SubmissionList.class)
    @ManyToOne(optional = false)
    private CustomActionDefinition definition;

    @JsonView(Views.SubmissionList.class)
    @Column(nullable = false, name = "\"value\"")
    private Boolean value;

    public CustomActionValue() {
        setModelValidator(new CustomActionValueValidator());
    }

    public CustomActionValue(CustomActionDefinition definition, Boolean value) {
        this();
        setDefinition(definition);
        setValue(value);
    }

    /**
     * @return the definition
     */
    public CustomActionDefinition getDefinition() {
        return definition;
    }

    /**
     * @param definition
     *            the definition to set
     */
    public void setDefinition(CustomActionDefinition definition) {
        this.definition = definition;
    }

    /**
     * @return the value
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Boolean value) {
        this.value = value;
    }

}
