package org.tdl.vireo.model;

import static javax.persistence.FetchType.EAGER;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;

import org.tdl.vireo.model.validation.InputTypeValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class InputType extends ValidatingBaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String validationPattern;

    @Column
    private String validationMessage;

    @ElementCollection(fetch = EAGER)
    @MapKeyColumn(name = "property")
    @Column(name = "validation")
    private Map<String, Validation> validation;

    // TODO: use Map of validations for all input types.

    // TODO: add url for fetching data, controlled vocabulary, or additional logic if needed

    public InputType() {
        setModelValidator(new InputTypeValidator());

        validation = new HashMap<String, Validation>();
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

    public String getValidationPattern() {
        return validationPattern;
    }

    public void setValidationPattern(String pattern) {
        validationPattern = pattern;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String message) {
        validationMessage = message;
    }

    public Map<String, Validation> getValidation() {
        return validation;
    }

    public void setValidation(Map<String, Validation> validation) {
        this.validation = validation;
    }

    public String getValidationPattern(String mapName) {
        String pattern = null;
        if (validation.containsKey(mapName)) {
            Validation val = validation.get(mapName);
            pattern = val.getPattern();
        }
        return pattern;
    }

    public void setValidationPattern(String pattern, String mapName) {
        Validation val = validation.get(mapName);
        val.setPattern(pattern);
        validation.put(mapName, val);
    }

    public String getValidationMessage(String mapName) {
        String message = null;
        if (validation.containsKey(mapName)) {
            Validation val = validation.get(mapName);
            message = val.getMessage();
        }
        return message;
    }

    public void setValidationMessage(String message, String mapName) {
        Validation val = validation.get(mapName);
        val.setMessage(message);
        validation.put(mapName, val);
    }
}
