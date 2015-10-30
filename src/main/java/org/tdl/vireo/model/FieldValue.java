package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FieldValue extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = true)
    private String value;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private FieldPredicate predicate;

    public FieldValue() { }

    /**
     * 
     * @param predicate
     */
    public FieldValue(FieldPredicate predicate) {
        setPredicate(predicate);
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
     * @return the predicate
     */
    public FieldPredicate getPredicate() {
        return predicate;
    }

    /**
     * @param predicate
     *            the predicate to set
     */
    public void setPredicate(FieldPredicate predicate) {
        this.predicate = predicate;
    }
    
}
