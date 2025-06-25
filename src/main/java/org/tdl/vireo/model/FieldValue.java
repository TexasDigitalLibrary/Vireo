package org.tdl.vireo.model;

import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.tdl.vireo.model.response.Views;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FieldValue extends ValidatingBaseEntity {

    @JsonView(Views.SubmissionList.class)
    @Column(columnDefinition = "text", nullable = true, name = "\"value\"")
    private String value;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = true)
    private String identifier;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = true)
    private String definition;

    @JsonView(Views.SubmissionList.class)
    @ElementCollection(fetch = LAZY)
    @Fetch(FetchMode.SELECT)
    private List<String> contacts;

    @JsonView(Views.SubmissionList.class)
    @ManyToOne(optional = false, fetch = LAZY)
    private FieldPredicate fieldPredicate;

    public FieldValue() {
        contacts = new ArrayList<String>();
    }

    /**
     * Initializer.
     *
     * @param fieldPredicate The fieldPredicate to set.
     */
    public FieldValue(FieldPredicate fieldPredicate) {
        this();
        setFieldPredicate(fieldPredicate);
    }

    /**
     * Initializer.
     *
     * @param fieldPredicate The fieldPredicate to set.
     * @param contacts The array of contacts to set.
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
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * @return the contacts
     */
    public List<String> getContacts() {
        return contacts;
    }

    /**
     * @param contacts the contacts to set
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
     * @return the fieldPredicate
     */
    public FieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }

    /**
     * @param fieldPredicate the fieldPredicate to set
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

    @JsonIgnore
    public String getExportFileName() {
        String fullFileName = value.substring(value.lastIndexOf("/") + 1, value.length());
        String fileName = fullFileName;
        if((fullFileName.contains("PRIMARY") && !fullFileName.contains("archived")) || fullFileName.toLowerCase().endsWith(".txt")){
          fileName = fullFileName.substring(fullFileName.indexOf("-") + 1, fullFileName.length());
        }
        return fileName;
    }

}
