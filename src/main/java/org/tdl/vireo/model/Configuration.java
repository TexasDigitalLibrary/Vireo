package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Jpa specific implementation of Vireo's Configuration interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
public class Configuration extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false, length = 32768) // 2^15
    private String value;

    public Configuration() {
    }

    /**
     * Construct a new JpaConfigurationImpl
     * 
     * @param name
     *            The name of the configuration parameter.
     * @param value
     *            The value of the configuration parameter.
     */
    public Configuration(String name, String value) {
        this.name = name;
        this.value = value;
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
}
