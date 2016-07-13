package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.validation.FieldValueValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FieldValue extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = true)
    private String value;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private FieldPredicate predicate;

    public FieldValue() {
        setModelValidator(new FieldValueValidator());
    }

    /**
     * 
     * @param predicate
     */
    public FieldValue(FieldPredicate predicate) {
        this();
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
