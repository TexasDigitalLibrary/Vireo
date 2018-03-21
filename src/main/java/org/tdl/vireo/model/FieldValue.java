package org.tdl.vireo.model;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class FieldValue extends ValidatingBaseEntity {

    @JsonView(ApiView.Partial.class)
    @Column(columnDefinition = "text", nullable = true)
    private String value;

    @Column(nullable = true)
    private String identifier;

    @Column(nullable = true)
    private String definition;

    @ElementCollection(fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    private List<String> contacts;

    @JsonView(ApiView.Partial.class)
    @ManyToOne(optional = false)
    private FieldPredicate fieldPredicate;

    public FieldValue() {
        contacts = new ArrayList<String>();
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

    /**
     * 
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * 
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
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
        this.contacts = new ArrayList<String>();
        if (contacts != null) {
            contacts.forEach(contact -> {
                this.contacts.add(contact.trim());
            });
        }
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
