package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
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

    @ElementCollection(fetch = EAGER)
    private List<String> contacts;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private FieldPredicate fieldPredicate;

    public FieldValue() {
        setContacts(new ArrayList<String>());
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
     *
     * @param predicate
     */
    public FieldValue(FieldPredicate fieldPredicate, List<String> contacts) {
        this(fieldPredicate);
        setContacts(contacts);
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
     * 
     * @return
     */
    public List<String> getContacts() {
        return contacts;
    }

    /**
     * 
     * @param contacts
     */
    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
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
