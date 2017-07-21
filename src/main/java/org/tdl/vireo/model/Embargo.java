package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.validation.EmbargoValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "guarantor", "isSystemRequired" }))
public class Embargo extends BaseOrderedEntity implements EntityControlledVocabulary {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = true)
    private Integer duration;

    @Column(nullable = false)
    @JsonProperty("isActive")
    private Boolean isActive;

    @Column(nullable = false)
    @JsonProperty("isSystemRequired")
    private Boolean isSystemRequired;

    @Column(nullable = false)
    private EmbargoGuarantor guarantor;

    /**
     * All new Embargo Types use these values as default
     */
    public Embargo() {
        setModelValidator(new EmbargoValidator());
        isSystemRequired(false);
    }

    /**
     * New Embargo Types just need a name, description and duration
     *
     * @param name
     * @param description
     * @param duration
     */
    public Embargo(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive) {
        this();
        setName(name);
        setDescription(description);
        setDuration(duration);
        setGuarantor(guarantor);
        isActive(isActive);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * @return the isActive
     */
    @JsonIgnore
    public Boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive
     *            the isActive to set
     */
    @JsonIgnore
    public void isActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the isSystemRequired
     */
    @JsonIgnore
    public Boolean isSystemRequired() {
        return isSystemRequired;
    }

    /**
     * @param isSystemRequired
     *            the isSystemRequired to set
     */
    @JsonIgnore
    public void isSystemRequired(Boolean isSystemRequired) {
        this.isSystemRequired = isSystemRequired;
    }

    /**
     * @return the guarantor
     */
    public EmbargoGuarantor getGuarantor() {
        return guarantor;
    }

    /**
     * @param guarantor
     *            the guarantor to set
     */
    public void setGuarantor(EmbargoGuarantor guarantor) {
        this.guarantor = guarantor;
    }

    /**
     * Assumes that the incoming Object embargo is @Valid
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        // if we're the same entity and we have the same ID
        if (super.equals(obj)) {
            Embargo embargo = (Embargo) obj;
            // if we have the same name, description, duration and guarantor
            if (embargo.getName().equals(this.getName()) && embargo.getDescription().equals(this.getDescription()) && embargo.getGuarantor().equals(this.getGuarantor())) {
                // duration is valid as null
                Integer tempDuration = embargo.getDuration();
                if (tempDuration != null) {
                    return tempDuration.equals(this.getDuration());
                } else {
                    return tempDuration == this.getDuration();
                }
            }
            // we're not equal
            else {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getControlledName() {
        return guarantor.name();
    }

    @Override
    public String getControlledDefinition() {
        return description;
    }

    @Override
    public String getControlledIdentifier() {
        return "";
    }

    @Override
    public List<String> getControlledContacts() {
        return new ArrayList<String>();
    }

}
