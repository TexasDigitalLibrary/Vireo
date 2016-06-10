package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FieldPredicate extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String value;

    public FieldPredicate() { }

    /**
     * 
     * @param value
     */
    public FieldPredicate(String value) {
        setValue(value);
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

}
