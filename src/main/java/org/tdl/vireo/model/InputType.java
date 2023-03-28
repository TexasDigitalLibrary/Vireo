package org.tdl.vireo.model;

import static javax.persistence.FetchType.EAGER;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;

import org.tdl.vireo.model.response.Views;
import org.tdl.vireo.model.validation.InputTypeValidator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class InputType extends ValidatingBaseEntity {

    @JsonView(Views.SubmissionIndividual.class)
    @Column(unique = true, nullable = false)
    private String name;

    @JsonView(Views.SubmissionIndividual.class)
    @Column
    private String validationPattern;

    @JsonView(Views.SubmissionIndividual.class)
    @Column
    private String validationMessage;

    @JsonView(Views.SubmissionIndividual.class)
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
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the validationPattern
     */
    public String getValidationPattern() {
        return validationPattern;
    }

    /**
     * @param validationPattern the validationPattern to set
     */
    public void setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
    }

    /**
     * @return the validationMessage
     */
    public String getValidationMessage() {
        return validationMessage;
    }

    /**
     * @param validationMessage the validationMessage to set
     */
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    /**
     * Get the validation.
     *
     * @return A map of the validations.
     */
    public Map<String, Validation> getValidation() {
        return validation;
    }

    /**
     * Set the validations map.
     *
     * @param validation The map to replace the current validations with.
     */
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
