package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * Jpa specific implementation of Vireo's Configuration interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
public class Configuration extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Lob
    @Column(nullable = false)
    private String value;

    @Column(nullable = true, unique = false, length = 255)
    private String type;

    public Configuration() { }

    /**
     * Construct a new JpaConfigurationImpl
     * 
     * @param name
     *            The name of the configuration parameter.
     * @param value
     *            The value of the configuration parameter.
     */
    public Configuration(String name, String value) {
        this();
        this.name = name;
        this.value = value;
        this.type = null;
    }
    
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
}
