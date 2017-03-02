package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FieldValue extends BaseEntity {

    @Column(columnDefinition = "text", nullable = true)
    private String value;

    @Column(nullable = true)
    private String identifier;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private FieldPredicate fieldPredicate;

    public FieldValue() {
    }

    /**
     * 
     * @param predicate
     */
    public FieldValue(FieldPredicate fieldPredicate) {
        this();
        setFieldPredicate(fieldPredicate);
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the predicate
     */
    public FieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }

    /**
     * @param predicate
     *            the predicate to set
     */
    public void setFieldPredicate(FieldPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }

    @JsonIgnore
    public String getFileName() {
        String fullFileName = value.substring(value.lastIndexOf("/") + 1, value.length());
        String fileName = fullFileName.substring(fullFileName.indexOf("-") + 1, fullFileName.length());
        return fileName;
    }

}
