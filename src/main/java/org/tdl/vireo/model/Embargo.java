package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.EmbargoGuarantor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "guarantor", "isSystemRequired" }))
public class Embargo extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 32768) // 2^15
    private String description;

    @Column
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
        isSystemRequired(false);
        isActive(false);
        setGuarantor(EmbargoGuarantor.DEFAULT);
    }

    /**
     * New Embargo Types just need a name, description and duration
     * 
     * @param name
     * @param description
     * @param duration
     */
    public Embargo(String name, String description, Integer duration) {
        this();
        setName(name);
        setDescription(description);
        setDuration(duration);
    }
    
    /**
     * New Embargo Types just need a name, description, duration, and is active
     * 
     * @param name
     * @param description
     * @param duration
     */
    public Embargo(String name, String description, Integer duration, boolean isActive) {
        this(name, description, duration);
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
    public Boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive
     *            the isActive to set
     */
    public void isActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the isSystemRequired
     */
    public Boolean isSystemRequired() {
        return isSystemRequired;
    }

    /**
     * @param isSystemRequired
     *            the isSystemRequired to set
     */
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

}
