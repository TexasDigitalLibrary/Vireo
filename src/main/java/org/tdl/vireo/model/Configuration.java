package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jpa specific implementation of Vireo's Configuration interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "isSystemRequired" }))
public class Configuration extends BaseEntity {

    @Column(nullable = false, length = 255)
    @NotNull
    @Size(max=255)
    @NotEmpty
    private String name;

    @Lob
    @Column(nullable = false)
    @NotNull
    private String value;

    @Column(nullable = false, length = 255)
    @NotNull
    @Size(max=255)
    @NotEmpty
    private String type;
    
    @Column(nullable = false)
    @JsonProperty("isSystemRequired")
    @NotNull
    private Boolean isSystemRequired;

    /**
     * Construct a new JpaConfigurationImpl
     * 
     * By default new ones are not system required.
     */
    public Configuration() {
        isSystemRequired(false);
    }

    /**
     * Construct a new JpaConfigurationImpl
     * 
     * @param name
     *            The name of the configuration parameter.
     * @param value
     *            The value of the configuration parameter.
     * @param type
     *            The type of the configuration parameter.            
     */
    public Configuration(String name, String value, String type) {
        this();
        this.name = name;
        this.value = value;
        this.type = type;
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type - the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the isSystemRequired
     */
    @JsonIgnore
    public Boolean isSystemRequired() {
        return isSystemRequired;
    }

    /**
     * @param isSystemRequired the isSystemRequired to set
     */
    @JsonIgnore
    public void isSystemRequired(Boolean isSystemRequired) {
        this.isSystemRequired = isSystemRequired;
    }
}
