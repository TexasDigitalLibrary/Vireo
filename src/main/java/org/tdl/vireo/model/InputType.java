package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.InputTypeValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class InputType extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String validationPatern;

    @Column
    private String validationMessage;

    // TODO: add Map of validations for input type

    // TODO: add url for fetching data, controlled vocabulary, or additional logic if needed

    public InputType() {
        setModelValidator(new InputTypeValidator());
    }

    public InputType(String name) {
        this();
        setName(name);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param text
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getValidationPatern() {
        return validationPatern;
    }

    public void setValidationPatern(String patern) {
        validationPatern = patern;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String message) {
        validationMessage = message;
    }

}