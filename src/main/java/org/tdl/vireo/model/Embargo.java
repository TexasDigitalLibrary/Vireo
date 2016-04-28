package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.tdl.vireo.enums.EmbargoGuarantor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "guarantor", "isSystemRequired" }) )
public class Embargo extends BaseOrderedEntity {

    @Column(nullable = false)
    @NotEmpty
    private String name;

    @Lob
    @Column(nullable = false)
    @NotEmpty
    private String description;

    @Column(nullable = true)
    private Integer duration;

    @Column(nullable = false)
    @JsonProperty("isActive")
    @NotNull
    private Boolean isActive;

    @Column(nullable = false)
    @JsonProperty("isSystemRequired")
    @NotNull
    private Boolean isSystemRequired;

    @Column(nullable = false)
    @NotNull
    private EmbargoGuarantor guarantor;

    /**
     * All new Embargo Types use these values as default
     */
    public Embargo() {
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
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        // if we're the same entity and we have the same ID
        Boolean equalsFromBase = super.equals(obj); 
        if (equalsFromBase) {
            Embargo embargo = (Embargo) obj;
            // if we are valid and we don't have any errors
            if (embargo.getBindingResult() != null && !embargo.getBindingResult().hasErrors()) {
                // if we have the same name, description, duration and guarantor
                if(embargo.getName().equals(this.getName()) && embargo.getDescription().equals(this.getDescription()) && embargo.getGuarantor().equals(this.getGuarantor())){
                    // duration is valid as null
                    Integer tempDuration = embargo.getDuration();
                    if(tempDuration != null) {
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
            // if we're here, incoming embargo didn't contain a binding result! We can't tell if we're a valid embargo or not!
            // INFO: this is only happening during automated testing
            else {
                // let BaseEntity take care of it.
                return equalsFromBase;
            }
        }
        return false;
    }
}
