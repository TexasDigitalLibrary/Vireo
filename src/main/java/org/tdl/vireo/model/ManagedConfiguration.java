package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.interfaces.Configuration;
import org.tdl.vireo.model.validation.ManagedConfigurationValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

/**
 * Jpa specific implementation of Vireo's Configuration interface
 *
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class ManagedConfiguration extends ValidatingBaseEntity implements Configuration {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String value;

    @Column(nullable = false, length = 255)
    private String type;

    /**
     * Construct a new JpaConfigurationImpl
     *
     * By default new ones are not system required.
     */
    public ManagedConfiguration() {
        setModelValidator(new ManagedConfigurationValidator());
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
    public ManagedConfiguration(String name, String value, String type) {
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
     * @param name
     *            the name to set
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
     * @param value
     *            the value to set
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
     * @param type
     *            - the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
