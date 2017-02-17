package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.tdl.vireo.model.validation.CustomActionDefinitionValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
public class CustomActionDefinition extends BaseOrderedEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String label;

    @Column(nullable = false)
    @JsonProperty("isStudentVisible")
    private Boolean isStudentVisible;

    public CustomActionDefinition() {
        setModelValidator(new CustomActionDefinitionValidator());
    }

    public CustomActionDefinition(String label, Boolean isStudentVisible) {
        this();
        setLabel(label);
        isStudentVisible(isStudentVisible);
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the isStudentVisible
     */
    @JsonIgnore
    public Boolean isStudentVisible() {
        return isStudentVisible;
    }

    /**
     * @param isStudentVisible the isStudentVisible to set
     */
    @JsonIgnore
    public void isStudentVisible(Boolean isStudentVisible) {
        this.isStudentVisible = isStudentVisible;
    }
}
