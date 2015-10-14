package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.EmbargoGuarantor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "guarantor", "isSystemRequired" }) )
public class EmbargoType extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 32768) // 2^15
    private String description;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isSystemRequired;

    @Column(nullable = false)
    private EmbargoGuarantor guarantor;

    /**
     * All new Embargo Types use these values as default
     */
    public EmbargoType() {
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
    public EmbargoType(String name, String description, Integer duration) {
        this();
        setName(name);
        setDescription(description);
        setDuration(duration);
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
